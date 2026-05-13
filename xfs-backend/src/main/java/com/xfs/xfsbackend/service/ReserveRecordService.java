package com.xfs.xfsbackend.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xfs.xfsbackend.entity.ReserveRecord;
import com.xfs.xfsbackend.entity.ScenicArea;
import com.xfs.xfsbackend.entity.TimeSlot;
import com.xfs.xfsbackend.mapper.ReserveRecordMapper;
import com.xfs.xfsbackend.mapper.ScenicAreaMapper;
import com.xfs.xfsbackend.mapper.TimeSlotMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import java.util.UUID;

/**
 * 预约记录服务。
 * 负责预约参数校验、Redis 库存扣减、订单号/核销码生成、重复预约冲突处理和库存回滚。
 */
@Slf4j
@Service
public class ReserveRecordService extends ServiceImpl<ReserveRecordMapper, ReserveRecord> {

    private static final int STOCK_EXPIRE_DAYS_AFTER_RESERVE = 3;

    @Autowired
    private TimeSlotMapper timeSlotMapper;

    @Autowired
    private ScenicAreaMapper scenicAreaMapper;

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    /**
     * 执行门票预约核心流程。
     * 流程：校验预约参数 -> 初始化/扣减 Redis 库存 -> 生成订单信息 -> 写入数据库。
     * 如果数据库写入失败或触发重复预约唯一约束，会回滚 Redis 库存。
     */
    public String doReserve(ReserveRecord record) {
        String validationMsg = validateReserve(record);
        if (!"success".equals(validationMsg)) {
            return validationMsg;
        }

        String redisKey = buildTicketStockKey(record.getAreaId(), record.getReserveDate(), record.getSlotId());
        TimeSlot slot = timeSlotMapper.selectById(record.getSlotId());
        ensureStockKey(redisKey, record.getReserveDate(), slot);

        Long remainStock = stringRedisTemplate.opsForValue().decrement(redisKey);
        log.info("Deduct ticket stock: key={}, remain={}", redisKey, remainStock);

        if (remainStock != null && remainStock < 0) {
            stringRedisTemplate.opsForValue().increment(redisKey);
            return "手慢啦，当前时段名额已被抢空！";
        }

        record.setOrderNo("XFS" + System.currentTimeMillis() + UUID.randomUUID().toString().substring(0, 4));
        record.setStatus(1);
        record.setPayStatus(1);

        String shortUuid = UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        record.setVerifyCode("V-" + shortUuid);

        try {
            this.save(record);
        } catch (DuplicateKeyException e) {
            rollbackStock(redisKey);
            log.warn("Duplicate reserve rejected and Redis stock rolled back: key={}", redisKey, e);
            return "您已预约过该景区当前日期和时段，请勿重复预约";
        } catch (Exception e) {
            rollbackStock(redisKey);
            if (isDuplicateKeyException(e)) {
                log.warn("Duplicate reserve rejected and Redis stock rolled back: key={}", redisKey, e);
                return "您已预约过该景区当前日期和时段，请勿重复预约";
            }
            log.error("Create reserve record failed and Redis stock rolled back: key={}", redisKey, e);
            return "预约失败，请稍后重试";
        }
        return "success";
    }

