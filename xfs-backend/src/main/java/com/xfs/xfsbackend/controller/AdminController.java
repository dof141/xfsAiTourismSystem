package com.xfs.xfsbackend.controller;

import com.xfs.xfsbackend.common.Result;
import com.xfs.xfsbackend.entity.SysAdmin;
import com.xfs.xfsbackend.service.SysAdminService;
import com.xfs.xfsbackend.utils.JwtUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/admin")
public class AdminController {

    @Autowired
    private SysAdminService sysAdminService;

    @Autowired
    private JwtUtils jwtUtils;

    @PostMapping("/login")
    public Result<Map<String, Object>> login(@RequestBody SysAdmin loginAdmin) {
        // 1. 查询数据库是否存在该用户
        SysAdmin admin = sysAdminService.lambdaQuery()
                .eq(SysAdmin::getUsername, loginAdmin.getUsername())
                .eq(SysAdmin::getPassword, loginAdmin.getPassword()) // 建议以后加盐加密
                .one();

        if (admin == null) {
            return Result.error("用户名或密码错误");
        }

        // 2. 生成 JWT 令牌
        String token = jwtUtils.generateToken(admin.getId(), admin.getUsername());

        // 3. 封装返回数据
        Map<String, Object> result = new HashMap<>();
        result.put("token", token);
        result.put("admin", admin);

        return Result.success(result);
    }
}
