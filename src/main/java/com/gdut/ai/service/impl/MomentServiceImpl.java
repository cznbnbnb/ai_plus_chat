package com.gdut.ai.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.gdut.ai.entity.Moment;
import com.gdut.ai.mapper.MomentMapper;
import com.gdut.ai.service.MomentService;
import org.springframework.stereotype.Service;

@Service
public class MomentServiceImpl extends ServiceImpl<MomentMapper, Moment> implements MomentService {
}
