package com.gdut.ai.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.gdut.ai.entity.FriendRequest;
import com.gdut.ai.entity.GroupJoinRequest;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface FriendRequestMapper extends BaseMapper<FriendRequest> {
}
