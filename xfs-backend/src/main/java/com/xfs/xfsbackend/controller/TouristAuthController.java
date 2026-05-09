package com.xfs.xfsbackend.controller;

import com.xfs.xfsbackend.common.Result;
import com.xfs.xfsbackend.entity.TouristUser;
import com.xfs.xfsbackend.service.TouristUserService;
import com.xfs.xfsbackend.utils.JwtUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

/**
 * 游客端登录控制器
 */
@RestController
@RequestMapping("/api/tourist")
public class TouristAuthController {

    @Autowired
    private JwtUtils jwtUtils;

    @Autowired
    private TouristUserService touristUserService;

    private static final Pattern PHONE_PATTERN = Pattern.compile("^1[3-9]\\d{9}$");

    /**
     * 极简登录：仅需手机号即可登录
     */
    @PostMapping("/login")
    public Result<Map<String, Object>> login(@RequestBody Map<String, String> params) {
        String phone = params.get("phone");
        if (phone == null || !PHONE_PATTERN.matcher(phone).matches()) {
            return Result.error("请输入正确的11位手机号");
        }

        // 根据手机号查询或创建游客记录
        TouristUser tourist = touristUserService.lambdaQuery()
                .eq(TouristUser::getPhone, phone)
                .one();

        if (tourist == null) {
            tourist = new TouristUser();
            tourist.setPhone(phone);
            tourist.setNickname("雪峰游客" + phone.substring(7));
            tourist.setOpenid("phone_" + phone);
            touristUserService.save(tourist);
        }

        String token = jwtUtils.generateToken(tourist.getId(), tourist.getNickname(), "tourist");

        Map<String, Object> result = new HashMap<>();
        result.put("token", token);
        result.put("touristId", tourist.getId());
        result.put("nickname", tourist.getNickname());
        result.put("phone", tourist.getPhone());

        return Result.success(result);
    }
}
