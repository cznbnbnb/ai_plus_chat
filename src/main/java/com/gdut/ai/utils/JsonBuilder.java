package com.gdut.ai.utils;


import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.springframework.beans.factory.annotation.Value;

import java.util.UUID;

public class JsonBuilder {

    public String buildRequestJson(String appid, String newQuestion) {
        JSONObject header = new JSONObject();
        header.put("app_id", appid);
        header.put("uid", UUID.randomUUID().toString().substring(0, 10));

        JSONObject chat = new JSONObject();
        chat.put("domain", "generalv3.5");
        chat.put("temperature", 0.5);

        JSONObject parameter = new JSONObject();
        parameter.put("chat", chat);

        JSONObject textContent = new JSONObject();
        textContent.put("role", "user");
        textContent.put("content", newQuestion);

        JSONArray textArray = new JSONArray();
        textArray.add(textContent);

        JSONObject message = new JSONObject();
        message.put("text", textArray);

        JSONObject payload = new JSONObject();
        payload.put("message", message);

        JSONObject requestJson = new JSONObject();
        requestJson.put("header", header);
        requestJson.put("parameter", parameter);
        requestJson.put("payload", payload);

        return requestJson.toJSONString();
    }
}
