package com.gdut.ai.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.gdut.ai.entity.FriendRequest;
import com.gdut.ai.mapper.FriendRequestMapper;
import com.gdut.ai.service.FriendRequestService;
import org.springframework.stereotype.Service;

@Service
public class FriendRequestServiceImpl extends ServiceImpl<FriendRequestMapper, FriendRequest> implements FriendRequestService {
}
