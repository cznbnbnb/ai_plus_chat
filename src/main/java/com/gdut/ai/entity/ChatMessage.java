package com.gdut.ai.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;

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
    @JsonSerialize(using = ToStringSerializer.class)
    private Long id;

    // 发送者ID
    @JsonSerialize(using = ToStringSerializer.class)
    private Long senderId;

    // 发送者姓名
    private String senderName;

    // 接收者ID
    @JsonSerialize(using = ToStringSerializer.class)
    private Long receiverId;

    // 接收者姓名
    private String receiverName;

    // 消息内容 (如果是图片则是图片地址)
    private String content;

    // 消息类型 0:文本 1:图片
    private Integer type;

    // 消息发送对象类型 0:用户 1:群组
    private Integer receiverType;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;

}
