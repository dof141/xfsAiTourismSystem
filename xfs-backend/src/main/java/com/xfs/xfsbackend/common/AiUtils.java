package com.xfs.xfsbackend.common;

import cn.hutool.http.HttpUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;
//ai 类，负责进行处理ai导游功能
@Component
public class AiUtils {

    // ⚠️ 这里我们先模拟一个本地的方法，证明整个链路是通的。
    // 等你以后申请到真实的 API Key（比如通义千问、DeepSeek），就把里面的代码换成真实的 HTTP 请求。

    /**
     * 调用大模型接口 (模拟版)
     * @param question 用户的提问
     * @return AI 的回答
     */
    public String getAiAnswer(String question) {

        // --- 以下是模拟 AI 思考的假数据逻辑，用于前期开发联调 ---
        String answer = "对不起，我不太明白您的意思。我是雪峰山专属导览助手，您可以问我关于穿岩山、大花瑶等五大景区的问题。";

        if (question.contains("门票") || question.contains("价格")) {
            answer = "雪峰山五大景区门票各不相同。例如：雪峰山国家森林公园核心景点英雄山门票参考价为30元；穿岩山景区枫香瑶寨参考价为45元。具体请以您预约当天的实际价格为准。";
        } else if (question.contains("穿岩山") || question.contains("玻璃栈道")) {
            answer = "穿岩山景区是我国首个民企申报成功的国家级森林公园！其核心景点包括惊险刺激的山鬼玻璃栈道（55元）和极具特色的枫香瑶寨（45元），非常适合周末游玩。";
        } else if (question.contains("路线") || question.contains("怎么玩")) {
            answer = "为您推荐【雪峰山抗战文化与梯田风光2日游】路线：\nDay1: 上午游览阳雀坡抗战古村，感受历史；下午前往穿岩山景区，夜宿枫香瑶寨。\nDay2: 前往山背花瑶梯田，在观景台看云海和万亩梯田，体验农耕文化。";
        } else if (question.contains("避暑") || question.contains("夏天")) {
            answer = "如果您想避暑，强烈推荐【虎形山大花瑶景区】！那里平均海拔1350米，夏季最高气温不超过27℃，还有旺溪瀑布群，是绝佳的避暑胜地。";
        }

        // 模拟网络延迟 1 秒钟，让它看起来像真的在思考
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        return answer;

        /* // --- ⬇️ 这里是真实调用大模型API的骨架代码（供你答辩和后期升级参考） ⬇️ ---
        // 假设你申请了某个大模型的 API Key
        String apiKey = "sk-xxxxxxxxxxxxxxxxx";
        String apiUrl = "https://api.xxxx.com/v1/chat/completions";

        // 构建符合大模型要求的 JSON 数据结构（通常包含系统人设和用户问题）
        Map<String, Object> requestBody = new HashMap<>();
        // ... (此处省略构建消息体的细节，不同大模型结构略有不同，但大同小异)

        // 使用 Hutool 发送真正的 HTTP POST 请求
        String jsonResult = HttpUtil.createPost(apiUrl)
                .header("Authorization", "Bearer " + apiKey)
                .body(JSONUtil.toJsonStr(requestBody))
                .execute().body();

        // 解析返回的 JSON 拿到核心文本
        JSONObject jsonObject = JSONUtil.parseObj(jsonResult);
        // return 解析出来的文本...
        */
    }
}