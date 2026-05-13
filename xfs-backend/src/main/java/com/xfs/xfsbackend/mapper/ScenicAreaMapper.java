package com.xfs.xfsbackend.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xfs.xfsbackend.entity.ScenicArea;
import org.apache.ibatis.annotations.Mapper;

/**
 * 大景区 Mapper。
 * 继承 MyBatis-Plus BaseMapper，提供 scenic_area 表的基础 CRUD 能力。
 */
@Mapper
public interface ScenicAreaMapper extends BaseMapper<ScenicArea> {
}
