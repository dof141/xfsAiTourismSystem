package com.xfs.xfsbackend.controller;

import com.xfs.xfsbackend.common.AiUtils;
import com.xfs.xfsbackend.common.Result;
import com.xfs.xfsbackend.dto.QuestionDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.test.util.ReflectionTestUtils;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AiControllerRateLimitTest {

    private AiController aiController;

    @Mock
    private AiUtils aiUtils;

    @Mock
    private StringRedisTemplate stringRedisTemplate;

    @Mock
    private ValueOperations<String, String> valueOperations;

    @BeforeEach
    void setUp() {
        aiController = new AiController();
        ReflectionTestUtils.setField(aiController, "aiUtils", aiUtils);
        ReflectionTestUtils.setField(aiController, "stringRedisTemplate", stringRedisTemplate);
    }

    @Test
    void chatCallsAiWhenRequestIsUnderLimit() {
        when(stringRedisTemplate.opsForValue()).thenReturn(valueOperations);
        when(valueOperations.increment("ai_chat_limit:127.0.0.1")).thenReturn(1L);
        when(aiUtils.getAiAnswer(eq("介绍一下穿岩山"), any())).thenReturn("穿岩山风景优美");

        Result<String> result = aiController.chatWithAi(question("介绍一下穿岩山"), request("127.0.0.1"));

        assertEquals(200, result.getCode());
        assertEquals("穿岩山风景优美", result.getData());
        verify(aiUtils).getAiAnswer(eq("介绍一下穿岩山"), any());
    }

    @Test
    void chatRejectsRequestWhenIpExceedsLimit() {
        when(stringRedisTemplate.opsForValue()).thenReturn(valueOperations);
        when(valueOperations.increment("ai_chat_limit:127.0.0.1")).thenReturn(21L);

        Result<String> result = aiController.chatWithAi(question("介绍一下穿岩山"), request("127.0.0.1"));

        assertEquals(500, result.getCode());
        assertEquals("AI 助手访问太频繁啦，请稍后再试", result.getMsg());
        verify(aiUtils, never()).getAiAnswer(any(), any());
    }

    @Test
    void chatUsesForwardedIpForRateLimitKey() {
        MockHttpServletRequest request = request("10.0.0.2");
        request.addHeader("X-Forwarded-For", "203.0.113.10, 10.0.0.2");
        when(stringRedisTemplate.opsForValue()).thenReturn(valueOperations);
        when(valueOperations.increment("ai_chat_limit:203.0.113.10")).thenReturn(1L);
        when(aiUtils.getAiAnswer(eq("推荐路线"), any())).thenReturn("推荐穿岩山一日游");

        Result<String> result = aiController.chatWithAi(question("推荐路线"), request);

        assertEquals(200, result.getCode());
        verify(valueOperations).increment("ai_chat_limit:203.0.113.10");
    }

    private QuestionDTO question(String text) {
        QuestionDTO dto = new QuestionDTO();
        dto.setText(text);
        return dto;
    }

    private MockHttpServletRequest request(String remoteAddr) {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setRemoteAddr(remoteAddr);
        return request;
    }
}
