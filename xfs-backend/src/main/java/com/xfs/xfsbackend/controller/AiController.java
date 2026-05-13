package com.xfs.xfsbackend.controller;

import com.xfs.xfsbackend.common.AiUtils;
import com.xfs.xfsbackend.common.Result;
import com.xfs.xfsbackend.dto.QuestionDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.concurrent.TimeUnit;

/**
 * AI 智能导览控制器。
 * 负责接收游客的导览问题、执行基础限流，并调用大模型工具类生成回答。
 */
@Slf4j
@RestController
@RequestMapping("/api/ai")
public class AiController {

    @Autowired
    private AiUtils aiUtils;

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    private static final int AI_CHAT_LIMIT_PER_MINUTE = 20;
    private static final String AI_RATE_LIMIT_PREFIX = "ai_chat_limit:";

    /**
     * 智能导览问答接口
     * 注意：因为前端要传 JSON 数据过来，所以用 @PostMapping 和 @RequestBody
     */
    @PostMapping("/chat")
    public Result<String> chatWithAi(@RequestBody QuestionDTO questionDTO, HttpServletRequest request) {
        // 1. 获取用户提问内容
        String questionText = questionDTO.getText();

        // 2. 简单的空值校验
        if (questionText == null || questionText.trim().isEmpty()) {
            return Result.error("您还没有输入任何问题哦！");
        }

        if (isRateLimited(request)) {
            return Result.error("AI 助手访问太频繁啦，请稍后再试");
        }

        // 3. 调用 AI 工具类获取回答（传入对话历史）
        String answer = aiUtils.getAiAnswer(questionText, questionDTO.getHistory());

        // 4. 返回结果给前端
        return Result.success(answer);
    }

    /**
     * 判断当前客户端是否触发 AI 问答限流。
     * 使用 Redis 按客户端 IP 统计 1 分钟内的请求次数，防止频繁调用大模型接口。
     */
    private boolean isRateLimited(HttpServletRequest request) {
        String clientIp = getClientIp(request);
        String redisKey = AI_RATE_LIMIT_PREFIX + clientIp;
        try {
            Long count = stringRedisTemplate.opsForValue().increment(redisKey);
            if (count != null && count == 1L) {
                stringRedisTemplate.expire(redisKey, 60, TimeUnit.SECONDS);
            }
            return count != null && count > AI_CHAT_LIMIT_PER_MINUTE;
        } catch (Exception e) {
            log.warn("AI 限流检查失败，已放行本次请求: {}", e.getMessage());
            return false;
        }
    }

    /**
     * 获取客户端 IP。
     * 如果系统部署在反向代理后面，优先读取 X-Forwarded-For 的第一个 IP。
     */
    private String getClientIp(HttpServletRequest request) {
        String forwardedFor = request.getHeader("X-Forwarded-For");
        if (forwardedFor != null && !forwardedFor.trim().isEmpty()) {
            return forwardedFor.split(",")[0].trim();
        }
        return request.getRemoteAddr();
    }
}
