package com.gdut.ai.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.gdut.ai.entity.Moment;

import java.util.List;

public interface MomentService extends IService<Moment> {

    boolean sendMoment(Long userId, String content, String images);

    Page<Moment> getMoments(Long userId, Integer page, Integer pageSize);

    boolean deleteMoment(Long userId,Long momentId);

    Page<Moment> getMyMoments(Long userId, Integer page, Integer pageSize);
}
