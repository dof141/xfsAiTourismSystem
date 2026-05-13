package com.xfs.xfsbackend.service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xfs.xfsbackend.entity.TimeSlot;
import com.xfs.xfsbackend.mapper.TimeSlotMapper;
import org.springframework.stereotype.Service;

/**
 * 入园时段服务。
 * 继承 MyBatis-Plus 通用 Service，负责读取和维护可预约时间段。
 */
@Service
public class TimeSlotService extends ServiceImpl<TimeSlotMapper, TimeSlot> {
}
