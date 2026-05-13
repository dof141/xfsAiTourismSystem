package com.xfs.xfsbackend.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.xfs.xfsbackend.common.Result;
import com.xfs.xfsbackend.entity.TouristUser;
import com.xfs.xfsbackend.service.TouristUserService;
import com.xfs.xfsbackend.utils.JwtUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;
import java.util.regex.Pattern;

/**
 * 游客登录认证控制器。
 * 负责手机号验证码发送、验证码校验、游客账号自动创建和游客 JWT Token 签发。
 */
@RestController
@RequestMapping("/api/tourist")
public class TouristAuthController {

    private static final Pattern PHONE_PATTERN = Pattern.compile("^1[3-9]\\d{9}$");
    private static final String LOGIN_CODE_PREFIX = "tourist_login_code:";

    @Autowired
    private JwtUtils jwtUtils;

    @Autowired
    private TouristUserService touristUserService;

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @Value("${xfs.sms.mock-enabled:true}")
    private boolean smsMockEnabled;

    /**
     * 发送游客登录验证码。
     * 演示环境可通过 xfs.sms.mock-enabled=true 回显验证码；生产环境应关闭回显，只返回发送成功。
     */
    @PostMapping("/sendCode")
    public Result<String> sendCode(@RequestBody Map<String, String> params) {
        String phone = params.get("phone");
        if (!isValidPhone(phone)) {
            return Result.error("请输入正确的11位手机号");
        }

        String code = String.format("%06d", ThreadLocalRandom.current().nextInt(1000000));
        stringRedisTemplate.opsForValue().set(LOGIN_CODE_PREFIX + phone, code, 5, TimeUnit.MINUTES);
        if (smsMockEnabled) {
            return Result.success("验证码已发送：" + code);
        }
        //这里先在控制台显示输出验证码，方便我的测试
        System.out.println("验证码：" + code);
        return Result.success("验证码已发送");
    }

    /**
     * 游客手机号验证码登录接口。
     * 校验 Redis 中的验证码后，按手机号查询或创建游客账号，并返回游客 JWT Token。
     */
    @PostMapping("/login")
    public Result<Map<String, Object>> login(@RequestBody Map<String, String> params) {
        String phone = params.get("phone");
        String code = params.get("code");
        if (!isValidPhone(phone)) {
            return Result.error("请输入正确的11位手机号");
        }
        if (code == null || !code.matches("^\\d{6}$")) {
            return Result.error("请输入6位验证码");
        }

        String codeKey = LOGIN_CODE_PREFIX + phone;
        String cachedCode = stringRedisTemplate.opsForValue().get(codeKey);
        if (cachedCode == null) {
            return Result.error("验证码已过期，请重新获取");
        }
        if (!cachedCode.equals(code)) {
            return Result.error("验证码错误");
        }

        QueryWrapper<TouristUser> wrapper = new QueryWrapper<>();
        wrapper.eq("phone", phone);
        TouristUser tourist = touristUserService.getOne(wrapper, false);

        if (tourist == null) {
            tourist = new TouristUser();
            tourist.setPhone(phone);
            tourist.setNickname("雪峰游客" + phone.substring(7));
            tourist.setOpenid("phone_" + phone);
            touristUserService.save(tourist);
        }
        stringRedisTemplate.delete(codeKey);

        String token = jwtUtils.generateToken(tourist.getId(), tourist.getNickname(), "tourist");

        Map<String, Object> result = new HashMap<>();
        result.put("token", token);
        result.put("touristId", tourist.getId());
        result.put("nickname", tourist.getNickname());
        result.put("phone", tourist.getPhone());

        return Result.success(result);
    }

    /**
     * 校验手机号格式。
     * 当前规则要求中国大陆 11 位手机号，且第二位为 3-9。
     */
    private boolean isValidPhone(String phone) {
        return phone != null && PHONE_PATTERN.matcher(phone).matches();
    }
}
