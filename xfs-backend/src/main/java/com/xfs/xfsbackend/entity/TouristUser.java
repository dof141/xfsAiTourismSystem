package com.xfs.xfsbackend.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;
import java.util.Date;

/**
 * 游客用户实体。
 * 对应 tourist_user 表，保存小程序游客的 openid、昵称、头像、真实姓名和手机号。
 */
@Data
public class TouristUser {
    @TableId(type = IdType.AUTO)
    private Long id;
    private String openid;
    private String nickname;
    private String avatar;
    private String realName;
    private String phone;
    private Date createTime;
}
