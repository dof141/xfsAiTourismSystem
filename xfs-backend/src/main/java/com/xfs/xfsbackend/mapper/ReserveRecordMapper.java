package com.xfs.xfsbackend.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xfs.xfsbackend.entity.ReserveRecord;
import org.apache.ibatis.annotations.Mapper;

/**
 * 预约订单 Mapper。
 * 继承 MyBatis-Plus BaseMapper，提供 reserve_record 表的基础 CRUD 能力。
 */
@Mapper
public interface ReserveRecordMapper extends BaseMapper<ReserveRecord> {}
