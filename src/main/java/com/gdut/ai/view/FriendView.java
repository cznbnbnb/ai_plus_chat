package com.gdut.ai.view;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.gdut.ai.entity.ChatMessage;
import lombok.Data;

import java.util.List;

@Data
public class FriendView {
    //用户id
    Long id;
    //用户姓名
    String name;
    //用户头像
    String avatar;
    //用户备注
    String remark;
    //用户邮箱
    String email;
    //最后一条消息
    String lastMessage;
}
