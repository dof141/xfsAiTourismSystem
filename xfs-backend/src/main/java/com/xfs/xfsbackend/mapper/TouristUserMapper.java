package com.xfs.xfsbackend.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xfs.xfsbackend.entity.TouristUser;
import org.apache.ibatis.annotations.Mapper;

/**
 * 游客用户 Mapper。
 * 继承 MyBatis-Plus BaseMapper，提供 tourist_user 表的基础 CRUD 和手机号查询能力。
 */
@Mapper
public interface TouristUserMapper extends BaseMapper<TouristUser> {}
