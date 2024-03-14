package com.gdut.ai.config;

import com.gdut.ai.entity.ChatMessage;
import com.gdut.ai.service.ChatMessageService;
import com.gdut.ai.service.GroupMemberService;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import com.fasterxml.jackson.databind.ObjectMapper; // 引入 Jackson 库进行 JSON 解析

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;


public class MyWebSocketHandler extends TextWebSocketHandler {

    // 用于保存所有的 WebSocket session，以用户ID作为键
    private final Map<Long, WebSocketSession> userSessions = new ConcurrentHashMap<>();

    private final ChatMessageService chatMessageService;

    private final GroupMemberService groupMemberService;
    private final ObjectMapper objectMapper;

    public MyWebSocketHandler(ChatMessageService chatMessageService, GroupMemberService groupMemberService) {
        this.chatMessageService = chatMessageService;
        this.groupMemberService = groupMemberService;
        this.objectMapper = new ObjectMapper();
    }

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        Long userId = (Long) session.getAttributes().get("userId");
        if (userId != null) {
            userSessions.put(userId, session);
        }
    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        // 解析接收到的消息
        ChatMessage chatMessage = objectMapper.readValue(message.getPayload(), ChatMessage.class);
        // 保存消息
        chatMessageService.save(chatMessage);
        // 广播消息
        broadcastMessage(chatMessage);

    }

    private void broadcastMessage(ChatMessage message) {
        if (message.getReceiverType() == 0) {
            sendToUser(message);
        } else {
            sendToGroup(message);
        }
    }

    private void sendToUser(ChatMessage message) {
        userSessions.values().forEach(session -> {
            try {
                if (session.isOpen()) {
                    String jsonMessage = objectMapper.writeValueAsString(message);
                    session.sendMessage(new TextMessage(jsonMessage));
                }
            } catch (IOException e) {
                // 异常处理，比如记录日志等
                e.printStackTrace();
            }
        });
    }

    private void sendToGroup(ChatMessage message) {
        // 获取群聊的所有成员列表
        List<Long> groupMemberIds = groupMemberService.getGroupMemberIds(message.getReceiverId());
        groupMemberIds.forEach(memberId -> {
            WebSocketSession memberSession = userSessions.get(memberId);
            if (memberSession != null && memberSession.isOpen()) {
                try {
                    String jsonMessage = objectMapper.writeValueAsString(message);
                    memberSession.sendMessage(new TextMessage(jsonMessage));
                } catch (IOException e) {
                    // 异常处理
                    e.printStackTrace();
                }
            }
        });
    }

}