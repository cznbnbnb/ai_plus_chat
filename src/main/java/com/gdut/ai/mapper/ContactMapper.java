package com.gdut.ai.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.gdut.ai.entity.Contact;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface ContactMapper extends BaseMapper<Contact> {
}
