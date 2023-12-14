package com.gdut.ai.entity;

import lombok.Data;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 聊天消息信息
 */
@Data
public class ChatMessage implements Serializable {

    private static final long serialVersionUID = 1L;

    // 消息ID
    private Long id;

    // 发送者ID
    private Long senderId;

    // 接收者ID
    private Long receiverId;

    // 消息内容 (如果是图片则是图片地址)
    private String content;

    // 消息类型 0:文本 1:图片
    private Integer type;

}
