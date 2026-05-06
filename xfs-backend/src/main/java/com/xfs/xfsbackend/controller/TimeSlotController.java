package com.xfs.xfsbackend.controller;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xfs.xfsbackend.common.Result;
import com.xfs.xfsbackend.entity.TimeSlot;
import com.xfs.xfsbackend.mapper.TimeSlotMapper;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/slot")
public class TimeSlotController extends ServiceImpl<TimeSlotMapper, TimeSlot> {

    @GetMapping("/list")
    public Result<List<TimeSlot>> getSlotList() {
        return Result.success(this.list());
    }
}