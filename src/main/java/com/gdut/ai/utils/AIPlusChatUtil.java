package com.gdut.ai.utils;

import com.aliyuncs.utils.StringUtils;
import com.gdut.ai.common.ResultCollector;
import com.gdut.ai.service.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class AIPlusChatUtil {

    @Autowired
    private AIService aiService;

    @Autowired
    private UserService userService;

    @Autowired
    private MomentService momentService;

    @Autowired
    private ChatMessageService chatMessageService;


    //部分参数可以忽略，这里统一参数进入只是便于反射调用

    /**
     * 分析聊天记录
     *
     * @param uid             用户id
     * @param fid             好友id
     * @param message         暂时无用
     * @param resultCollector 结果收集器
     */
    public void analyzeMessage(Long uid, Long fid, String message, ResultCollector resultCollector) {
        resultCollector.setAnswer("暂时不支持分析聊天记录功能");
        resultCollector.setCanDisplay(true);
    }

    /**
     * 删除聊天记录
     *
     * @param uid             用户id
     * @param fid             好友id
     * @param message         暂时无用
     * @param resultCollector 结果收集器
     */
    public void deleteFriend(Long uid, Long fid, String message, ResultCollector resultCollector) {
        if (uid != null && fid != null) {
            if (userService.deleteFriend(uid, fid)) {
                if (chatMessageService.deleteMessage(uid, fid)) {
                    resultCollector.setAnswer("删除好友并清空聊天记录成功");
                    resultCollector.setCanDisplay(true);
                } else {
                    resultCollector.setAnswer("删除好友成功，但清空聊天记录失败");
                    resultCollector.setCanDisplay(true);
                }
            } else {
                resultCollector.setAnswer("好友信息解析失败，请重试");
                resultCollector.setCanDisplay(true);
            }
        }else {
            resultCollector.setAnswer("好友信息解析失败，请重试");
            resultCollector.setCanDisplay(true);
        }
    }

    /**
     * 发送朋友圈
     *
     * @param uid             用户id
     * @param fid             好友id 暂时无用
     * @param message         朋友圈内容
     * @param resultCollector 结果收集器
     */
    public void postMoments(Long uid, Long fid, String message, ResultCollector resultCollector) {
        log.info("发送朋友圈，message:{}", message);
        if (uid != null && !StringUtils.isEmpty(message)) {
            if (momentService.sendMoment(uid, message, "")) {
                resultCollector.setAnswer("发送朋友圈成功");
                resultCollector.setCanDisplay(true);
            } else {
                resultCollector.setAnswer("发送朋友圈失败");
                resultCollector.setCanDisplay(true);
            }
        } else {
            resultCollector.setAnswer("用户信息解析失败，请重试");
            resultCollector.setCanDisplay(true);
        }
    }
}
