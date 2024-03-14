package com.gdut.ai.gpt;

import com.gdut.ai.common.ResultCollector;
import com.gdut.ai.exception.AskException;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.WebSocket;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class XfSpark implements Gpt {

    @Value("${api.secret}")
    private String apiSecret;

    @Value("${api.key}")
    private String apiKey;

    //存放大模型回复
    ConcurrentHashMap<Long, ResultCollector> resultMap = new ConcurrentHashMap<>();

    final static Logger logger = LoggerFactory.getLogger(XfSpark.class);

    @Override
    public void send(String question, Long userId,boolean canDisplay) throws Exception {
        logger.info("发送消息{}，userId：{}",question, userId);
        String hostUrl = "https://spark-api.xf-yun.com/v3.5/chat";
        // 构建鉴权url
        String authUrl = getAuthUrl(hostUrl, apiKey, apiSecret);
        OkHttpClient client = new OkHttpClient.Builder().build();
        String url = authUrl.replace("http://", "ws://").replace("https://", "wss://");
        Request request = new Request.Builder().url(url).build();
        XFSparkModel model = new XFSparkModel(this, false, request, userId, canDisplay);
        model.setNewQuestion(question);
        // 个性化参数入口，如果是并发使用，可以在这里模拟
        WebSocket webSocket = client.newWebSocket(request, model);
    }

    @Override
    public ResultCollector getAnswer(Long userId) throws AskException {
        if (resultMap.containsKey(userId)) {
            ResultCollector resultCollector = resultMap.get(userId);
            if (resultCollector.getState() == ResultCollector.STATE_FINISHED) {
                resultMap.remove(userId);
                return resultCollector;
            }
            return resultCollector;
        }
        logger.info("该用户未进行对话：{}", userId);
        throw new AskException("该用户未进行对话");
    }

    // 鉴权方法
    public String getAuthUrl(String hostUrl, String apiKey, String apiSecret) throws Exception {
        URL url = new URL(hostUrl);
        // 时间
        SimpleDateFormat format = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss z", Locale.US);
        format.setTimeZone(TimeZone.getTimeZone("GMT"));
        String date = format.format(new Date());
        // 拼接
        String preStr = "host: " + url.getHost() + "\n" +
                "date: " + date + "\n" +
                "GET " + url.getPath() + " HTTP/1.1";
        // System.err.println(preStr);
        // SHA256加密
        Mac mac = Mac.getInstance("hmacsha256");
        SecretKeySpec spec = new SecretKeySpec(apiSecret.getBytes(StandardCharsets.UTF_8), "hmacsha256");
        mac.init(spec);

        byte[] hexDigits = mac.doFinal(preStr.getBytes(StandardCharsets.UTF_8));
        // Base64加密
        String sha = Base64.getEncoder().encodeToString(hexDigits);
        // System.err.println(sha);
        // 拼接
        String authorization = String.format("api_key=\"%s\", algorithm=\"%s\", headers=\"%s\", signature=\"%s\"", apiKey, "hmac-sha256", "host date request-line", sha);
        // 拼接地址
        HttpUrl httpUrl = Objects.requireNonNull(HttpUrl.parse("https://" + url.getHost() + url.getPath())).newBuilder().//
                addQueryParameter("authorization", Base64.getEncoder().encodeToString(authorization.getBytes(StandardCharsets.UTF_8))).//
                addQueryParameter("date", date).//
                addQueryParameter("host", url.getHost()).//
                build();
        return httpUrl.toString();
    }

    public void setApiSecret(String apiSecret) {
        this.apiSecret = apiSecret;
    }

    public void setApiKey(String apiKey) {
        this.apiKey = apiKey;
    }

    @Override
    public void removeUserMap(Long userId) {
        resultMap.remove(userId);
    }

    @Override
    public int getMaxToken() {
        return 8100;
    }

    @Override
    public ConcurrentHashMap<Long, ResultCollector> getResultMap() {
        return resultMap;
    }


}
