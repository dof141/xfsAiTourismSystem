package com.xfs.xfsbackend.common;

import cn.hutool.http.HttpRequest;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * AI 导游工具类
 * 已接入真实大模型接口 (OpenAI 兼容协议)
 */
@Slf4j
@Component
public class AiUtils {

    @Value("${xfs.ai.api-key}")
    private String apiKey;

    @Value("${xfs.ai.api-url}")
    private String apiUrl;

    @Value("${xfs.ai.model}")
    private String model;

    // 预设系统人设 Prompt：让 AI 成为雪峰山专家
    private static final String SYSTEM_PROMPT = 
            "你现在是【雪峰山智慧文旅系统】的专属 AI 导游。你的名字叫'雪峰百事通'。\n" +
            "你的职责：\n" +
            "1. 介绍怀化雪峰山五大景区（穿岩山、大花瑶、阳雀坡、山背花瑶梯田、雪峰山国家森林公园）的自然美景、历史文化、门票价格和开放时间。\n" +
            "2. 为游客规划旅游路线，推荐当地特色美食（如花瑶腊肉、红薯粉）。\n" +
            "3. 回答关于景区预约、天气情况等相关问题。\n" +
            "要求：语气亲切、热情、专业。如果用户的问题与旅游或雪峰山完全无关，请礼貌地引导用户回到旅游话题。";

    /**
     * 调用大模型接口 (真实版)
     * @param question 用户的提问
     * @return AI 的回答
     */
    public String getAiAnswer(String question) {
        log.info("开始调用 AI 接口，提问内容: {}", question);

        // 如果没有配置 Key，返回提示（防止报错导致系统崩溃）
        if (apiKey == null || apiKey.contains("your-real-key")) {
            return "【系统提示】AI 接口密钥尚未配置，请联系管理员在 application.yml 中配置 API Key。目前为您开启模拟回答模式：雪峰山欢迎您！这里景色优美，适合避暑。";
        }

        try {
            // 1. 构建请求消息体 (OpenAI 标准格式)
            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("model", model);
            
            List<Map<String, String>> messages = new ArrayList<>();
            // 添加系统提示词
            Map<String, String> systemMsg = new HashMap<>();
            systemMsg.put("role", "system");
            systemMsg.put("content", SYSTEM_PROMPT);
            messages.add(systemMsg);
            
            // 添加用户问题
            Map<String, String> userMsg = new HashMap<>();
            userMsg.put("role", "user");
            userMsg.put("content", question);
            messages.add(userMsg);
            
            requestBody.put("messages", messages);
            requestBody.put("temperature", 0.7); // 适中的创造力

            // 2. 发送 POST 请求 (使用 Hutool)
            String jsonResult = HttpRequest.post(apiUrl)
                    .header("Authorization", "Bearer " + apiKey)
                    .header("Content-Type", "application/json")
                    .body(JSONUtil.toJsonStr(requestBody))
                    .timeout(30000) // 30秒超时，AI 思考比较慢
                    .execute().body();

            log.info("AI 接口返回原始数据: {}", jsonResult);

            // 3. 解析返回的 JSON
            JSONObject jsonObject = JSONUtil.parseObj(jsonResult);
            
            // 检查是否有错误返回
            if (jsonObject.containsKey("error")) {
                JSONObject error = jsonObject.getJSONObject("error");
                return "AI 助手暂时无法回答：" + error.getStr("message");
            }

            // 提取 choices[0].message.content
            JSONArray choices = jsonObject.getJSONArray("choices");
            if (choices != null && !choices.isEmpty()) {
                JSONObject firstChoice = choices.getJSONObject(0);
                return firstChoice.getJSONObject("message").getStr("content");
            }

        } catch (Exception e) {
            log.error("调用 AI 接口发生异常: ", e);
            return "抱歉，由于网络波动，我暂时无法连接到大脑，请稍后再试。";
        }

        return "我思考了很久，但没能给出准确答案，换个方式问我吧？";
    }
}