package com.gdut.ai.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.gdut.ai.entity.Contact;
import com.gdut.ai.entity.FriendRequest;
import com.gdut.ai.entity.User;
import com.gdut.ai.entity.UserSettings;
import com.gdut.ai.view.FriendView;

import java.util.List;

public interface UserService extends IService<User> {

    boolean friendRequest(Long requesterId, String friendEmail, String message);

    List<FriendRequest> getFriendRequest(Long userId);

    boolean handleFriendRequest(Long userId,Long requestId,int type);

    List<FriendView> getFriendList(Long userId);

    List<User> getAllFriendList(Long userId);

    void updateSettings(UserSettings userSettings, Long userId);

    boolean deleteFriend(Long userId,Long friendId);


}
