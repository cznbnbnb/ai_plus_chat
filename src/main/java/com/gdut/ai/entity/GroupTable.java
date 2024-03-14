package com.gdut.ai.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import lombok.Data;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 群组信息
 */
@Data
public class GroupTable implements Serializable {

    private static final long serialVersionUID = 1L;

    // 群组ID
    @JsonSerialize(using = ToStringSerializer.class)
    private Long id;

    // 群组名称
    private String name;

    // 群组头像
    private String avatar;

    // 群主
    @JsonSerialize(using = ToStringSerializer.class)
    private Long ownerId;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;

}
