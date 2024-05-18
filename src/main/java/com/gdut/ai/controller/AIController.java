package com.gdut.ai.controller;

import com.aliyuncs.utils.StringUtils;
import com.gdut.ai.common.R;
import com.gdut.ai.common.ResultCollector;
import com.gdut.ai.service.AIService;
import com.gdut.ai.service.ChatMessageService;
import com.gdut.ai.service.MomentService;
import com.gdut.ai.service.UserService;
import com.gdut.ai.utils.AIPlusChatUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpSession;
import java.lang.reflect.Method;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.gdut.ai.common.ResultCollector.STATE_FINISHED;

@RestController
@RequestMapping("/ai")
@Slf4j
public class AIController {

    @Autowired
    private AIService aiService;

    @Autowired
    private UserService userService;

    @Autowired
    private MomentService momentService;

    @Autowired
    private ChatMessageService chatMessageService;

    @Autowired
    private AIPlusChatUtil aiPlusChatUtil;

    /**
     * 发送消息给AI
     *
     * @param payload 消息内容 :
     *                type:消息类型: order, normal
     *                message:消息内容
     *                contactId:好友id
     * @param session 用于获取用户id
     * @return 发送结果
     */
    @PostMapping("/sendMessage")
    public R<String> sendMessageToAI(@RequestBody Map<String, String> payload, HttpSession session) {
        Long userId = (Long) session.getAttribute("user");
        String message = payload.get("message");
        String type = payload.get("type");
        String contactIdStr = payload.get("contactId");
        long friendId = 0;
        if (!StringUtils.isEmpty(contactIdStr)) {
            friendId = Long.parseLong(contactIdStr);
        }
        log.info("反射发送资料：message:{}, userId:{}, friendId:{}, type:{}",message, userId, friendId, type);
        boolean flag = aiService.send(message, userId, friendId, type);
        if (flag) {
            return R.success("发送成功");
        }
        return R.error("发送失败");
    }

    //获取ai回复
    @PostMapping("/getAnswer")
    public R<ResultCollector> getAnswerFromAI(HttpSession session) {
        Long userId = (Long) session.getAttribute("user");
        log.info("获取回复，userId：{}", userId);
        // 这里实现获取回复的逻辑
        ResultCollector answer = aiService.getAnswer(userId);
        if (answer.getState() == STATE_FINISHED && !answer.getCanDisplay()) {
            messageGenerator(answer.getAnswer(), answer);
        }
        return R.success(answer);
    }

    private void messageGenerator(String msg, ResultCollector resultCollector) {
        if (StringUtils.isEmpty(msg)) {
            return;
        }
        log.info("message:{}", msg);
        String patternTemplate = "\\|(op|uid|fid|message):([^|]+)";
        Pattern pattern = Pattern.compile(patternTemplate);
        Matcher matcher = pattern.matcher(msg);
        String op = "", uid = "", fid = "", message = "";

        while (matcher.find()) {
            switch (matcher.group(1)) {
                case "op":
                    op = matcher.group(2);
                    break;
                case "uid":
                    uid = matcher.group(2);
                    break;
                case "fid":
                    fid = matcher.group(2);
                    break;
                case "message":
                    message = matcher.group(2);
                    break;
            }
        }
        log.info("op:{},uid:{},fid:{},message:{}", op, uid, fid, message);
        Long uidValue = uid.isEmpty() ? null : Long.valueOf(uid);
        Long fidValue = fid.isEmpty() ? null : Long.valueOf(fid);
        try {
            Method method = AIPlusChatUtil.class.getDeclaredMethod(op, Long.class
                    , Long.class, String.class, ResultCollector.class);
            method.invoke(aiPlusChatUtil, uidValue, fidValue, message, resultCollector);
        } catch (Exception e) {
            e.printStackTrace();
            resultCollector.setAnswer("操作失败：" + e.getMessage());
            resultCollector.setCanDisplay(true);
        }
    }


}
