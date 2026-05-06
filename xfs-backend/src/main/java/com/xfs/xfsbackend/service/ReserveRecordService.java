package com.xfs.xfsbackend.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xfs.xfsbackend.entity.ReserveRecord;
import com.xfs.xfsbackend.entity.TimeSlot;
import com.xfs.xfsbackend.mapper.ReserveRecordMapper;
import com.xfs.xfsbackend.mapper.TimeSlotMapper;
import com.xfs.xfsbackend.utils.QrCodeUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.UUID;

//处理预约相关的服务类
@Service
public class ReserveRecordService extends ServiceImpl<ReserveRecordMapper, ReserveRecord> {

    @Autowired
    private TimeSlotMapper timeSlotMapper;

    // 核心预约逻辑：这里以后可以升级为 Redis 高并发防超卖！
    public String doReserve(ReserveRecord record) {
        // 1. 查询该时段的最大总名额
        TimeSlot timeSlot = timeSlotMapper.selectById(record.getSlotId());
        if (timeSlot == null) {
            return "预约时段不存在";
        }

        // 2. 统计这个景区、这一天、这个时段 已经被人预约了多少个名额？
        QueryWrapper<ReserveRecord> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("area_id", record.getAreaId())
                .eq("reserve_date", record.getReserveDate())
                .eq("slot_id", record.getSlotId())
                .eq("status", 1); // 1表示有效预约
        long alreadyBooked = this.count(queryWrapper);

        // 3. 判断名额是否满了
        if (alreadyBooked >= timeSlot.getMaxPeople()) {
            return "手慢啦，该时段名额已满！";
        }

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
        String shortUuid = java.util.UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        String verifyCode = "V-" + shortUuid;

        record.setVerifyCode(verifyCode);
        this.save(record);
        return "success"; // 成功标志
    }
}