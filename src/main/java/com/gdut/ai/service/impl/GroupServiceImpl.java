package com.gdut.ai.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.gdut.ai.entity.*;
import com.gdut.ai.mapper.GroupMapper;
import com.gdut.ai.service.GroupJoinRequestService;
import com.gdut.ai.service.GroupMemberService;
import com.gdut.ai.service.GroupService;
import com.gdut.ai.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class GroupServiceImpl extends ServiceImpl<GroupMapper, Group> implements GroupService {

    @Autowired
    UserService userService;

    @Autowired
    GroupJoinRequestService groupJoinRequestService;

    @Autowired
    GroupMemberService groupMemberService;

    @Override
    public boolean createGroup(Long userId, String name, String avatar) {
        User user = userService.getById(userId);
        if (user == null) {
            return false;
        }

        Group group = new Group();
        group.setOwnerId(userId);
        group.setName(name);
        group.setAvatar(avatar);
        return save(group);
    }

    @Override
    public boolean joinGroup(Long userId, Long groupId, String message) {
        // 检查是否已经是群成员
        LambdaQueryWrapper<Group> groupWrapper = new LambdaQueryWrapper<>();
        groupWrapper.eq(Group::getId, groupId);
        Group group = this.getOne(groupWrapper);
        if (group == null) {
            // 没群加个锤子
            return false;
        }

        LambdaQueryWrapper<GroupMember> contactWrapper = new LambdaQueryWrapper<>();
        contactWrapper.eq(GroupMember::getUserId, userId)
                .eq(GroupMember::getGroupId, groupId);
        if (groupMemberService.getOne(contactWrapper) != null) {
            // 已经是群成员了
            return false;
        }

        // 检查是否已经发送过入群申请
        LambdaQueryWrapper<GroupJoinRequest> requestWrapper = new LambdaQueryWrapper<>();
        requestWrapper.eq(GroupJoinRequest::getUserId, userId)
                .eq(GroupJoinRequest::getGroupId, groupId)
                .eq(GroupJoinRequest::getStatus, 0);
        if (groupJoinRequestService.getOne(requestWrapper) != null) {
            return false;
        }

        // 保存入群申请
        GroupJoinRequest newRequest = new GroupJoinRequest();
        newRequest.setUserId(userId);
        newRequest.setGroupId(groupId);
        newRequest.setMessage(message);
        newRequest.setStatus(0); // 0 代表未处理
        groupJoinRequestService.save(newRequest);

        return true;
    }

    @Override
    public boolean handleGroupRequest(Long userId, Long requestId, int type) {

        GroupJoinRequest request = groupJoinRequestService.getById(requestId);
        if (request == null) {
            return false;
        }

        // 更新入群申请状态
        request.setStatus(type);
        groupJoinRequestService.updateById(request);

        // 如果同意，添加群成员
        if (type == 1) {
            GroupMember member = new GroupMember();
            member.setUserId(request.getUserId());
            member.setGroupId(request.getGroupId());
            groupMemberService.save(member);
        }

        return true;
    }

}
