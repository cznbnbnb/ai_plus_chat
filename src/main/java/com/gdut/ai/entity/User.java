package com.gdut.ai.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 用户信息
 */
@Data
public class User implements Serializable {

    private static final long serialVersionUID = 1L;

    //用户id
    @JsonSerialize(using = ToStringSerializer.class)
    private Long id;

    //姓名
    private String name;


    //邮箱
    private String email;

    //密码
    private String password;

    //性别 F 女 M 男
    private String sex;


    //头像
    private String avatar;


    //状态 0:禁用，1:正常
    private Integer status;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;


}
