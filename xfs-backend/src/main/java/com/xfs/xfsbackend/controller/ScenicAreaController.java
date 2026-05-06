package com.xfs.xfsbackend.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.xfs.xfsbackend.common.Result;
import com.xfs.xfsbackend.entity.ScenicArea;
import com.xfs.xfsbackend.service.ScenicAreaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
//景点相关的控制器
@RestController
@RequestMapping("/api/area")
public class ScenicAreaController {

    @Autowired
    private ScenicAreaService scenicAreaService;

    /**
     * 返回所有的景点信息
     * @return
     */
    @GetMapping("/list")
    public Result<List<ScenicArea>> getAreaList() {
        List<ScenicArea> list = scenicAreaService.list();
        return Result.success(list);
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