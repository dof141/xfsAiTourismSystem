package com.xfs.xfsbackend.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;
import java.math.BigDecimal;
import java.util.Date;

//预约类
@Data
public class ReserveRecord {
    @TableId(type = IdType.AUTO)
    private Long id;
    private String orderNo;     // 订单号
    private Long touristId;     // 游客ID
    private Long areaId;        // 景区ID
    private Long slotId;        // 时段ID
    private String reserveDate; // 游玩日期(用String接收前端传来的 "2026-05-01")
    private BigDecimal payAmount; // 支付金额
    private Integer payStatus;  // 支付状态
    private String verifyCode;  // 核销码
    private Integer status;     // 状态：1待入园 2已核销
    @TableField(fill = FieldFill.INSERT)
    private Date createTime;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private Date updateTime;
}