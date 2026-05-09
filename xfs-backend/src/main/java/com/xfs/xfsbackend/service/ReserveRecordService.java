package com.xfs.xfsbackend.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xfs.xfsbackend.entity.ReserveRecord;
import com.xfs.xfsbackend.entity.TimeSlot;
import com.xfs.xfsbackend.mapper.ReserveRecordMapper;
import com.xfs.xfsbackend.mapper.TimeSlotMapper;
import com.xfs.xfsbackend.utils.QrCodeUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.UUID;

/**
 * 处理预约相关的服务类
 */
@Service
public class ReserveRecordService extends ServiceImpl<ReserveRecordMapper, ReserveRecord> {

    @Autowired
    private TimeSlotMapper timeSlotMapper;
    
    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    /**
     * 核心预约逻辑：支持自动补仓的 Redis 高并发扣减
     */
    public String doReserve(ReserveRecord record) {
        // 1. 拼接 Redis 库存 Key (格式: ticket_stock:日期:时段ID)
        String redisKey = "ticket_stock:" + record.getReserveDate() + ":" + record.getSlotId();
        
        // 2. 检查 Redis 中是否存在该 Key
        Boolean hasKey = stringRedisTemplate.hasKey(redisKey);
        
        if (Boolean.FALSE.equals(hasKey)) {
            // 如果没有 Key，说明 Redis 重启了或该日期从未初始化过，去数据库查一下
            TimeSlot slot = timeSlotMapper.selectById(record.getSlotId());
            if (slot == null) {
                return "预约时段不存在！";
            }
            // 初始化 Redis 库存（使用 setIfAbsent 保证原子性，防止并发初始化）
            stringRedisTemplate.opsForValue().setIfAbsent(redisKey, String.valueOf(slot.getMaxPeople()));
            System.out.println("=== [DEBUG] 自动初始化 Redis 库存: Key=" + redisKey + ", 数量=" + slot.getMaxPeople());
        }

        // 3. 执行原子扣减
        Long remainStock = stringRedisTemplate.opsForValue().decrement(redisKey);
        System.out.println("=== [DEBUG] 执行扣减: Key=" + redisKey + ", 剩余=" + remainStock);

        // 4. 判断扣减结果
        if (remainStock != null && remainStock < 0) {
            // 如果扣成负数了，说明刚好没票了，把负数加回去保持为 0
            stringRedisTemplate.opsForValue().increment(redisKey);
            return "手慢啦，当前时段名额已被抢空！";
        }

        // ====== 5. 扣减成功，执行订单入库逻辑 ======
        if (record.getTouristId() == null) {
            return "预约失败：用户身份信息缺失，请重新登录";
        }
        // 生成唯一订单号
        record.setOrderNo("XFS" + System.currentTimeMillis() + UUID.randomUUID().toString().substring(0, 4));
        record.setStatus(1); // 1 待入园状态
        record.setPayStatus(1); // 默认支付成功
        
        // 生成工业级短核销码
        String shortUuid = UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        record.setVerifyCode("V-" + shortUuid);
        
        this.save(record);
        return "success"; 
    }
}
