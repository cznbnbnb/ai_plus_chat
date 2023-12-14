package com.gdut.ai.gpt;


import com.gdut.ai.common.ResultCollector;
import com.gdut.ai.exception.AskException;

import java.util.concurrent.ConcurrentHashMap;

public interface Gpt {

    // 问题（模板+问题）
    void send(String question,String accessId) throws Exception;

    ResultCollector getAnswer(String accessId) throws AskException;
    //删除用户的问答映射
    void removeUserMap(String accessId);

    int getMaxToken();

    ConcurrentHashMap<String, ResultCollector> getResultMap();

}
