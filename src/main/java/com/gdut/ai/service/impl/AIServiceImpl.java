package com.gdut.ai.service.impl;

import com.gdut.ai.common.ResultCollector;
import com.gdut.ai.gpt.Gpt;
import com.gdut.ai.service.AIService;
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
    public boolean send(String msg, String accessId) {
        try{
            xfSpark.send(msg,accessId);
            return true;
        }catch (Exception e){
            log.error("发送消息失败",e);
            return false;
        }
    }

    @Override
    public ResultCollector getAnswer(String accessId) {
        try{
            return xfSpark.getAnswer(accessId);
        }catch (Exception e){
            log.error("获取回复失败",e);
            return null;
        }
    }
}
