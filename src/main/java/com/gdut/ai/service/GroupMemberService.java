package com.gdut.ai.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.gdut.ai.entity.GroupMember;
import com.gdut.ai.entity.User;

import java.util.List;

public interface GroupMemberService extends IService<GroupMember> {

    List<Long> getGroupMemberIds(Long groupId);
}
