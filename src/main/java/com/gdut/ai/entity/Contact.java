package com.gdut.ai.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
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
    @JsonSerialize(using = ToStringSerializer.class)
    private Long id;

    // 用户ID
    @JsonSerialize(using = ToStringSerializer.class)
    private Long userId;

    // 联系人用户ID
    @JsonSerialize(using = ToStringSerializer.class)
    private Long contactUserId;

    // 联系人备注
    private String remark;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;


}
