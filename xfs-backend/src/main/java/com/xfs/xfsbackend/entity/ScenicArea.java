package com.xfs.xfsbackend.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;
import java.util.Date;

/**
 * 大景区实体。
 * 对应 scenic_area 表，保存景区名称、简介、地址、等级、封面图、状态和统计字段。
 */
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
