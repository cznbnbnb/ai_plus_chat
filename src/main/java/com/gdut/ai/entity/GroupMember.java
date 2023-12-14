package com.gdut.ai.entity;

import lombok.Data;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 群组成员信息
 */
@Data
public class GroupMember implements Serializable {

    private static final long serialVersionUID = 1L;

    // 成员ID
    private Long id;

    // 群组ID
    private Long groupId;

    // 用户ID
    private Long userId;

}