package com.gdut.ai.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.gdut.ai.entity.*;
import com.gdut.ai.mapper.UserMapper;
import com.gdut.ai.service.ChatMessageService;
import com.gdut.ai.service.ContactService;
import com.gdut.ai.service.FriendRequestService;
import com.gdut.ai.service.UserService;
import com.gdut.ai.view.FriendView;
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

    @Override
    public boolean friendRequest(Long requesterId, String friendEmail, String message) {
        //查看是否存在该用户
        LambdaQueryWrapper<User> userWrapper = new LambdaQueryWrapper<>();
        userWrapper.eq(User::getEmail, friendEmail);
        User user = this.getOne(userWrapper);

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
        newRequest.setMessage(message);
        newRequest.setStatus(0); // 0 代表未处理
        friendRequestService.save(newRequest);

        return true;
    }

    @Override
    public List<FriendRequest> getFriendRequest(Long userId) {
        LambdaQueryWrapper<FriendRequest> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(FriendRequest::getReceiverId, userId)
                .eq(FriendRequest::getStatus, 0);
        return friendRequestService.list(wrapper);
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
        return userList;
    }

    @Override
    public void updateSettings(UserSettings userSettings, Long userId) {
        User user = this.getById(userId);
        if (user == null) {
            throw new RuntimeException("用户不存在");
        }
        user.setName(userSettings.getName());
        user.setSex(userSettings.getSex());
        user.setAvatar(userSettings.getAvatar());
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

}
