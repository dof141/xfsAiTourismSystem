package com.xfs.xfsbackend.service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xfs.xfsbackend.entity.TimeSlot;
import com.xfs.xfsbackend.mapper.TimeSlotMapper;
import org.springframework.stereotype.Service;

@Service
public class TimeSlotService extends ServiceImpl<TimeSlotMapper, TimeSlot> {
}
