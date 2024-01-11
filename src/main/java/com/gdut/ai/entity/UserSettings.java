package com.gdut.ai.entity;

import lombok.Data;

import java.io.Serializable;
@Data
public class UserSettings implements Serializable {

    private String name; // 姓名
    private String sex; // 性别
    private String email; // 邮箱
    private String avatar; // 头像URL

    // 省略构造函数、getter和setter
}
