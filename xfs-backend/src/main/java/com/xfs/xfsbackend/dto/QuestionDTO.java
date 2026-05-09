package com.xfs.xfsbackend.dto;

import lombok.Data;
import java.util.List;

@Data
public class QuestionDTO {
    // 接收前端传来的提问文本
    private String text;
    // 对话历史（用于多轮对话）
    private List<ChatMessage> history;

    @Data
    public static class ChatMessage {
        private String role;    // "user" 或 "ai"
        private String content;
    }
}