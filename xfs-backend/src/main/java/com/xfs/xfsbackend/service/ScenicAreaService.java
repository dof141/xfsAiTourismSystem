package com.xfs.xfsbackend.service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xfs.xfsbackend.entity.ScenicArea;
import com.xfs.xfsbackend.mapper.ScenicAreaMapper;
import org.springframework.stereotype.Service;

/**
 * 大景区服务。
 * 继承 MyBatis-Plus 通用 Service，提供景区表的增删改查能力。
 */
@Service
public class ScenicAreaService extends ServiceImpl<ScenicAreaMapper, ScenicArea> {
}
