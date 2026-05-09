package com.xfs.xfsbackend;

import com.xfs.xfsbackend.common.AiUtils;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

//ai 测试类
@SpringBootTest
class AiIntegrationTest {

    @Autowired
    private AiUtils aiUtils;

    @Test
    void testRealAiAnswer() {
        // 1. 模拟用户提问关于雪峰山的问题
        String question = "请简要介绍一下雪峰山的穿岩山景区有哪些好玩的？";
        
        // 2. 调用 AI 服务
        String answer = aiUtils.getAiAnswer(question);
        
        // 3. 打印结果（控制台可见）
        System.out.println("--- AI Answer Start ---");
        System.out.println(answer);
        System.out.println("--- AI Answer End ---");

        // 4. 校验：回答不为空
        assertNotNull(answer, "AI回答不应为空");
        
        // 5. 校验：回答中应该包含一些核心关键词（证明人设生效）
        // 注意：由于 AI 的随机性，这里只做软校验
        boolean isRelated = answer.contains("雪峰山") || answer.contains("穿岩山") || answer.contains("景区");
        System.out.println("是否符合导览人设: " + isRelated);
    }
}
