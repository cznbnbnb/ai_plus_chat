package com.gdut.ai.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
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

    // 发送者姓名
    private String senderName;

    // 接收者ID
    private Long receiverId;

    // 接收者姓名
    private String receiverName;

    // 消息内容 (如果是图片则是图片地址)
    private String content;

    // 消息类型 0:文本 1:图片
    private Integer type;

    // 创建时间
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    // 更新时间
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;

}
