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

//处理预约相关的服务类
@Service
public class ReserveRecordService extends ServiceImpl<ReserveRecordMapper, ReserveRecord> {

    @Autowired
    private TimeSlotMapper timeSlotMapper;
    @Autowired
    private StringRedisTemplate stringRedisTemplate;
    // 核心预约逻辑：这里以后可以升级为 Redis 高并发防超卖！
    public String doReserve(ReserveRecord record) {
        // 1. 拼接当前请求对应的 Redis 库存 Key
        String redisKey = "ticket_stock:" + record.getReserveDate() + ":" + record.getSlotId();
        //  2. Redis 原子操作扣减库存 (关键点！)
        // decrement 是单线程原子的，10000 个人同时并发执行，它也会乖乖排队，绝对不会错乱。
        Long remainStock = stringRedisTemplate.opsForValue().decrement(redisKey);
        //  3. 判断扣减后的库存
        if (remainStock == null) {
            return "该日期的名额尚未开放或已过期！";
        }

        if (remainStock < 0) {
            // 如果变成负数，说明刚好卖完了。为了保持数据干净，把 -1 加回 0
            stringRedisTemplate.opsForValue().increment(redisKey);
            return "手慢啦，当前时段名额已被抢空！";
        }
        // ====== 以下逻辑只有成功抢到 Redis 名额的幸运儿才能执行 ======


        // 4. 名额充足，生成订单数据入库
        // 生成唯一订单号 (XFS + 时间戳 + 随机串)
        record.setOrderNo("XFS" + System.currentTimeMillis() + UUID.randomUUID().toString().substring(0, 4));
        record.setTouristId(1L); // TODO: 以后做登录了，这里改成实际的微信游客ID
        record.setStatus(1); // 1 待入园状态
        record.setPayStatus(1); // 默认支付成功
//      // 为订单号生成二维码
//        String qrCodeBase64 = QrCodeUtil.generateBase64QrCode(record.getOrderNo());
//        record.setVerifyCode(qrCodeBase64); // 将二维码base码存入数据库
        // ====== 工业级短核销码生成 ======
        // UUID 是全球唯一的，我们截取前 8 位并转为大写，生成类似 "V-A3F8B2C9" 的码
        String shortUuid = UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        record.setVerifyCode("V-" + shortUuid);
        this.save(record);
        return "success"; // 成功标志
    }
}