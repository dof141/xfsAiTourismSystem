package com.xfs.xfsbackend.controller;

import com.xfs.xfsbackend.common.Result;
import com.xfs.xfsbackend.entity.TimeSlot;
import com.xfs.xfsbackend.service.TimeSlotService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 入园时段控制器。
 * 提供可预约时段列表，游客预约页根据该接口展示可选时间段。
 */
@RestController
@RequestMapping("/api/slot")
public class TimeSlotController {

    @Autowired
    private TimeSlotService timeSlotService;

    /**
     * 获取所有入园时段。
     * 当前时段数据来自 time_slot 表，每个时段包含名称和最大预约人数。
     */
    @GetMapping("/list")
    public Result<List<TimeSlot>> getSlotList() {
        return Result.success(timeSlotService.list());
    }
}
