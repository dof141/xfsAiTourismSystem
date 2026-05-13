package com.xfs.xfsbackend.service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xfs.xfsbackend.entity.ScenicSpot;
import com.xfs.xfsbackend.mapper.ScenicSpotMapper;
import org.springframework.stereotype.Service;

/**
 * 子景点服务。
 * 继承 MyBatis-Plus 通用 Service，提供景区内部景点的增删改查能力。
 */
@Service
public class ScenicSpotService extends ServiceImpl<ScenicSpotMapper, ScenicSpot> {
}
