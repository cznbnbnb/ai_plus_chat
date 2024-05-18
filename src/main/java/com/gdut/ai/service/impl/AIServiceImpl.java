package com.gdut.ai.service.impl;

import com.gdut.ai.common.ResultCollector;
import com.gdut.ai.gpt.Gpt;
import com.gdut.ai.service.AIService;
import com.gdut.ai.prompts.PrePrompt;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class AIServiceImpl implements AIService {

    private Gpt xfSpark;

    @Autowired
    @Qualifier("xfSpark")
    public void setXfSpark(Gpt xfSpark) {
        this.xfSpark = xfSpark;
    }

    @Override
    public boolean send(String msg, Long userId, Long friendId, String type) {
        try {
            if (type.equals("order")) {
                String ids = "后续的用户id请参考->uid为：" + userId.toString() + "\nfid为：" + friendId.toString()+ "\n";
                xfSpark.send(ids + PrePrompt.ORDER_PROMPT.getText() + msg, userId, false);
            }else if (type.equals("again")) {
                //
                System.out.println("userId: " + userId);
            } else
                xfSpark.send(PrePrompt.NORMAL_PROMPT.getText() + msg, userId, true);
            return true;
        } catch (Exception e) {
            log.error("发送消息失败", e);
            return false;
        }
    }



    @Override
    public ResultCollector getAnswer(Long userId) {
        try {
            return xfSpark.getAnswer(userId);
        } catch (Exception e) {
            log.error("获取回复失败", e);
            return null;
        }
    }
}
