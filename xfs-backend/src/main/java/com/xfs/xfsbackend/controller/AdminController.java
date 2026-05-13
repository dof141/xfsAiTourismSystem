package com.xfs.xfsbackend.controller;

import com.xfs.xfsbackend.common.Result;
import com.xfs.xfsbackend.entity.SysAdmin;
import com.xfs.xfsbackend.service.SysAdminService;
import com.xfs.xfsbackend.utils.JwtUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

/**
 * 后台管理员认证接口。
 * 负责管理员登录、BCrypt 密码校验，以及签发带角色信息的 JWT Token。
 */
@RestController
@RequestMapping("/api/admin")
public class AdminController {

    @Autowired
    private SysAdminService sysAdminService;

    @Autowired
    private JwtUtils jwtUtils;

    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    /**
     * 管理员登录接口。
     * 前端提交用户名和密码，后端校验通过后返回管理员信息和 JWT Token。
     * 注意：密码字段在 SysAdmin 中配置为只写，响应给前端时不会暴露密码。
     */
    @PostMapping("/login")
    public Result<Map<String, Object>> login(@RequestBody SysAdmin loginAdmin) {
        // 1. 参数校验
        if (loginAdmin.getUsername() == null || loginAdmin.getPassword() == null) {
            return Result.error("用户名和密码不能为空");
        }

        // 2. 根据用户名查询
        SysAdmin admin = sysAdminService.lambdaQuery()
                .eq(SysAdmin::getUsername, loginAdmin.getUsername())
                .one();

        if (admin == null) {
            return Result.error("用户名或密码错误");
        }

        // 3. 使用 BCrypt 校验密码
        if (!passwordEncoder.matches(loginAdmin.getPassword(), admin.getPassword())) {
            return Result.error("用户名或密码错误");
        }

        // 3. 生成带角色信息的 JWT 令牌
        String token = jwtUtils.generateToken(admin.getId(), admin.getUsername(), admin.getRole());

        // 4. 封装返回数据（password 已被 @JsonIgnore 自动排除）
        Map<String, Object> result = new HashMap<>();
        result.put("token", token);
        result.put("admin", admin);

        return Result.success(result);
    }
}
