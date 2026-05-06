package com.xfs.xfsbackend.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;
import java.util.Date;

@Data
public class TimeSlot {
    @TableId(type = IdType.AUTO)
    private Long id;
    private String slotName;
    private Integer maxPeople; // 这个时段的最大容量
    private Date createTime;
}