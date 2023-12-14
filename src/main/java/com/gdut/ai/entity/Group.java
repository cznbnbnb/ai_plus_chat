package com.gdut.ai.entity;

import lombok.Data;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 群组信息
 */
@Data
public class Group implements Serializable {

    private static final long serialVersionUID = 1L;

    // 群组ID
    private Long id;

    // 群组名称
    private String name;

    // 群组头像
    private String avatar;

    // 群主
    private Long ownerId;

}
