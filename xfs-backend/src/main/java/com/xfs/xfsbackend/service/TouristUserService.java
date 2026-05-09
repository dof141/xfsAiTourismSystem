package com.xfs.xfsbackend.service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xfs.xfsbackend.entity.TouristUser;
import com.xfs.xfsbackend.mapper.TouristUserMapper;
import org.springframework.stereotype.Service;

@Service
public class TouristUserService extends ServiceImpl<TouristUserMapper, TouristUser> {
}
