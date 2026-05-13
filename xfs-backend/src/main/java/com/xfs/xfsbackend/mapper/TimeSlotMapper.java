package com.xfs.xfsbackend.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xfs.xfsbackend.entity.TimeSlot;
import org.apache.ibatis.annotations.Mapper;

/**
 * 入园时段 Mapper。
 * 继承 MyBatis-Plus BaseMapper，提供 time_slot 表的基础 CRUD 能力。
 */
@Mapper
public interface TimeSlotMapper extends BaseMapper<TimeSlot> {}
