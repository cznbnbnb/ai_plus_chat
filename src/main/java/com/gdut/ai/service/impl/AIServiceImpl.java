package com.gdut.ai.service.impl;

import com.alibaba.druid.util.StringUtils;
import com.gdut.ai.common.ResultCollector;
import com.gdut.ai.gpt.Gpt;
import com.gdut.ai.service.AIService;
import com.gdut.ai.textenum.TextType;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
                String ids = "uid:" + userId.toString() + "\nfid:" + friendId.toString();
                xfSpark.send(ids + TextType.ORDER_TEXT.getText() + msg, userId, false);
            }else if (type.equals("again")) {
                //
                System.out.println("userId: " + userId);
            } else
                xfSpark.send(TextType.NORMAL_TEXT.getText() + msg, userId, true);
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
