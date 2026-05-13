package com.xfs.xfsbackend.service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xfs.xfsbackend.entity.SysAdmin;
import com.xfs.xfsbackend.mapper.SysAdminMapper;
import org.springframework.stereotype.Service;

/**
 * 后台管理员服务。
 * 继承 MyBatis-Plus 通用 Service，供登录认证和管理员信息查询使用。
 */
@Service
public class SysAdminService extends ServiceImpl<SysAdminMapper, SysAdmin> {
}
