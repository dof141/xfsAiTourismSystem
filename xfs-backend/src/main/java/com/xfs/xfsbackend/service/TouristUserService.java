package com.xfs.xfsbackend.service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xfs.xfsbackend.entity.TouristUser;
import com.xfs.xfsbackend.mapper.TouristUserMapper;
import org.springframework.stereotype.Service;

/**
 * 游客用户服务。
 * 继承 MyBatis-Plus 通用 Service，供游客登录、自动注册和个人信息查询使用。
 */
@Service
public class TouristUserService extends ServiceImpl<TouristUserMapper, TouristUser> {
}
