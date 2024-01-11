package com.gdut.ai.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.gdut.ai.common.R;
import com.gdut.ai.entity.ChatMessage;
import com.gdut.ai.service.ChatMessageService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import java.util.Map;


@RestController
@RequestMapping("/chat")
@Slf4j
public class ChatMessageController {
    @Autowired
    private ChatMessageService chatMessageService;

    @GetMapping("/getMessage")
    public R<Page<ChatMessage>> getMessage(
            @RequestParam("friendId") Long friendId,
            @RequestParam(value = "page", defaultValue = "1") Integer page,
            @RequestParam(value = "limit", defaultValue = "50") Integer pageSize,
            HttpSession session) {

        // 获取用户id
        Long userId = (Long) session.getAttribute("user");
        if (userId == null) {
            return R.error("用户未登录");
        }

        // 获取消息
        Page<ChatMessage> messages = chatMessageService.getMessages(userId, friendId, page, pageSize);
        if (messages == null) {
            return R.error("获取消息失败");
        }

        return R.success(messages);
    }

    //删除与该好友的所有聊天记录
    @PostMapping("/deleteMessage")
    public R<String> deleteMessage(@RequestBody Map<String, String> map, HttpSession session) {
        //获取用户id
        Long userId = (Long) session.getAttribute("user");
        //获取好友id
        Long friendId = Long.parseLong(map.get("contactId"));
        //删除聊天记录
        boolean flag = chatMessageService.deleteMessage(userId, friendId);
        if (flag) {
            return R.success("删除聊天记录成功");
        }
        return R.error("删除聊天记录失败");
    }

}
