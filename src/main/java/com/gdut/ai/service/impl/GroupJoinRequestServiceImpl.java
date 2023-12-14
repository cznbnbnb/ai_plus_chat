package com.gdut.ai.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.gdut.ai.entity.GroupJoinRequest;
import com.gdut.ai.mapper.GroupJoinRequestMapper;
import com.gdut.ai.service.GroupJoinRequestService;
import org.springframework.stereotype.Service;

@Service
public class GroupJoinRequestServiceImpl extends ServiceImpl<GroupJoinRequestMapper, GroupJoinRequest> implements GroupJoinRequestService {
}
