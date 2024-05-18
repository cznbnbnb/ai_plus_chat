package com.gdut.ai.utils;

import com.aliyuncs.utils.StringUtils;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.gdut.ai.common.ResultCollector;
import com.gdut.ai.entity.ChatMessage;
import com.gdut.ai.entity.GroupTable;
import com.gdut.ai.entity.User;
import com.gdut.ai.service.*;
import com.gdut.ai.prompts.PrePrompt;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

import static com.gdut.ai.common.ResultCollector.STATE_RECEIVING;

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

    @Autowired
    private GroupsService groupsService;

    //部分参数可以忽略，这里统一参数进入只是便于反射调用

    /**
     * 分析聊天记录
     *
     * @param uid             用户id
     * @param contactId       好友或群组id
     * @param message         暂时无用
     * @param resultCollector 结果收集器
     */
    public void analyze(Long uid, Long contactId, String message, ResultCollector resultCollector) {
        if (uid != null && contactId != null) {
            User user = userService.getById(uid);
            User contact = userService.getById(contactId);
            if (contact != null && user != null) {
                analyzeFriendMessage(message, resultCollector, user, contact);
            } else {
                GroupTable group = groupsService.getById(contactId);
                if (group != null) {
                    analyzeGroupMessage(uid, contactId, message, resultCollector);
                } else {
                    resultCollector.setAnswer("无法解析好友或群组信息，请重试");
                    resultCollector.setCanDisplay(true);
                }
            }
        } else {
            resultCollector.setAnswer("用户ID或联系人ID为空");
            resultCollector.setCanDisplay(true);
        }
    }

    //分析好友聊天记录
    private void analyzeFriendMessage(String message, ResultCollector resultCollector
            , User user, User contact) {
        String myName = user.getName();
        String friendName = contact.getName();
        Page<ChatMessage> messages = chatMessageService.getMessages(user.getId()
                , contact.getId(), 1, 30);
        StringBuilder sb = new StringBuilder();
        List<ChatMessage> records = messages.getRecords();
        records.sort((o1, o2) -> o2.getCreateTime().compareTo(o1.getCreateTime()));
        sb.append("我的昵称叫做：").append(myName).append("，以下是我们的聊天记录：\n");
        for (ChatMessage chatMessage : records) {
            if (chatMessage.getSenderId().equals(user.getId()))
                sb.append(chatMessage.getCreateTime()).append("时:")
                        .append("我").append("说：").append(chatMessage.getContent()).append("\n");
            else if (chatMessage.getSenderId().equals(contact.getId()))
                sb.append(chatMessage.getCreateTime())
                        .append("时:").append(friendName).append("说：")
                        .append(chatMessage.getContent()).append("\n");
        }
        sb.append("以上是最近30条聊天记录,请分析，").append(message);
        sendAnalysisResult(sb.toString(), user.getId(), contact.getId(), resultCollector);
    }

    //分析群组聊天记录
    private void analyzeGroupMessage(Long uid, Long contactId, String message, ResultCollector resultCollector) {
        Page<ChatMessage> messages = chatMessageService.getMessages(uid, contactId, 1, 30);
        StringBuilder sb = new StringBuilder();
        List<ChatMessage> records = messages.getRecords();
        records.sort((o1, o2) -> o2.getCreateTime().compareTo(o1.getCreateTime()));
        sb.append("我的昵称叫做：").append(userService.getById(uid).getName()).append("，以下是我们的群聊天记录：\n");
        for (ChatMessage chatMessage : records) {
            User groupMember = userService.getById(chatMessage.getSenderId());
            String groupMemberName = groupMember != null ? groupMember.getName() : "群友";
            sb.append(chatMessage.getCreateTime()).append("时:").append(groupMemberName).append("说：").append(chatMessage.getContent()).append("\n");
        }
        sb.append("以上是最近30条聊天记录,请分析，").append(message);
        sendAnalysisResult(sb.toString(), uid, contactId, resultCollector);
    }

    //提取公共部分
    private void sendAnalysisResult(String analysisContent, Long uid, Long contactId, ResultCollector resultCollector) {
        boolean result = aiService.send(analysisContent, uid, contactId, PrePrompt.NORMAL_PROMPT.getText());
        if (result) {
            resultCollector.setAnswer("");
            resultCollector.setState(STATE_RECEIVING);
            resultCollector.setCanDisplay(true);
        } else {
            resultCollector.setAnswer("分析聊天记录失败");
            resultCollector.setCanDisplay(true);
        }
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
        } else {
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
        if (uid != null) {
            if (momentService.sendMoment(uid, message, "")) {
                resultCollector.setAnswer("发送朋友圈成功，内容为：" + message);
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

    /**
     * 发送消息给AI
     *
     * @param uid             用户id
     * @param contactId       好友id
     * @param message         消息内容
     * @param resultCollector 结果收集器
     */
    public void chat(Long uid, Long contactId, String message, ResultCollector resultCollector) {
        log.info("发送消息，accessId：{}", uid);
        if (uid != null) {
            if (aiService.send(message, uid, contactId, PrePrompt.NORMAL_PROMPT.getText())) {
                resultCollector.setAnswer("");
                resultCollector.setState(STATE_RECEIVING);
                resultCollector.setCanDisplay(true);
            } else {
                resultCollector.setAnswer("发送失败，请重试");
                resultCollector.setCanDisplay(true);
            }
        } else {
            resultCollector.setAnswer("用户ID为空，请重试");
            resultCollector.setCanDisplay(true);
        }
    }
}
