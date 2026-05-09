package com.xfs.xfsbackend.controller;

import com.xfs.xfsbackend.common.Result;
import com.xfs.xfsbackend.entity.TimeSlot;
import com.xfs.xfsbackend.service.TimeSlotService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/slot")
public class TimeSlotController {

    @Autowired
    private TimeSlotService timeSlotService;

    @GetMapping("/list")
    public Result<List<TimeSlot>> getSlotList() {
        return Result.success(timeSlotService.list());
    }
}