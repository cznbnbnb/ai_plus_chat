package com.gdut.ai.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.gdut.ai.entity.User;
import com.gdut.ai.entity.UserSettings;
import com.gdut.ai.view.FriendView;
import com.gdut.ai.view.RequestView;

import java.util.List;

public interface UserService extends IService<User> {

    boolean friendRequest(Long requesterId, String friendEmail, String message);

    List<RequestView> getRequest(Long userId);

    boolean handleFriendRequest(Long userId,Long requestId,int type);

    List<FriendView> getFriendList(Long userId);

    List<User> getAllFriendList(Long userId);

    void updateSettings(UserSettings userSettings, Long userId);

    boolean deleteFriend(Long userId,Long friendId);

    User loginByPassword(String email, String password);
}
