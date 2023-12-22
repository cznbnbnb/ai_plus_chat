package com.gdut.ai.view;

import com.gdut.ai.entity.ChatMessage;
import lombok.Data;

import java.util.List;

@Data
public class FriendView {
    //用户id
    Long userId;
    //用户姓名
    String name;
    //用户头像
    String avatar;
    //用户备注
    String remark;
    //聊天记录
    List<ChatMessage> chatMessage;
}
