package com.gdut.ai.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.gdut.ai.entity.ChatMessage;

import java.util.List;

public interface ChatMessageService extends IService<ChatMessage> {

    Page<ChatMessage> getMessages(Long userId, Long friendId, int page, int pageSize);

    boolean deleteMessage(Long userId, Long friendId);
}
