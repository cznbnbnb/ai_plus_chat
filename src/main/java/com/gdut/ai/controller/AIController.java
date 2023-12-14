package com.gdut.ai.controller;

import com.gdut.ai.common.R;
import com.gdut.ai.common.ResultCollector;
import com.gdut.ai.service.AIService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.Map;

@RestController
@RequestMapping("/ai")
@Slf4j
public class AIController {

    @Autowired
    private AIService aiService;

    //给ai发送消息
    @PostMapping("/sendMessage")
    public R<String> sendMessageToAI(@RequestBody Map<String, String> payload, HttpSession session) {
        String accessId = session.getId();
        log.info("发送消息，accessId：{}", accessId);
        String message = payload.get("message");
        boolean flag = aiService.send(message, accessId);
        if (flag) {
            return R.success("发送成功");
        }
        return R.error("发送失败");
    }

    //获取ai回复
    @PostMapping("/getAnswer")
    public R<ResultCollector> getAnswerFromAI(HttpServletRequest request) {
        String accessId = request.getSession().getId();
        log.info("获取回复，accessId：{}", accessId);
        // 这里实现获取回复的逻辑
        ResultCollector answer = aiService.getAnswer(accessId);
        return R.success(answer);
    }


}
