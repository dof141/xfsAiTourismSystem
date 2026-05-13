package com.xfs.xfsbackend.dto;

import lombok.Data;
import java.util.List;

/**
 * AI 问答请求 DTO。
 * 前端调用 /api/ai/chat 时传入当前问题 text，以及可选的历史对话 history。
 */
@Data
public class QuestionDTO {
    // 接收前端传来的提问文本
    private String text;
    // 对话历史（用于多轮对话）
    private List<ChatMessage> history;

    /**
     * 单条历史对话消息。
     * role 表示消息角色，content 表示消息内容，用于让大模型理解上下文。
     */
    @Data
    public static class ChatMessage {
        private String role;    // "user" 或 "ai"
        private String content;
    }
}
