package com.gdut.ai.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.gdut.ai.common.R;
import com.gdut.ai.entity.GroupTable;
import com.gdut.ai.entity.User;
import com.gdut.ai.view.GroupView;

import java.util.List;

public interface GroupsService extends IService<GroupTable> {

    R<String> createGroup(Long userId, String name, String avatar);

    boolean joinGroup(Long userId, String name,String message);

    boolean handleGroupRequest(Long userId,Long requestId,int type);

    List<GroupTable> getAllGroupList(Long userId);

    List<GroupView> getGroupList(Long userId);

    boolean removeUser(Long userId,Long groupId,Long removeUserId);

    List<User> getGroupMemberList(Long groupId);
}
