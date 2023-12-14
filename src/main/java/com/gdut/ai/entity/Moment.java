package com.gdut.ai.entity;

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
    private Long id;

    // 用户ID
    private Long userId;

    // 动态内容
    private String content;

    // 动态图片
    private List<String> images;
}
