package com.gdut.ai.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.gdut.ai.entity.ChatMessage;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface ChatMessageMapper extends BaseMapper<ChatMessage> {
}
