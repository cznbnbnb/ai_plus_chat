package com.gdut.ai.view;

import lombok.Data;

@Data
public class GroupView {
    //组群id
    Long id;
    //组群姓名
    String name;
    //组群头像
    String avatar;
    //组群备注
    String remark;
    //最后一条消息
    String lastMessage;
}
