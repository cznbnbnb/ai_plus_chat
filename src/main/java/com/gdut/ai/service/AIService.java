package com.gdut.ai.service;

import com.gdut.ai.common.ResultCollector;

public interface AIService {

    //向ai发送消息
    boolean send(String msg, String accessId);

    //获取ai的回复
    ResultCollector getAnswer(String accessId);

}
