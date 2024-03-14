package com.gdut.ai.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import lombok.Data;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 朋友圈动态信息
 */
@Data
public class Moment implements Serializable {

    private static final long serialVersionUID = 1L;

    // 动态ID
    @JsonSerialize(using = ToStringSerializer.class)
    private Long id;

    // 用户ID
    @JsonSerialize(using = ToStringSerializer.class)
    private Long userId;

    // 用户名
    private String name;

    // 用户头像
    private String avatar;

    // 动态内容
    private String content;

    // 动态图片
    private String images;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
}
