package com.gdut.ai.service.impl;

import com.aliyuncs.utils.StringUtils;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.gdut.ai.entity.*;
import com.gdut.ai.mapper.UserMapper;
import com.gdut.ai.service.*;
import com.gdut.ai.view.FriendView;
import com.gdut.ai.view.RequestView;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {

    @Autowired
    private FriendRequestService friendRequestService;

    @Autowired
    private ContactService contactService;

    @Autowired
    private ChatMessageService chatMessageService;

    @Autowired
    private GroupJoinRequestService groupJoinRequestService;

    @Autowired
    private GroupsService groupsService;

    @Override
    public boolean friendRequest(Long requesterId, String friendEmail, String message) {
        //查看是否存在该用户
        LambdaQueryWrapper<User> userWrapper = new LambdaQueryWrapper<>();
        userWrapper.eq(User::getEmail, friendEmail);
        User user = this.getOne(userWrapper);
        User self = this.getById(requesterId);

        // 检查是否已经发送过好友请求
        LambdaQueryWrapper<FriendRequest> requestWrapper = new LambdaQueryWrapper<>();
        requestWrapper.eq(FriendRequest::getRequesterId, requesterId)
                .eq(FriendRequest::getReceiverId, user.getId())
                .eq(FriendRequest::getStatus, 0);
        if (friendRequestService.getOne(requestWrapper) != null) {
            return false;
        }

        // 检查是否已经是好友
        LambdaQueryWrapper<Contact> contactWrapper = new LambdaQueryWrapper<>();
        contactWrapper.eq(Contact::getContactUserId, user.getId())
                .eq(Contact::getUserId, requesterId);
        if (contactService.getOne(contactWrapper) != null) {
            return false;
        }

        // 保存好友请求
        FriendRequest newRequest = new FriendRequest();
        newRequest.setRequesterId(requesterId);
        newRequest.setReceiverId(user.getId());
        newRequest.setAvatar(self.getAvatar());
        newRequest.setMessage(message);
        newRequest.setStatus(0); // 0 代表未处理
        friendRequestService.save(newRequest);

        return true;
    }

    @Override
    public List<RequestView> getRequest(Long userId) {
        List<RequestView> requestViewList = new ArrayList<>();
        try {
            // 处理好友请求
            LambdaQueryWrapper<FriendRequest> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(FriendRequest::getReceiverId, userId)
                    .eq(FriendRequest::getStatus, 0);
            List<FriendRequest> friendRequests = friendRequestService.list(wrapper);
            if (friendRequests != null) {
                friendRequests.forEach(request -> {
                    RequestView requestView = new RequestView(request);
                    requestViewList.add(requestView);
                });
            }

            // 处理群组加入请求

            // 找到用户为群主的群组
            LambdaQueryWrapper<GroupTable> groupsWrapper = new LambdaQueryWrapper<>();
            groupsWrapper.eq(GroupTable::getOwnerId, userId);
            List<GroupTable> groupTables = groupsService.list(groupsWrapper);
            // 遍历群组，找到每个群组的加入请求
            if (groupTables != null) {
                groupTables.forEach(groupTable -> {
                    LambdaQueryWrapper<GroupJoinRequest> wrapper2 = new LambdaQueryWrapper<>();
                    wrapper2.eq(GroupJoinRequest::getGroupId, groupTable.getId())
                            .eq(GroupJoinRequest::getStatus, 0);
                    List<GroupJoinRequest> groupJoinRequests = groupJoinRequestService.list(wrapper2);
                    if (groupJoinRequests != null) {
                        groupJoinRequests.forEach(request -> {
                            RequestView requestView = new RequestView(request);
                            requestViewList.add(requestView);
                        });
                    }
                });
            }
        } catch (Exception e) {
            // 记录异常信息
            log.error("获取好友请求列表失败:" + userId, e);
        }
        return requestViewList;
    }


    @Override
    public boolean handleFriendRequest(Long userId, Long requestId, int type) {
        // 检查是否已经是好友
        LambdaQueryWrapper<Contact> contactWrapper = new LambdaQueryWrapper<>();
        contactWrapper.eq(Contact::getContactUserId, requestId)
                .eq(Contact::getUserId, userId);
        if (contactService.getOne(contactWrapper) != null) {
            return false;
        }

        // 更新好友请求状态
        FriendRequest request = friendRequestService.getById(requestId);
        request.setStatus(type);
        friendRequestService.updateById(request);

        // 如果同意，添加好友
        if (type == 1) {
            Contact contact = new Contact();
            contact.setUserId(userId);
            contact.setContactUserId(request.getRequesterId());
            contactService.save(contact);

            Contact contact2 = new Contact();
            contact2.setUserId(request.getRequesterId());
            contact2.setContactUserId(userId);
            contactService.save(contact2);
        }


        return true;
    }

    @Override
    public List<FriendView> getFriendList(Long userId) {
        LambdaQueryWrapper<Contact> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Contact::getUserId, userId);
        List<Contact> contactList = contactService.list(wrapper);
        List<FriendView> friendViewList = new ArrayList<>();
        // 遍历好友列表，获取好友信息
        for (Contact contact : contactList) {
            User user = this.getById(contact.getContactUserId());
            // 获取好友聊天记录
            Page<ChatMessage> pageConfig = new Page<>(1, 30);
            Long friendId = contact.getContactUserId();
            // 获取最后一条聊天记录
            LambdaQueryWrapper<ChatMessage> messageWrapper = new LambdaQueryWrapper<>();
            messageWrapper.eq(ChatMessage::getSenderId, userId)
                    .eq(ChatMessage::getReceiverId, friendId)
                    .or()
                    .eq(ChatMessage::getSenderId, friendId)
                    .eq(ChatMessage::getReceiverId, userId)
                    .orderByDesc(ChatMessage::getCreateTime)
                    .last("limit 1");
            if (chatMessageService.getOne(messageWrapper) == null) {
                continue;
            }
            FriendView friendView = new FriendView();
            friendView.setLastMessage(chatMessageService.getOne(messageWrapper).getContent());
            friendView.setId(user.getId());
            friendView.setEmail(user.getEmail());
            friendView.setAvatar(user.getAvatar());
            friendView.setName(user.getName());
            friendView.setRemark(contact.getRemark());
            friendViewList.add(friendView);
        }
        return friendViewList;
    }

    @Override
    public List<User> getAllFriendList(Long userId) {
        LambdaQueryWrapper<Contact> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Contact::getUserId, userId);
        List<Contact> contactList = contactService.list(wrapper);
        List<User> userList = new ArrayList<>();
        // 遍历好友列表，获取好友信息
        for (Contact contact : contactList) {
            User user = this.getById(contact.getContactUserId());
            userList.add(user);
        }
        desensitization(userList);
        return userList;
    }

    @Override
    public void updateSettings(UserSettings userSettings, Long userId) {
        User user = this.getById(userId);
        if (userSettings == null) {
            throw new RuntimeException("用户设置为空");
        }
        if (userSettings.getEmail() == null) {
            throw new RuntimeException("用户设置为空");
        }
        if (user == null) {
            throw new RuntimeException("用户不存在");
        }
        user.setName(userSettings.getName());
        user.setSex(userSettings.getSex());
        user.setAvatar(userSettings.getAvatar());
        String oldPassword = userSettings.getOldPassword();
        String newPassword = userSettings.getNewPassword();
        if (StringUtils.isEmpty(user.getPassword())&&!StringUtils.isEmpty(newPassword)){
            user.setPassword(newPassword);
        } else if (!StringUtils.isEmpty(oldPassword)&&!StringUtils.isEmpty(newPassword)) {
            if (!oldPassword.equals(user.getPassword())) {
                throw new RuntimeException("原密码错误");
            }
            user.setPassword(newPassword);
        }
        this.updateById(user);
    }

    @Override
    public boolean deleteFriend(Long userId, Long friendId) {
        LambdaQueryWrapper<Contact> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Contact::getUserId, userId)
                .eq(Contact::getContactUserId, friendId);
        contactService.remove(wrapper);
        LambdaQueryWrapper<Contact> wrapper2 = new LambdaQueryWrapper<>();
        wrapper2.eq(Contact::getUserId, friendId)
                .eq(Contact::getContactUserId, userId);
        contactService.remove(wrapper2);
        return true;
    }



    @Override
    public User loginByPassword(String email, String password) {
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(User::getEmail, email)
                .eq(User::getPassword, password);
        User user = this.getOne(wrapper);
        if (user != null) {
            desensitization(user);
        }
        return user;
    }

    //进行单个用户的密码脱敏
    private void desensitization(User user) {
        user.setPassword("");
    }

    //进行用户列表的密码脱敏
    private void desensitization(List<User> userList) {
        for (User user : userList) {
            user.setPassword("");
        }
    }

}
