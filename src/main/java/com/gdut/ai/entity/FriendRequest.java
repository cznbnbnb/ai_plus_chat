package com.gdut.ai.entity;

import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 好友申请信息
 */
@Data
public class FriendRequest implements Serializable {

    private static final long serialVersionUID = 1L;

    // 申请ID
    private Long id;

    // 发起人ID
    private Long requesterId;

    // 接收人ID
    private Long receiverId;

    // 申请状态 (例如：0 待审核，1 已接受，2 已拒绝)
    private Integer status;

    // 附加消息
    private String message;
}
