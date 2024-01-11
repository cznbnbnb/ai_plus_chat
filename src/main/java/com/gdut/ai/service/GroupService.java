package com.gdut.ai.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.gdut.ai.entity.Group;

public interface GroupService extends IService<Group> {

    boolean createGroup(Long userId, String name, String avatar);

    boolean joinGroup(Long userId, Long groupId,String message);

    boolean handleGroupRequest(Long userId,Long requestId,int type);
}
