package com.xfs.xfsbackend.controller;

import com.xfs.xfsbackend.common.Result;
import com.xfs.xfsbackend.config.AdminRequired;
import com.xfs.xfsbackend.entity.ReserveRecord;
import com.xfs.xfsbackend.entity.ScenicArea;
import com.xfs.xfsbackend.service.ReserveRecordService;
import com.xfs.xfsbackend.service.ScenicAreaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

/**
 * 统计分析控制器。
 * 为后台数据大屏提供预约数量、核销率、景区热度和最近 7 天预约趋势。
 */
@RestController
@RequestMapping("/api/stats")
public class StatisticsController {

    @Autowired
    private ReserveRecordService reserveRecordService;

    @Autowired
    private ScenicAreaService scenicAreaService;

    private static final DateTimeFormatter DATE_FMT = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private static final DateTimeFormatter SHORT_FMT = DateTimeFormatter.ofPattern("MM-dd");

    /**
     * 获取后台大屏顶部核心指标。
     * 包括今日预约数、累计核销数、景区总数和今日核销率。
     */
    @AdminRequired
    @GetMapping("/cards")
    public Result<Map<String, Object>> getStatCards() {
        Map<String, Object> map = new HashMap<>();
        String today = LocalDate.now().format(DATE_FMT);

        // 1. 今日预约数
        long todayReserve = reserveRecordService.lambdaQuery()
                .eq(ReserveRecord::getReserveDate, today)
                .count();

        // 2. 累计核销数（已入园）
        long totalVisitors = reserveRecordService.lambdaQuery()
                .eq(ReserveRecord::getStatus, 2)
                .count();

        // 3. 景区总数
        long areaCount = scenicAreaService.count();

        // 4. 今日核销率
        long todayVerified = reserveRecordService.lambdaQuery()
                .eq(ReserveRecord::getReserveDate, today)
                .eq(ReserveRecord::getStatus, 2)
                .count();
        String verifyRate = todayReserve > 0
                ? String.format("%.1f%%", todayVerified * 100.0 / todayReserve)
                : "0%";

        map.put("todayReserve", todayReserve);
        map.put("totalVisitors", totalVisitors);
        map.put("areaCount", areaCount);
        map.put("verifyRate", verifyRate);

        return Result.success(map);
    }

    /**
     * 获取景区热度排行数据。
     * 按各景区累计预约量生成饼图数据，供 ECharts 渲染。
     */
    @AdminRequired
    @GetMapping("/areaHeat")
    public Result<List<Map<String, Object>>> getAreaHeat() {
        List<ScenicArea> list = scenicAreaService.list();
        List<Map<String, Object>> result = new ArrayList<>();

        for (ScenicArea area : list) {
            long count = reserveRecordService.lambdaQuery()
                    .eq(ReserveRecord::getAreaId, area.getId())
                    .count();
            Map<String, Object> item = new HashMap<>();
            item.put("name", area.getName());
            item.put("value", count);
            result.add(item);
        }
        return Result.success(result);
    }

    /**
     * 获取最近 7 天预约趋势。
     * 返回日期数组和预约量数组，供后台折线图展示。
     */
    @AdminRequired
    @GetMapping("/trend")
    public Result<Map<String, Object>> getTrend() {
        List<String> dates = new ArrayList<>();
        List<Long> values = new ArrayList<>();

        for (int i = 6; i >= 0; i--) {
            String date = LocalDate.now().minusDays(i).format(DATE_FMT);
            String shortDate = LocalDate.now().minusDays(i).format(SHORT_FMT);
            long count = reserveRecordService.lambdaQuery()
                    .eq(ReserveRecord::getReserveDate, date)
                    .count();
            dates.add(shortDate);
            values.add(count);
        }

        Map<String, Object> map = new HashMap<>();
        map.put("dates", dates);
        map.put("values", values);

        return Result.success(map);
    }
}
