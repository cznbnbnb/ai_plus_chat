package com.gdut.ai.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.gdut.ai.entity.User;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface UserMapper extends BaseMapper<User> {
}
