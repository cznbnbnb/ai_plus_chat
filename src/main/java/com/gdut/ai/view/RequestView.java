package com.gdut.ai.view;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import com.gdut.ai.entity.FriendRequest;
import com.gdut.ai.entity.GroupJoinRequest;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class RequestView {


    private static final long serialVersionUID = 1L;

    // 申请ID
    @JsonSerialize(using = ToStringSerializer.class)
    private Long id;

    // 发起人ID
    @JsonSerialize(using = ToStringSerializer.class)
    private Long requesterId;

    // 接收人ID
    @JsonSerialize(using = ToStringSerializer.class)
    private Long receiverId;

    // 申请人头像
    private String avatar;

    // 群头像
    private String groupAvatar;

    // 申请群名称
    private String groupName;

    // 申请状态 (例如：0 待审核，1 已接受，2 已拒绝)
    private Integer status;

    // 附加消息
    private String message;

    private LocalDateTime createTime;

    private LocalDateTime updateTime;

    public RequestView(FriendRequest request) {
        this.id = request.getId();
        this.requesterId = request.getRequesterId();
        this.receiverId = request.getReceiverId();
        this.avatar = request.getAvatar();
        this.status = request.getStatus();
        this.message = request.getMessage();
        this.createTime = request.getCreateTime();
        this.updateTime = request.getUpdateTime();
    }

    public RequestView(GroupJoinRequest request) {
        this.id = request.getId();
        this.requesterId = request.getUserId();
        this.groupName = request.getGroupName();
        this.avatar = request.getAvatar();
        this.groupAvatar = request.getGroupAvatar();
        this.status = request.getStatus();
        this.message = request.getMessage();
        this.createTime = request.getCreateTime();
        this.updateTime = request.getUpdateTime();
    }
}