    /**
     * 校验预约请求是否合法。
     * 检查用户身份、景区、时段、日期格式、过去日期和重复待入园预约。
     */
    String validateReserve(ReserveRecord record) {
        if (record == null) {
            return "预约信息不能为空";
        }
        if (record.getTouristId() == null) {
            return "预约失败：用户身份信息缺失，请重新登录";
        }
        if (record.getAreaId() == null) {
            return "请选择预约景区";
        }
        if (record.getSlotId() == null) {
            return "请选择入园时段";
        }
        if (record.getReserveDate() == null || record.getReserveDate().trim().isEmpty()) {
            return "请选择游玩日期";
        }

        LocalDate reserveDate;
        try {
            reserveDate = LocalDate.parse(record.getReserveDate());
        } catch (DateTimeParseException e) {
            return "游玩日期格式错误，请使用 yyyy-MM-dd";
        }
        if (reserveDate.isBefore(LocalDate.now())) {
            return "不能预约过去的日期";
        }

        ScenicArea area = scenicAreaMapper.selectById(record.getAreaId());
        if (area == null || !Integer.valueOf(1).equals(area.getStatus())) {
            return "预约景区不存在或已下架";
        }

        TimeSlot slot = timeSlotMapper.selectById(record.getSlotId());
        if (slot == null) {
            return "预约时段不存在！";
        }

        QueryWrapper<ReserveRecord> duplicateWrapper = new QueryWrapper<>();
        duplicateWrapper.eq("tourist_id", record.getTouristId())
                .eq("area_id", record.getAreaId())
                .eq("slot_id", record.getSlotId())
                .eq("reserve_date", record.getReserveDate())
                .eq("status", 1);
        Long duplicateCount = baseMapper.selectCount(duplicateWrapper);
        if (duplicateCount != null && duplicateCount > 0) {
            return "您已预约过该景区当前日期和时段，请勿重复预约";
        }

        return "success";
    }

    /**
     * 构造 Redis 库存 key。
     * 格式：ticket_stock:{景区ID}:{日期}:{时段ID}，确保不同景区库存互不影响。
     */
    private String buildTicketStockKey(Long areaId, String reserveDate, Long slotId) {
        return "ticket_stock:" + areaId + ":" + reserveDate + ":" + slotId;
    }

    /**
     * 确保指定库存 key 已存在。
     * 当 Redis 重启或某日期库存未预热时，会根据时段最大人数自动初始化库存。
     */
    private void ensureStockKey(String redisKey, String reserveDate, TimeSlot slot) {
        if (slot == null) {
            return;
        }
        Boolean hasKey = stringRedisTemplate.hasKey(redisKey);
        if (Boolean.TRUE.equals(hasKey)) {
            return;
        }

        Duration ttl = buildStockTtl(reserveDate);
        Boolean wasSet = stringRedisTemplate.opsForValue()
                .setIfAbsent(redisKey, String.valueOf(slot.getMaxPeople()), ttl);
        if (Boolean.TRUE.equals(wasSet)) {
            log.info("Initialized Redis ticket stock: key={}, stock={}, ttlSeconds={}",
                    redisKey, slot.getMaxPeople(), ttl.getSeconds());
        }
    }

    /**
     * 计算库存 key 的 Redis 过期时间。
     * 过期时间设置到游玩日期之后，避免历史库存 key 长期保留。
     */
    private Duration buildStockTtl(String reserveDate) {
        LocalDate expireDate = LocalDate.parse(reserveDate).plusDays(STOCK_EXPIRE_DAYS_AFTER_RESERVE);
        Duration ttl = Duration.between(LocalDateTime.now(), expireDate.atStartOfDay());
        return ttl.isNegative() || ttl.isZero() ? Duration.ofDays(1) : ttl;
    }

    /**
     * 回滚 Redis 库存。
     * 当数据库写入失败或重复预约冲突时，将之前扣减的库存加回去。
     */
    private void rollbackStock(String redisKey) {
        stringRedisTemplate.opsForValue().increment(redisKey);
    }

    /**
     * 判断异常链中是否包含数据库唯一键冲突。
     * 兼容 Spring 的 DuplicateKeyException 和 MySQL 驱动的唯一约束异常。
     */
    private boolean isDuplicateKeyException(Throwable e) {
        Throwable current = e;
        while (current != null) {
            String className = current.getClass().getName();
            if (className.contains("DuplicateKeyException")
                    || className.contains("MySQLIntegrityConstraintViolationException")) {
                return true;
            }
            current = current.getCause();
        }
        return false;
    }
}
