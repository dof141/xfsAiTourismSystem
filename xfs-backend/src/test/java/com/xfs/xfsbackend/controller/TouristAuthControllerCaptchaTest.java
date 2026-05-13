package com.xfs.xfsbackend.controller;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.xfs.xfsbackend.common.Result;
import com.xfs.xfsbackend.entity.TouristUser;
import com.xfs.xfsbackend.service.TouristUserService;
import com.xfs.xfsbackend.utils.JwtUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TouristAuthControllerCaptchaTest {

    private TouristAuthController touristAuthController;

    @Mock
    private TouristUserService touristUserService;

    @Mock
    private StringRedisTemplate stringRedisTemplate;

    @Mock
    private ValueOperations<String, String> valueOperations;

    @BeforeEach
    void setUp() {
        JwtUtils jwtUtils = new JwtUtils();
        ReflectionTestUtils.setField(jwtUtils, "secret", "TEST_SECRET_FOR_CAPTCHA_LOGIN");
        ReflectionTestUtils.setField(jwtUtils, "expiration", 3600000L);

        touristAuthController = new TouristAuthController();
        ReflectionTestUtils.setField(touristAuthController, "jwtUtils", jwtUtils);
        ReflectionTestUtils.setField(touristAuthController, "touristUserService", touristUserService);
        ReflectionTestUtils.setField(touristAuthController, "stringRedisTemplate", stringRedisTemplate);
        ReflectionTestUtils.setField(touristAuthController, "smsMockEnabled", true);
    }

    @Test
    void sendCodeStoresSixDigitCodeInRedis() {
        when(stringRedisTemplate.opsForValue()).thenReturn(valueOperations);

        Result<String> result = touristAuthController.sendCode(params("phone", "13800138000"));

        ArgumentCaptor<String> codeCaptor = ArgumentCaptor.forClass(String.class);
        verify(valueOperations).set(eq("tourist_login_code:13800138000"), codeCaptor.capture(), eq(5L), eq(TimeUnit.MINUTES));
        assertEquals(200, result.getCode());
        assertTrue(codeCaptor.getValue().matches("^\\d{6}$"));
        assertTrue(result.getData().contains(codeCaptor.getValue()));
    }

    @Test
    void sendCodeReturnsCodeWhenMockEnabled() {
        ReflectionTestUtils.setField(touristAuthController, "smsMockEnabled", true);
        when(stringRedisTemplate.opsForValue()).thenReturn(valueOperations);

        Result<String> result = touristAuthController.sendCode(params("phone", "13800138000"));

        ArgumentCaptor<String> codeCaptor = ArgumentCaptor.forClass(String.class);
        verify(valueOperations).set(eq("tourist_login_code:13800138000"), codeCaptor.capture(), eq(5L), eq(TimeUnit.MINUTES));
        assertEquals(200, result.getCode());
        assertTrue(result.getData().contains(codeCaptor.getValue()));
    }

    @Test
    void sendCodeDoesNotExposeCodeWhenMockDisabled() {
        ReflectionTestUtils.setField(touristAuthController, "smsMockEnabled", false);
        when(stringRedisTemplate.opsForValue()).thenReturn(valueOperations);

        Result<String> result = touristAuthController.sendCode(params("phone", "13800138000"));

        ArgumentCaptor<String> codeCaptor = ArgumentCaptor.forClass(String.class);
        verify(valueOperations).set(eq("tourist_login_code:13800138000"), codeCaptor.capture(), eq(5L), eq(TimeUnit.MINUTES));
        assertEquals(200, result.getCode());
        assertFalse(result.getData().contains(codeCaptor.getValue()),
                "生产验证码开关关闭时，接口响应不能回显验证码");
    }

    @Test
    void loginRejectsWrongCode() {
        when(stringRedisTemplate.opsForValue()).thenReturn(valueOperations);
        when(valueOperations.get("tourist_login_code:13800138000")).thenReturn("123456");

        Result<Map<String, Object>> result = touristAuthController.login(loginParams("13800138000", "654321"));

        assertEquals(500, result.getCode());
        assertEquals("验证码错误", result.getMsg());
    }

    @Test
    void loginRejectsExpiredCode() {
        when(stringRedisTemplate.opsForValue()).thenReturn(valueOperations);
        when(valueOperations.get("tourist_login_code:13800138000")).thenReturn(null);

        Result<Map<String, Object>> result = touristAuthController.login(loginParams("13800138000", "123456"));

        assertEquals(500, result.getCode());
        assertEquals("验证码已过期，请重新获取", result.getMsg());
    }

    @Test
    void loginWithValidCodeReturnsTokenAndDeletesCode() {
        TouristUser tourist = new TouristUser();
        tourist.setId(8L);
        tourist.setPhone("13800138000");
        tourist.setNickname("雪峰游客8000");
        when(stringRedisTemplate.opsForValue()).thenReturn(valueOperations);
        when(valueOperations.get("tourist_login_code:13800138000")).thenReturn("123456");
        when(touristUserService.getOne(any(Wrapper.class), eq(false))).thenReturn(tourist);

        Result<Map<String, Object>> result = touristAuthController.login(loginParams("13800138000", "123456"));

        assertEquals(200, result.getCode());
        assertNotNull(result.getData().get("token"));
        assertEquals(8L, result.getData().get("touristId"));
        verify(stringRedisTemplate).delete("tourist_login_code:13800138000");
    }

    private Map<String, String> loginParams(String phone, String code) {
        Map<String, String> params = new HashMap<>();
        params.put("phone", phone);
        params.put("code", code);
        return params;
    }

    private Map<String, String> params(String key, String value) {
        Map<String, String> params = new HashMap<>();
        params.put(key, value);
        return params;
    }
}
