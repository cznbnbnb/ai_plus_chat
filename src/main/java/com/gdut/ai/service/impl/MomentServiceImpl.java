package com.gdut.ai.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.gdut.ai.entity.Contact;
import com.gdut.ai.entity.Moment;
import com.gdut.ai.entity.User;
import com.gdut.ai.mapper.MomentMapper;
import com.gdut.ai.service.ContactService;
import com.gdut.ai.service.MomentService;
import com.gdut.ai.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Service
public class MomentServiceImpl extends ServiceImpl<MomentMapper, Moment> implements MomentService {

    @Autowired
    private ContactService contactService;

    @Autowired
    private UserService userService;

    @Override
    public boolean sendMoment(Long userId, String content, String images) {
        Moment moment = new Moment();
        User user = userService.getById(userId);
        if (user == null) {
            return false;
        }
        moment.setUserId(userId);
        moment.setName(user.getName());
        moment.setAvatar(user.getAvatar());
        moment.setContent(content);
        moment.setImages(images);
        return save(moment);
    }

    @Override
    public Page<Moment> getMoments(Long userId, Integer page, Integer pageSize) {
        //查询出自己所有的好友，包括自己，然后根据好友的id查询出所有的朋友圈，然后根据时间排序
        LambdaQueryWrapper<Contact> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Contact::getUserId, userId);
        List<Contact> contactList = contactService.list(wrapper);
        List<Long> userIds = new ArrayList<>();
        // 遍历好友列表，获取好友信息
        for (Contact contact : contactList) {
            userIds.add(contact.getContactUserId());
        }
        // 添加自己的id
        userIds.add(userId);
        // 查询朋友圈
        LambdaQueryWrapper<Moment> momentWrapper = new LambdaQueryWrapper<>();
        momentWrapper.in(Moment::getUserId, userIds);
        momentWrapper.orderByDesc(Moment::getCreateTime);
        Page<Moment> moments = new Page<>(page, pageSize);
        return page(moments, momentWrapper);
    }

    @Override
    public boolean deleteMoment(Long userId, Long momentId) {
        //检查是否是自己的朋友圈
        Moment moment = getById(momentId);
        if (moment.getUserId().equals(userId)) {
            return removeById(momentId);
        }
        return false;
    }

    @Override
    public Page<Moment> getMyMoments(Long userId, Integer page, Integer pageSize) {
        LambdaQueryWrapper<Moment> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Moment::getUserId, userId);
        wrapper.orderByDesc(Moment::getCreateTime);
        Page<Moment> moments = new Page<>(page, pageSize);
        return page(moments, wrapper);
    }


}
