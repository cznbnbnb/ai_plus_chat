package com.gdut.ai.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.gdut.ai.entity.ChatMessage;
import com.gdut.ai.entity.Contact;
import com.gdut.ai.entity.FriendRequest;
import com.gdut.ai.entity.User;
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
            LambdaQueryWrapper<ChatMessage> messageWrapper = new LambdaQueryWrapper<>();
            messageWrapper.eq(ChatMessage::getSenderId, user.getId())
                    .eq(ChatMessage::getReceiverId, userId);
            List<ChatMessage> chatMessageList = new ArrayList<>(chatMessageService.list(messageWrapper));
            FriendView friendView = new FriendView();
            if (chatMessageList.size()== 0) {
               continue;
            }
            friendView.setChatMessage(chatMessageList);
            friendView.setUserId(user.getId());
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

}
