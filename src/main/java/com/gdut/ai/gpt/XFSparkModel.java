package com.gdut.ai.gpt;

import com.gdut.ai.common.ResultCollector;
import com.gdut.ai.utils.JsonBuilder;
import com.gdut.ai.utils.JsonParse;
import com.google.gson.Gson;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.WebSocket;
import okhttp3.WebSocketListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;

import java.io.IOException;
import java.util.List;

import static com.gdut.ai.common.ResultCollector.STATE_FINISHED;

//讯飞星火websocket构建
public class XFSparkModel extends WebSocketListener {

    final static Logger logger = LoggerFactory.getLogger(XFSparkModel.class);

    public String appid;

    private final Object ansLock = new Object();
    //需要询问的问题
    public String newQuestion;
    public StringBuilder ans;

    public Request request;
    public Gson gson = new Gson();

    private final Long userId;

    StringBuilder combinedStringBuilder;
    private Boolean wsCloseFlag;
    //状态码，如果为2时代表生成结束
    private int status;

    private final Gpt m_XFSpark;

    //如果是命令模式，则在二轮对话之前不展示聊天内容
    private final boolean canDisplay;

    // 构造函数
    public XFSparkModel(Gpt gpt, Boolean wsCloseFlag, Request request, Long userId, boolean canDisplay) {
        this.appid = "4c73e42b";
        this.request = request;
        this.wsCloseFlag = wsCloseFlag;
        this.combinedStringBuilder = new StringBuilder();
        this.userId = userId;
        this.canDisplay = canDisplay;
        m_XFSpark = gpt;
    }


    public void setNewQuestion(String newQuestion) {
        this.newQuestion = newQuestion;
    }

    // 线程来发送音频与参数
    class MyThread extends Thread {
        private final WebSocket webSocket;

        public MyThread(WebSocket webSocket) {
            this.webSocket = webSocket;
        }

        public void run() {
            String requestJson;//请求参数json串
            try {
                JsonBuilder jsonBuilder = new JsonBuilder();
                requestJson = jsonBuilder.buildRequestJson(appid, newQuestion);
                webSocket.send(requestJson);
                // 等待服务端返回完毕后关闭
                long startTime = System.currentTimeMillis();
                while (true) {
                    Thread.sleep(200);
                    if (wsCloseFlag) {
                        break;
                    }
                    long currTime = System.currentTimeMillis();
                    if (currTime - startTime > 2 * 60 * 1000) {
                        // 已经等待超过2分钟
                        logger.info("回复超时，关闭websocket");
                        break;
                    }
                }
                webSocket.close(1000, "");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    //连接成功会调用到
    @Override
    public void onOpen(WebSocket webSocket, Response response) {
        super.onOpen(webSocket, response);
        MyThread myThread = new MyThread(webSocket);
        myThread.start();
    }

    //当 WebSocket 接收到来自客户端的消息时，会调用
    @Override
    public void onMessage(WebSocket webSocket, String text) {
        JsonParse myJsonParse = JsonParse.fromJsonString(text);
        //System.out.println(myJsonParse.header.code);
        if (myJsonParse.header.code != 0) {
            logger.info("发生错误，错误码为：" + myJsonParse.header.code);
            logger.info("本次请求的sid为：" + myJsonParse.header.sid);
            webSocket.close(1000, "");
        }
        List<JsonParse.Text> textList = myJsonParse.payload.choices.text;
        for (JsonParse.Text temp : textList) {
            System.out.print(temp.content);
            combinedStringBuilder.append(temp.content);
            if (m_XFSpark.getResultMap().containsKey(userId)) {
                m_XFSpark.getResultMap().get(userId).setCanDisplay(canDisplay);
                m_XFSpark.getResultMap().get(userId).setAnswer(combinedStringBuilder.toString());
            } else {
                m_XFSpark.getResultMap().put(userId, new ResultCollector(combinedStringBuilder.toString()
                        , 0, 0, "星火大模型V2.0"
                ));
                //设置是否展示
                m_XFSpark.getResultMap().get(userId).setCanDisplay(canDisplay);
            }
        }
        if (myJsonParse.header.status == STATE_FINISHED) {
            // 可以关闭连接，释放资源
            logger.info("星火大模型V2.0回复结束");
            ResultCollector resultCollector = m_XFSpark.getResultMap().get(userId);
            int inputToken = myJsonParse.payload.usage.textUsage.prompt_tokens;
            resultCollector.setInputToken(inputToken);
            int outputToken = myJsonParse.payload.usage.textUsage.completion_tokens;
            resultCollector.setOutputToken(outputToken);
            resultCollector.setState(STATE_FINISHED);
            this.status = STATE_FINISHED;
            wsCloseFlag = true;
            // 新增直接关闭WebSocket连接的代码
            webSocket.close(1000, "Process finished");
            logger.info("WebSocket连接已关闭");
        }
    }


    //连接失败的时候会用到
    @Override
    public void onFailure(WebSocket webSocket, Throwable t, Response response) {
        super.onFailure(webSocket, t, response);
        try {
            if (null != response) {
                int code = response.code();
                logger.info("onFailure code:" + code);
                logger.info("onFailure body:" + response.body().string());
                if (101 != code) {
                    logger.info("讯飞星火服务连接失败，请稍后重连...");
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public Long getUserId() {
        return userId;
    }


}