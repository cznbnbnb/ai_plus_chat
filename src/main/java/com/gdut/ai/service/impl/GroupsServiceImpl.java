package com.gdut.ai.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.gdut.ai.common.R;
import com.gdut.ai.entity.*;
import com.gdut.ai.mapper.GroupsMapper;
import com.gdut.ai.service.*;
import com.gdut.ai.view.FriendView;
import com.gdut.ai.view.GroupView;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Service
@Slf4j
public class GroupsServiceImpl extends ServiceImpl<GroupsMapper, GroupTable> implements GroupsService {

    @Autowired
    UserService userService;

    @Autowired
    GroupJoinRequestService groupJoinRequestService;

    @Autowired
    GroupMemberService groupMemberService;

    @Autowired
    ChatMessageService chatMessageService;

    @Override
    public R<String> createGroup(Long userId, String name, String avatar) {
        User user = userService.getById(userId);
        if (user == null) {
            return R.error("用户不存在");
        }

        GroupTable groupTable = new GroupTable();
        groupTable.setOwnerId(userId);
        //群名称不能重复
        LambdaQueryWrapper<GroupTable> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(GroupTable::getName, name);
        if (this.getOne(wrapper) != null) {
            return R.error("群名称已存在，请重新输入");
        }
        groupTable.setName(name);
        groupTable.setAvatar(avatar);
        save(groupTable);

        GroupMember member = new GroupMember();
        member.setUserId(userId);
        member.setGroupId(groupTable.getId());
        groupMemberService.save(member);
        return R.success("创建群组成功");
    }

    @Override
    public boolean joinGroup(Long userId, String groupName, String message) {
        User user = userService.getById(userId);

        // 检查是否已经是群成员
        LambdaQueryWrapper<GroupTable> groupWrapper = new LambdaQueryWrapper<>();
        groupWrapper.eq(GroupTable::getName, groupName);
        GroupTable groupTable = this.getOne(groupWrapper);
        if (groupTable == null) {
            // 没群加个锤子
            return false;
        }

        LambdaQueryWrapper<GroupMember> contactWrapper = new LambdaQueryWrapper<>();
        contactWrapper.eq(GroupMember::getUserId, userId)
                .eq(GroupMember::getGroupId, groupTable.getId());
        if (groupMemberService.getOne(contactWrapper) != null) {
            // 已经是群成员了
            return false;
        }

        // 检查是否已经发送过入群申请
        LambdaQueryWrapper<GroupJoinRequest> requestWrapper = new LambdaQueryWrapper<>();
        requestWrapper.eq(GroupJoinRequest::getUserId, userId)
                .eq(GroupJoinRequest::getGroupId, groupTable.getId())
                .eq(GroupJoinRequest::getStatus, 0);
        if (groupJoinRequestService.getOne(requestWrapper) != null) {
            return false;
        }

        // 保存入群申请
        GroupJoinRequest newRequest = new GroupJoinRequest();
        newRequest.setUserId(userId);
        newRequest.setGroupId(groupTable.getId());
        newRequest.setGroupName(groupTable.getName());
        newRequest.setGroupAvatar(groupTable.getAvatar());
        newRequest.setAvatar(user.getAvatar());
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

    @Override
    public List<GroupTable> getAllGroupList(Long userId) {
        LambdaQueryWrapper<GroupMember> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(GroupMember::getUserId, userId);
        List<GroupMember> groupMemberList = groupMemberService.list(wrapper);
        List<GroupTable> groupTableList = new ArrayList<>();
        // 遍历群成员列表，获取群信息
        for (GroupMember groupMember : groupMemberList) {
            GroupTable groupTable = this.getById(groupMember.getGroupId());
            groupTableList.add(groupTable);
        }
        return groupTableList;
    }

    @Override
    public List<GroupView> getGroupList(Long userId) {
        LambdaQueryWrapper<GroupMember> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(GroupMember::getUserId, userId);
        List<GroupMember> groupMemberList = groupMemberService.list(wrapper);
        // 流处理获取群号
        List<Long> groupIdList = groupMemberList
                .stream().map(GroupMember::getGroupId)
                .collect(java.util.stream.Collectors.toList());
        List<GroupView> groupViewList = new ArrayList<>();
        // 遍历群号列表，获取群信息
        for (Long groupId : groupIdList) {
            GroupTable groupTable = this.getById(groupId);
            // 获取最后一条消息
            LambdaQueryWrapper<ChatMessage> messageWrapper = new LambdaQueryWrapper<>();
            messageWrapper.eq(ChatMessage::getReceiverId, groupId)
                    .orderByDesc(ChatMessage::getCreateTime)
                    .last("limit 1");
            ChatMessage message = chatMessageService.getOne(messageWrapper);

            if (message == null) {
                continue;
            }
            log.info("last message: {}", message);
            GroupView groupView = new GroupView();
            groupView.setLastMessage(message.getContent());
            groupView.setId(groupTable.getId());
            groupView.setAvatar(groupTable.getAvatar());
            groupView.setName(groupTable.getName());
            groupView.setRemark(groupTable.getName());
            groupViewList.add(groupView);
        }
        return groupViewList;
    }

    @Override
    public boolean removeUser(Long userId, Long groupId, Long removeUserId) {
        // 检查是否是群主或者自己
        GroupTable groupTable = this.getById(groupId);
        if (!Objects.equals(groupTable.getOwnerId(), userId)&&!Objects.equals(userId, removeUserId)) {
            log.info("{}不是群主又不是自己，无法移除{}",userId,removeUserId);
            return false;
        }

        //如果是要移除的是群主，直接解散该群，移除该群全部成员，再移除相关申请，移除聊天记录，再移除该群
        if(Objects.equals(groupTable.getOwnerId(), removeUserId)){
            // 删除群成员
            LambdaQueryWrapper<GroupMember> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(GroupMember::getGroupId, groupId);
            groupMemberService.remove(wrapper);

            // 删除群申请
            LambdaQueryWrapper<GroupJoinRequest> requestWrapper = new LambdaQueryWrapper<>();
            requestWrapper.eq(GroupJoinRequest::getGroupId, groupId);
            groupJoinRequestService.remove(requestWrapper);

            // 删除聊天记录
            LambdaQueryWrapper<ChatMessage> messageWrapper = new LambdaQueryWrapper<>();
            messageWrapper.eq(ChatMessage::getReceiverId, groupId);
            chatMessageService.remove(messageWrapper);

            this.removeById(groupId);
            log.info("{}解散群聊{}成功",userId,groupId);
            return true;
        }

        // 删除群成员
        LambdaQueryWrapper<GroupMember> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(GroupMember::getUserId, removeUserId)
                .eq(GroupMember::getGroupId, groupId);
        groupMemberService.remove(wrapper);
        log.info("{}将{}移出群聊{}成功",userId,removeUserId,groupId);
        return true;
    }

    @Override
    public List<User> getGroupMemberList(Long groupId) {
        LambdaQueryWrapper<GroupMember> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(GroupMember::getGroupId, groupId);
        List<GroupMember> groupMemberList = groupMemberService.list(wrapper);
        List<User> userList = new ArrayList<>();
        // 遍历群成员列表，获取群信息
        for (GroupMember groupMember : groupMemberList) {
            User user = userService.getById(groupMember.getUserId());
            userList.add(user);
        }
        return userList;
    }


}
