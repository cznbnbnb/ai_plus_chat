package com.gdut.ai.controller;

import com.gdut.ai.entity.ChatMessage;
import com.gdut.ai.service.ChatMessageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

import javax.servlet.http.HttpSession;

@Controller
public class ChatController {

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    @Autowired
    private ChatMessageService chatMessageService;

    @MessageMapping("/chat")
    public void handleChat(ChatMessage message, HttpSession session) {
        //获取用户id
        Long userId = (Long) session.getAttribute("user");
        message.setSenderId(userId);
        chatMessageService.save(message);
        messagingTemplate.convertAndSendToUser(message.getReceiverId().toString(), "/queue/messages", message);
    }
}
