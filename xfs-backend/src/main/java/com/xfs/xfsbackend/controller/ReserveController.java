package com.xfs.xfsbackend.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.xfs.xfsbackend.common.Result;
import com.xfs.xfsbackend.entity.ReserveRecord;
import com.xfs.xfsbackend.service.ReserveRecordService;
import com.xfs.xfsbackend.utils.QrCodeUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

//预约的控制器
@RestController
@RequestMapping("/api/reserve")
@CrossOrigin
public class ReserveController {

    @Autowired
    private ReserveRecordService reserveRecordService;

    /**
     * 游客进行预约的控制器 用于返回预约的信息
     *
     * @param record
     * @return
     */
    @PostMapping("/add")
    public Result<String> addReserve(@RequestBody ReserveRecord record) {
        // 调用我们刚才写的 Service 核心逻辑
        String msg = reserveRecordService.doReserve(record);

        if ("success".equals(msg)) {
            String qrCodeBase64 = QrCodeUtil.generateBase64QrCode(record.getOrderNo());
            // 把动态生成的 base64 图片塞进 Result 的 data 字段里返回给小程序
            return Result.success(qrCodeBase64);
//            return Result.success("预约成功！请在规定时段内入园。");
        } else {
            return Result.error(msg);
        }
    }

    /**
     * 后端管理员进行二维码核心
     *      * 新增：扫描二维码进行核销的接口
     * @param orderNo 传入订单号、
     * @return
     */
    @PostMapping("/verify/{orderNo}")
    public Result<String> verifyOrder(@PathVariable String orderNo) {
        // 1. 根据二维码里的订单号去数据库找订单
       QueryWrapper<ReserveRecord> wrapper = new QueryWrapper<>();
        wrapper.eq("order_no", orderNo);
        ReserveRecord record = reserveRecordService.getOne(wrapper);

        // 2. 校验逻辑
        if (record == null) {
            return Result.error("无效的入园码！未找到相关订单。");
        }
        if (record.getStatus() == 2) {
            return Result.error("该二维码已于 " + record.getUpdateTime() + " 被核销，请勿重复入园！");
        }
        if (record.getStatus() != 1) {
            return Result.error("订单状态异常！");
        }

        // 3. 校验通过，修改状态为 2 (已入园)
        record.setStatus(2);
        // 这里会自动更新 update_time，记录入园时间
        reserveRecordService.updateById(record);

        return Result.success("核销成功！允许入园。");
    }
}