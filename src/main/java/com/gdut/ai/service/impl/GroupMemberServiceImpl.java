package com.gdut.ai.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.gdut.ai.entity.GroupMember;
import com.gdut.ai.mapper.GroupMemberMapper;
import com.gdut.ai.service.GroupMemberService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class GroupMemberServiceImpl extends ServiceImpl<GroupMemberMapper, GroupMember> implements GroupMemberService {
    @Override
    public List<Long> getGroupMemberIds(Long groupId) {
        LambdaQueryWrapper<GroupMember> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(GroupMember::getGroupId, groupId);
        List<GroupMember> groupMembers = this.list(wrapper);
        return groupMembers.stream().map(GroupMember::getUserId).collect(Collectors.toList());
    }
}
