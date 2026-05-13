package com.xfs.xfsbackend.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xfs.xfsbackend.entity.ScenicSpot;
import org.apache.ibatis.annotations.Mapper;

/**
 * 子景点 Mapper。
 * 继承 MyBatis-Plus BaseMapper，提供 scenic_spot 表的基础 CRUD 能力。
 */
@Mapper
public interface ScenicSpotMapper extends BaseMapper<ScenicSpot> {
}
