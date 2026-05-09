package com.xfs.xfsbackend.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.xfs.xfsbackend.common.Result;
import com.xfs.xfsbackend.config.AdminRequired;
import com.xfs.xfsbackend.entity.ScenicArea;
import com.xfs.xfsbackend.entity.ScenicSpot;
import com.xfs.xfsbackend.service.ScenicAreaService;
import com.xfs.xfsbackend.service.ScenicSpotService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

//景点相关的控制器
@RestController
@RequestMapping("/api/area")
public class ScenicAreaController {

    @Autowired
    private ScenicAreaService scenicAreaService;
    
    @Autowired
    private ScenicSpotService scenicSpotService;

    /**
     * 返回所有的景点信息
     */
    @GetMapping("/list")
    public Result<List<ScenicArea>> getAreaList() {
        return Result.success(scenicAreaService.list());
    }

    /**
     * 保存或更新景区
     */
    @AdminRequired
    @PostMapping("/save")
    public Result<String> saveArea(@RequestBody ScenicArea area) {
        scenicAreaService.saveOrUpdate(area);
        return Result.success("保存成功");
    }

    /**
     * 删除景区
     */
    @AdminRequired
    @DeleteMapping("/{id}")
    public Result<String> deleteArea(@PathVariable Long id) {
        scenicAreaService.removeById(id);
        return Result.success("删除成功");
    }

    /**
     * 获取指定景区下的所有子景点
     */
    @GetMapping("/{id}/spots")
    public Result<List<ScenicSpot>> getSpotsByArea(@PathVariable Long id) {
        List<ScenicSpot> spots = scenicSpotService.lambdaQuery()
                .eq(ScenicSpot::getAreaId, id)
                .list();
        return Result.success(spots);
    }
    
    /**
     * 保存或更新子景点
     */
    @AdminRequired
    @PostMapping("/spot/save")
    public Result<String> saveSpot(@RequestBody ScenicSpot spot) {
        scenicSpotService.saveOrUpdate(spot);
        return Result.success("子景点保存成功");
    }

    /**
     * 删除子景点
     */
    @AdminRequired
    @DeleteMapping("/spot/{id}")
    public Result<String> deleteSpot(@PathVariable Long id) {
        scenicSpotService.removeById(id);
        return Result.success("删除成功");
    }

    /**
     * 热门景区推荐（根据浏览量降序，取前3名）
     */

    @GetMapping("/hot")
    public Result<List<ScenicArea>> getHotList() {
        QueryWrapper<ScenicArea> queryWrapper = new QueryWrapper<>();
        // 按 view_count 字段倒序排列
        queryWrapper.orderByDesc("view_count");
        // 只取前 3 个
        queryWrapper.last("LIMIT 3");
        return Result.success(scenicAreaService.list(queryWrapper));
    }
}