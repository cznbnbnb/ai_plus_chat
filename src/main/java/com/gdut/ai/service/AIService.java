package com.gdut.ai.service;

import com.gdut.ai.common.ResultCollector;

public interface AIService {

    //向ai发送消息
    boolean send(String msg, Long userId,Long friendId, String type);

    //获取ai的回复
    ResultCollector getAnswer(Long userId);

}
