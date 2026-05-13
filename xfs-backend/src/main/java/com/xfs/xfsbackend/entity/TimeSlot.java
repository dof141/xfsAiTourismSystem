package com.xfs.xfsbackend.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;
import java.util.Date;

/**
 * 入园时段实体。
 * 对应 time_slot 表，保存可预约时间段名称和该时段最大可预约人数。
 */
@Data
public class TimeSlot {
    @TableId(type = IdType.AUTO)
    private Long id;
    private String slotName;
    private Integer maxPeople; // 这个时段的最大容量
    private Date createTime;
}
