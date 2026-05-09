package com.xfs.xfsbackend.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.xfs.xfsbackend.common.Result;
import com.xfs.xfsbackend.entity.ReserveRecord;
import com.xfs.xfsbackend.entity.TimeSlot;
import com.xfs.xfsbackend.service.ReserveRecordService;
import com.xfs.xfsbackend.service.TimeSlotService;
import com.xfs.xfsbackend.utils.QrCodeUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.Date;
import java.util.List;

/**
 * 预约的控制器
 */
@RestController
@RequestMapping("/api/reserve")
public class ReserveController {

    @Autowired
    private ReserveRecordService reserveRecordService;
    @Autowired
    private StringRedisTemplate stringRedisTemplate;
    @Autowired
    private TimeSlotService timeSlotService;

    /**
     * 游客进行预约的控制器
     */
    @PostMapping("/add")
    public Result<String> addReserve(@RequestBody ReserveRecord record, HttpServletRequest request) {
        // 尝试从 JWT 拦截器存入的 request 属性中获取用户 ID
        Object userId = request.getAttribute("adminId");
        if (userId != null) {
            record.setTouristId(Long.valueOf(userId.toString()));
        }

        // 调用 Service 核心逻辑
        String msg = reserveRecordService.doReserve(record);

        if ("success".equals(msg)) {
            String qrCodeBase64 = QrCodeUtil.generateBase64QrCode(record.getOrderNo());
            return Result.success(qrCodeBase64);
        } else {
            return Result.error(msg);
        }
    }

    /**
     * 后端管理员扫描二维码或输入代码进行核销
     */
    @PostMapping("/verify/{code}")
    public Result<String> verifyOrder(@PathVariable String code) {
        QueryWrapper<ReserveRecord> wrapper = new QueryWrapper<>();
        wrapper.eq("order_no", code).or().eq("verify_code", code);

        ReserveRecord record = reserveRecordService.getOne(wrapper, false);

        if (record == null) {
            return Result.error("无效的入园码！未找到相关订单。");
        }
        if (record.getStatus() == 2) {
            return Result.error("该二维码已于 " + record.getUpdateTime() + " 被核销，请勿重复入园！");
        }
        if (record.getStatus() != 1) {
            return Result.error("订单状态异常！");
        }

        boolean updated = reserveRecordService.lambdaUpdate()
                .eq(ReserveRecord::getId, record.getId())
                .eq(ReserveRecord::getStatus, 1)
                .set(ReserveRecord::getStatus, 2)
                .set(ReserveRecord::getUpdateTime, new Date())
                .update();

        if (!updated) {
            return Result.error("该订单已被核销或状态异常，请刷新后重试！");
        }
        return Result.success("核销成功！允许入园。");
    }

    /**
     * 获取个人的预约列表
     */
    @GetMapping("/myList")
    public Result<List<ReserveRecord>> getMyList(HttpServletRequest request) {
        Long userId = 1L; // 默认值
        Object userIdAttr = request.getAttribute("adminId");
        if (userIdAttr != null) {
            userId = Long.valueOf(userIdAttr.toString());
        }

        QueryWrapper<ReserveRecord> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("tourist_id", userId).orderByDesc("create_time");
        return Result.success(reserveRecordService.list(queryWrapper));
    }

    /**
     * 根据订单号动态生成二维码
     */
    @GetMapping("/qrcode/{orderNo}")
    public Result<String> getQrCode(@PathVariable String orderNo) {
        String qrCodeBase64 = QrCodeUtil.generateBase64QrCode(orderNo);
        return Result.success(qrCodeBase64);
    }

    /**
     * 缓存预热接口
     */
    @GetMapping("/initStock")
    public Result<String> initStock(@RequestParam(required = false) String date) {
        if (date == null || date.isEmpty()) {
            date = java.time.LocalDate.now().plusDays(1).toString();
        }

        List<TimeSlot> slotList = timeSlotService.list();
        if (slotList.isEmpty()) {
            return Result.error("请先配置入园时段！");
        }

        int count = 0;
        for (TimeSlot slot : slotList) {
            String redisKey = "ticket_stock:" + date + ":" + slot.getId();
            Boolean wasSet = stringRedisTemplate.opsForValue().setIfAbsent(redisKey, String.valueOf(slot.getMaxPeople()));
            if (Boolean.TRUE.equals(wasSet)) {
                count++;
            }
        }
        return Result.success("缓存预热完成！日期：" + date);
    }
}
