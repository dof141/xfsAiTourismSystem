package com.xfs.xfsbackend.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;
import java.util.Date;

/**
 * 子景点实体类 - 对应大景区内部的具体游玩点
 */
@Data
public class ScenicSpot {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long areaId;      // 所属大景区ID
    private String name;      // 景点名称
    private String intro;     // 景点详细介绍
    private Double price;     // 门票价格(元)
    private String openTime;  // 开放时间
    private String spotImg;   // 景点图片URL
    private Integer status;   // 状态：1正常 2下架
    private Date createTime;
}
