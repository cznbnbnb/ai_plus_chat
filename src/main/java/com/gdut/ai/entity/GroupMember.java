package com.gdut.ai.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
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
    @JsonSerialize(using = ToStringSerializer.class)
    private Long id;

    // 群组ID
    @JsonSerialize(using = ToStringSerializer.class)
    private Long groupId;

    // 用户ID
    @JsonSerialize(using = ToStringSerializer.class)
    private Long userId;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;


}