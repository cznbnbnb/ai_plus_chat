package com.gdut.ai.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.gdut.ai.entity.ChatMessage;
import com.gdut.ai.mapper.ChatMessageMapper;
import com.gdut.ai.service.ChatMessageService;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;

@Service
public class ChatMessageServiceImpl extends ServiceImpl<ChatMessageMapper, ChatMessage> implements ChatMessageService {
    @Override
    public Page<ChatMessage> getMessages(Long userId, Long friendId, int page, int pageSize) {
        if (userId == null || friendId == null) {
            return new Page<>(); // 返回空的分页结果
        }

        // 创建分页配置
        Page<ChatMessage> pageConfig = new Page<>(page, pageSize);

        // 创建查询条件
        LambdaQueryWrapper<ChatMessage> wrapper = new LambdaQueryWrapper<>();
        wrapper
                .nested(nestedWrapper -> nestedWrapper
                        .eq(ChatMessage::getSenderId, userId)
                        .eq(ChatMessage::getReceiverId, friendId)
                )
                .or()
                .nested(nestedWrapper -> nestedWrapper
                        .eq(ChatMessage::getSenderId, friendId)
                        .eq(ChatMessage::getReceiverId, userId)
                )
                .orderByDesc(ChatMessage::getCreateTime); // 排序

        // 执行分页查询
        return this.page(pageConfig, wrapper);
    }

    @Override
    public boolean deleteMessage(Long userId, Long friendId) {
        if (userId == null || friendId == null) {
            return false;
        }
        // 创建查询条件
        LambdaQueryWrapper<ChatMessage> wrapper = new LambdaQueryWrapper<>();
        wrapper
                .nested(nestedWrapper -> nestedWrapper
                        .eq(ChatMessage::getSenderId, userId)
                        .eq(ChatMessage::getReceiverId, friendId)
                )
                .or()
                .nested(nestedWrapper -> nestedWrapper
                        .eq(ChatMessage::getSenderId, friendId)
                        .eq(ChatMessage::getReceiverId, userId)
                );
        // 执行删除
        return this.remove(wrapper);
    }

}
