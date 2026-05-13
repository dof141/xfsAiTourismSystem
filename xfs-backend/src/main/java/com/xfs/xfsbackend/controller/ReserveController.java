package com.xfs.xfsbackend.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.xfs.xfsbackend.common.Result;
import com.xfs.xfsbackend.config.AdminRequired;
import com.xfs.xfsbackend.entity.ReserveRecord;
import com.xfs.xfsbackend.entity.ScenicArea;
import com.xfs.xfsbackend.entity.TimeSlot;
import com.xfs.xfsbackend.service.ReserveRecordService;
import com.xfs.xfsbackend.service.ScenicAreaService;
import com.xfs.xfsbackend.service.TimeSlotService;
import com.xfs.xfsbackend.utils.QrCodeUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import java.util.Date;
import java.util.List;

/**
 * 门票预约与核销控制器。
 * 负责游客预约、我的预约列表、二维码查看、管理员核销以及 Redis 库存预热。
 */
@RestController
@RequestMapping("/api/reserve")
public class ReserveController {

    private static final int STOCK_EXPIRE_DAYS_AFTER_RESERVE = 3;

    @Autowired
    private ReserveRecordService reserveRecordService;

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @Autowired
    private TimeSlotService timeSlotService;

    @Autowired
    private ScenicAreaService scenicAreaService;

    /**
     * 游客提交预约接口。
     * 从登录拦截器写入的 request 属性中读取游客 ID，防止前端伪造 touristId。
     * 预约成功后返回二维码 base64，供游客入园时出示。
     */
    @PostMapping("/add")
    public Result<String> addReserve(@RequestBody ReserveRecord record, HttpServletRequest request) {
        Object userId = request.getAttribute("adminId");
        if (userId == null) {
            return Result.error("请先登录后再预约");
        }
        record.setTouristId(Long.valueOf(userId.toString()));

        String msg = reserveRecordService.doReserve(record);

        if ("success".equals(msg)) {
            String qrCodeBase64 = QrCodeUtil.generateBase64QrCode(record.getOrderNo());
            return Result.success(qrCodeBase64);
        }
        return Result.error(msg);
    }

    /**
     * 管理员核销入园码接口。
     * 支持使用订单号或短核销码查询订单，并通过乐观条件更新避免重复核销。
     */
    @AdminRequired
    @PostMapping("/verify/{code}")
    public Result<String> verifyOrder(@PathVariable String code) {
        QueryWrapper<ReserveRecord> wrapper = new QueryWrapper<>();
        wrapper.eq("order_no", code).or().eq("verify_code", code);

        ReserveRecord record = reserveRecordService.getOne(wrapper, false);

        if (record == null) {
            return Result.error("无效的入园码！未找到相关订单。");
        }
        if (record.getStatus() == 2) {
            return Result.error("该二维码已于 " + record.getUpdateTime() + " 被核销，请勿重复入园！");
        }
        if (record.getStatus() != 1) {
            return Result.error("订单状态异常！");
        }

        boolean updated = reserveRecordService.lambdaUpdate()
                .eq(ReserveRecord::getId, record.getId())
                .eq(ReserveRecord::getStatus, 1)
                .set(ReserveRecord::getStatus, 2)
                .set(ReserveRecord::getUpdateTime, new Date())
                .update();

        if (!updated) {
            return Result.error("该订单已被核销或状态异常，请刷新后重试！");
        }
        return Result.success("核销成功！允许入园。");
    }

    /**
     * 获取当前登录游客的预约记录。
     * 只查询 JWT 中游客 ID 对应的数据，避免用户查看他人的预约订单。
     */
    @GetMapping("/myList")
    public Result<List<ReserveRecord>> getMyList(HttpServletRequest request) {
        Object userIdAttr = request.getAttribute("adminId");
        if (userIdAttr == null) {
            return Result.error("请先登录");
        }
        Long userId = Long.valueOf(userIdAttr.toString());

        QueryWrapper<ReserveRecord> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("tourist_id", userId).orderByDesc("create_time");
        return Result.success(reserveRecordService.list(queryWrapper));
    }

    /**
     * 根据订单号动态生成预约二维码。
     * 管理员可以查看任意订单二维码；游客只能查看自己的预约二维码。
     */
    @GetMapping("/qrcode/{orderNo}")
    public Result<String> getQrCode(@PathVariable String orderNo, HttpServletRequest request) {
        QueryWrapper<ReserveRecord> wrapper = new QueryWrapper<>();
        wrapper.eq("order_no", orderNo);
        ReserveRecord record = reserveRecordService.getOne(wrapper, false);

        if (record == null) {
            return Result.error("预约订单不存在");
        }

        Object role = request.getAttribute("role");
        Object userIdAttr = request.getAttribute("adminId");
        if (!"admin".equals(role)) {
            if (userIdAttr == null || record.getTouristId() == null
                    || !record.getTouristId().toString().equals(userIdAttr.toString())) {
                return Result.error("无权查看该预约二维码");
            }
        }

        String qrCodeBase64 = QrCodeUtil.generateBase64QrCode(orderNo);
        return Result.success(qrCodeBase64);
    }

    /**
     * 管理员手动预热指定日期的 Redis 票务库存。
     * 库存 key 包含景区 ID、日期和时段 ID，避免不同景区共用同一个库存池。
     */
    @AdminRequired
    @GetMapping("/initStock")
    public Result<String> initStock(@RequestParam(required = false) String date) {
        if (date == null || date.isEmpty()) {
            date = LocalDate.now().plusDays(1).toString();
        }
        try {
            LocalDate.parse(date);
        } catch (DateTimeParseException e) {
            return Result.error("日期格式错误，请使用 yyyy-MM-dd");
        }

        List<TimeSlot> slotList = timeSlotService.list();
        if (slotList.isEmpty()) {
            return Result.error("请先配置入园时段！");
        }
        List<ScenicArea> areaList = scenicAreaService.list();
        if (areaList.isEmpty()) {
            return Result.error("请先配置景区");
        }

        int count = 0;
        Duration ttl = buildStockTtl(date);
        for (ScenicArea area : areaList) {
            for (TimeSlot slot : slotList) {
                String redisKey = buildTicketStockKey(area.getId(), date, slot.getId());
                Boolean wasSet = stringRedisTemplate.opsForValue()
                        .setIfAbsent(redisKey, String.valueOf(slot.getMaxPeople()), ttl);
                if (Boolean.TRUE.equals(wasSet)) {
                    count++;
                }
            }
        }
        return Result.success("缓存预热完成！日期：" + date + "，新增库存Key：" + count);
    }

    /**
     * 构造 Redis 库存 key。
     * 格式：ticket_stock:{景区ID}:{日期}:{时段ID}。
     */
    private String buildTicketStockKey(Long areaId, String reserveDate, Long slotId) {
        return "ticket_stock:" + areaId + ":" + reserveDate + ":" + slotId;
    }

    /**
     * 计算库存缓存过期时间。
     * 库存 key 会保留到游玩日期后数天，避免历史日期的库存长期堆积。
     */
    private Duration buildStockTtl(String reserveDate) {
        LocalDate expireDate = LocalDate.parse(reserveDate).plusDays(STOCK_EXPIRE_DAYS_AFTER_RESERVE);
        Duration ttl = Duration.between(LocalDateTime.now(), expireDate.atStartOfDay());
        return ttl.isNegative() || ttl.isZero() ? Duration.ofDays(1) : ttl;
    }
}
