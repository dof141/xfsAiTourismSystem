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

/**
 * 景区与子景点管理控制器。
 * 对游客提供景区列表、热门景区、子景点查询；对管理员提供景区和子景点维护接口。
 */
@RestController
@RequestMapping("/api/area")
public class ScenicAreaController {

    @Autowired
    private ScenicAreaService scenicAreaService;
    
    @Autowired
    private ScenicSpotService scenicSpotService;

    /**
     * 获取所有大景区列表。
     * 游客端首页和管理后台景区表格都使用该接口。
     */
    @GetMapping("/list")
    public Result<List<ScenicArea>> getAreaList() {
        return Result.success(scenicAreaService.list());
    }

    /**
     * 新增或更新大景区信息。
     * 需要管理员权限，前端编辑景区基础资料时调用。
     */
    @AdminRequired
    @PostMapping("/save")
    public Result<String> saveArea(@RequestBody ScenicArea area) {
        scenicAreaService.saveOrUpdate(area);
        return Result.success("保存成功");
    }

    /**
     * 删除指定大景区。
     * 需要管理员权限，当前为物理删除，后续可按需要改为状态下架。
     */
    @AdminRequired
    @DeleteMapping("/{id}")
    public Result<String> deleteArea(@PathVariable Long id) {
        scenicAreaService.removeById(id);
        return Result.success("删除成功");
    }

    /**
     * 获取指定大景区下的子景点列表。
     * 游客查看景区详情和后台管理子景点时都会调用。
     */
    @GetMapping("/{id}/spots")
    public Result<List<ScenicSpot>> getSpotsByArea(@PathVariable Long id) {
        List<ScenicSpot> spots = scenicSpotService.lambdaQuery()
                .eq(ScenicSpot::getAreaId, id)
                .list();
        return Result.success(spots);
    }
    
    /**
     * 新增或更新子景点信息。
     * 需要管理员权限，用于维护景区内部景点、价格、开放时间和图片。
     */
    @AdminRequired
    @PostMapping("/spot/save")
    public Result<String> saveSpot(@RequestBody ScenicSpot spot) {
        scenicSpotService.saveOrUpdate(spot);
        return Result.success("子景点保存成功");
    }

    /**
     * 删除指定子景点。
     * 需要管理员权限，当前为物理删除。
     */
    @AdminRequired
    @DeleteMapping("/spot/{id}")
    public Result<String> deleteSpot(@PathVariable Long id) {
        scenicSpotService.removeById(id);
        return Result.success("删除成功");
    }

    /**
     * 热门景区推荐接口。
     * 根据浏览量倒序取前 3 个景区，用于游客端首页热门推荐区域。
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
