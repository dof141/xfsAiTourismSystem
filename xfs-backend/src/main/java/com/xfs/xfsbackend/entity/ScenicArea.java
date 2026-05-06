package com.xfs.xfsbackend.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;
import java.util.Date;

//景区类
@Data
public class ScenicArea {
    @TableId(type = IdType.AUTO)
    private Long id;
    private String name;
    private String intro;
    private String address;
    private String level;
    private String tel;
    private String coverImg;
    private Integer status;
    private Date createTime;
    private Integer viewCount;     // 浏览量
    private Integer reserveCount;  // 预约量
}