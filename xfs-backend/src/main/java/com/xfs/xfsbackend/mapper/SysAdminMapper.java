package com.xfs.xfsbackend.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xfs.xfsbackend.entity.SysAdmin;
import org.apache.ibatis.annotations.Mapper;

/**
 * 后台管理员 Mapper。
 * 继承 MyBatis-Plus BaseMapper，提供 sys_admin 表的基础 CRUD 和登录查询能力。
 */
@Mapper
public interface SysAdminMapper extends BaseMapper<SysAdmin> {
}
