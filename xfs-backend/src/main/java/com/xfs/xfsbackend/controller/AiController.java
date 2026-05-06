package com.xfs.xfsbackend.controller;

import com.xfs.xfsbackend.common.AiUtils;
import com.xfs.xfsbackend.common.Result;
import com.xfs.xfsbackend.dto.QuestionDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

//ai 的控制器
@RestController
@RequestMapping("/api/ai")
public class AiController {

    @Autowired
    private AiUtils aiUtils;

    /**
     * 智能导览问答接口
     * 注意：因为前端要传 JSON 数据过来，所以用 @PostMapping 和 @RequestBody
     */
    @PostMapping("/chat")
    public Result<String> chatWithAi(@RequestBody QuestionDTO questionDTO) {
        // 1. 获取用户提问内容
        String questionText = questionDTO.getText();

        // 2. 简单的空值校验
        if (questionText == null || questionText.trim().isEmpty()) {
            return Result.error("您还没有输入任何问题哦！");
        }

        // 3. 调用 AI 工具类获取回答
        String answer = aiUtils.getAiAnswer(questionText);

        // 4. 返回结果给前端
        return Result.success(answer);
    }
}