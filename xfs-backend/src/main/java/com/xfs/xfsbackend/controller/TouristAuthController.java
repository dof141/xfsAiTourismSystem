package com.xfs.xfsbackend.controller;

import com.xfs.xfsbackend.common.Result;

import com.xfs.xfsbackend.service.SysAdminService;
import com.xfs.xfsbackend.utils.JwtUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

/**
 * 游客端登录控制器
 */
@RestController
@RequestMapping("/api/tourist")
public class TouristAuthController {

    @Autowired
    private JwtUtils jwtUtils;

    /**
     * 极简登录：仅需手机号即可登录（模拟真实场景）
     */
    @PostMapping("/login")
    public Result<Map<String, Object>> login(@RequestBody Map<String, String> params) {
        String phone = params.get("phone");
        if (phone == null || phone.length() != 11) {
            return Result.error("请输入正确的手机号");
        }

        // 模拟用户ID，实际项目中这里应该查数据库或创建新用户
        Long touristId = 888L;
        String token = jwtUtils.generateToken(touristId, "Tourist_" + phone.substring(7), "tourist");

        Map<String, Object> result = new HashMap<>();
        result.put("token", token);
        result.put("touristId", touristId);
        result.put("nickname", "雪峰游客" + phone.substring(7));

        return Result.success(result);
    }
}
