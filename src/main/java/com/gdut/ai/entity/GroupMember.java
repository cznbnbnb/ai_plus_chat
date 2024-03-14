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

    //用户群昵称
    private String nickname;

    //用户对群的备注
    private String remark;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;


}