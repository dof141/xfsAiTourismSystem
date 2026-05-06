package com.xfs.xfsbackend.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.xfs.xfsbackend.common.Result;
import com.xfs.xfsbackend.entity.ReserveRecord;
import com.xfs.xfsbackend.service.ReserveRecordService;
import com.xfs.xfsbackend.utils.QrCodeUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.List;

//预约的控制器
@RestController
@RequestMapping("/api/reserve")
@CrossOrigin
public class ReserveController {

    @Autowired
    private ReserveRecordService reserveRecordService;
    @Autowired
    private StringRedisTemplate stringRedisTemplate;

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
     * @param code 传入订单号、核销码
     * @return
     */
    @PostMapping("/verify/{code}")
    public Result<String> verifyOrder(@PathVariable String code) {
      // 1. 去数据库找订单（核心修复：使用 .or() 拼接查询条件）
        QueryWrapper<ReserveRecord> wrapper = new QueryWrapper<>();
        // 翻译成 SQL 就是：WHERE order_no = ? OR verify_code = ?
        wrapper.eq("order_no", code).or().eq("verify_code", code);

        ReserveRecord record = reserveRecordService.getOne(wrapper);

        // 2. 校验逻辑 (保持不变)
        if (record == null) {
            return Result.error("无效的入园码！未找到相关订单。");
        }
        if (record.getStatus() == 2) {
            return Result.error("该二维码已于 " + record.getUpdateTime() + " 被核销，请勿重复入园！");
        }
        if (record.getStatus() != 1) {
            return Result.error("订单状态异常！");
        }

        // 3. 校验通过，修改状态为 2 (已入园) (保持不变)
        record.setStatus(2);
        reserveRecordService.updateById(record);

        return Result.success("核销成功！允许入园。");
    }
    /**
     * 获取个人的预约列表（目前先写死 touristId=1）
      */

    @GetMapping("/myList")
    public Result<List<ReserveRecord>> getMyList() {
        QueryWrapper<ReserveRecord> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("tourist_id", 1L).orderByDesc("create_time");
        return Result.success(reserveRecordService.list(queryWrapper));
    }
    /**
     *     2. 根据订单号，动态生成并获取单张二维码（用完即焚，不占数据库）
     */

    @GetMapping("/qrcode/{orderNo}")
    public Result<String> getQrCode(@PathVariable String orderNo) {
        // 调用我们之前写好的工具类，把订单号变成 Base64 图片
        String qrCodeBase64 = com.xfs.xfsbackend.utils.QrCodeUtil.generateBase64QrCode(orderNo);
        return Result.success(qrCodeBase64);
    }
    /**
     * 新增：高级功能 - 缓存预热接口 (由管理员在每天早晨或放票前调用)
      */

    @GetMapping("/initStock")
    public Result<String> initStock() {
        // 假设我们现在要预热 2026-05-01 这一天，时段ID为 1 (08:00-10:00) 的库存
        // 真实场景下，应该是查数据库遍历所有的时段写入
        String redisKey = "ticket_stock:2026-05-07:1";

        // 设置 Redis 中的初始名额为 500
        stringRedisTemplate.opsForValue().set(redisKey, "500");

        return Result.success("缓存预热成功！Redis 库存已初始化为 500");
    }
}