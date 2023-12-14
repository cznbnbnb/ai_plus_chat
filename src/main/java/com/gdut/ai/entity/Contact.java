package com.gdut.ai.entity;

import lombok.Data;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 联系人信息
 */
@Data
public class Contact implements Serializable {

    private static final long serialVersionUID = 1L;

    // 联系人ID
    private Long id;

    // 用户ID
    private Long userId;

    // 联系人用户ID
    private Long contactUserId;

    // 联系人备注
    private String remark;


}
