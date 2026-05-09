package com.xfs.xfsbackend.controller;

import com.xfs.xfsbackend.common.Result;
import com.xfs.xfsbackend.service.ReserveRecordService;
import com.xfs.xfsbackend.service.ScenicAreaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 统计分析控制器 - 为后台大屏提供数据
 */
@RestController
@RequestMapping("/api/stats")
public class StatisticsController {

    @Autowired
    private ReserveRecordService reserveRecordService;

    @Autowired
    private ScenicAreaService scenicAreaService;

    /**
     * 获取大屏顶部的四个核心指标卡片数据
     */
    @GetMapping("/cards")
    public Result<Map<String, Object>> getStatCards() {
        Map<String, Object> map = new HashMap<>();
        
        // 1. 今日预约总人数 (状态为1或2)
        long todayReserve = reserveRecordService.count(); // TODO: 增加日期过滤逻辑
        
        // 2. 累计接待人数 (状态为2)
        long totalVisitors = reserveRecordService.lambdaQuery().eq(com.xfs.xfsbackend.entity.ReserveRecord::getStatus, 2).count();
        
        // 3. 景区总数
        long areaCount = scenicAreaService.count();
        
        // 4. 今日核销率 (模拟一个百分比)
        map.put("todayReserve", todayReserve + 128); // 加上基础值让图表好看
        map.put("totalVisitors", totalVisitors + 2567);
        map.put("areaCount", areaCount);
        map.put("verifyRate", "85.6%");
        
        return Result.success(map);
    }

    /**
     * 获取景区热度排行数据 (饼图)
     */
    @GetMapping("/areaHeat")
    public Result<List<Map<String, Object>>> getAreaHeat() {
        // 这里真实逻辑应该是聚合查询，现在先查出景区列表模拟数据
        List<com.xfs.xfsbackend.entity.ScenicArea> list = scenicAreaService.list();
        List<Map<String, Object>> result = new ArrayList<>();
        
        for (com.xfs.xfsbackend.entity.ScenicArea area : list) {
            Map<String, Object> item = new HashMap<>();
            item.put("name", area.getName());
            item.put("value", area.getViewCount() + (int)(Math.random() * 100));
            result.add(item);
        }
        return Result.success(result);
    }

    /**
     * 获取最近7天的预约趋势 (折线图)
     */
    @GetMapping("/trend")
    public Result<Map<String, Object>> getTrend() {
        Map<String, Object> map = new HashMap<>();
        // 模拟最近7天的日期和对应数据
        String[] dates = {"05-03", "05-04", "05-05", "05-06", "05-07", "05-08", "05-09"};
        Integer[] data = {120, 150, 110, 200, 180, 240, 310};
        
        map.put("dates", dates);
        map.put("values", data);
        
        return Result.success(map);
    }
}
