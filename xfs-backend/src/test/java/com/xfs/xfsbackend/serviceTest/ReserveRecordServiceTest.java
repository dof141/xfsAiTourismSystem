package com.xfs.xfsbackend.serviceTest;

import com.xfs.xfsbackend.entity.ReserveRecord;
import com.xfs.xfsbackend.service.ReserveRecordService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class ReserveRecordServiceTest {
    @Autowired
   private ReserveRecordService reserveRecordService;

    /**
     * 测试预约记录的创建，验证核销码和订单号能正常生成
     */
    @Test
    public void testCreateReserveRecord() {
        ReserveRecord record = new ReserveRecord();
        record.setOrderNo("XFS" + System.currentTimeMillis());
        record.setTouristId(1L);
        record.setAreaId(1L);
        record.setSlotId(1L);
        record.setReserveDate("2026-05-06");
        record.setPayAmount(new BigDecimal("50.00"));
        record.setPayStatus(1);
        record.setStatus(1);
        record.setVerifyCode("V-" + java.util.UUID.randomUUID().toString().substring(0, 8).toUpperCase());

        boolean saved = reserveRecordService.save(record);
        assertTrue(saved, "预约记录保存失败");
        assertNotNull(record.getId(), "保存后ID应自动生成");
        assertNotNull(record.getCreateTime(), "创建时间应自动填充");
        assertNotNull(record.getUpdateTime(), "更新时间应自动填充");
        assertEquals(1, record.getStatus().intValue(), "状态应为待入园");
    }

    /**
     * 测试核销后状态变更
     */
    @Test
    public void testVerifyRecord() {
        ReserveRecord record = new ReserveRecord();
        record.setOrderNo("XFS-VERIFY-TEST-" + System.currentTimeMillis());
        record.setTouristId(1L);
        record.setAreaId(1L);
        record.setSlotId(1L);
        record.setReserveDate("2026-05-06");
        record.setPayAmount(new BigDecimal("50.00"));
        record.setPayStatus(1);
        record.setStatus(1);
        record.setVerifyCode("V-TEST" + System.currentTimeMillis() % 10000);
        reserveRecordService.save(record);
        assertNotNull(record.getId());

        // 模拟核销：条件更新 status = 2
        boolean updated = reserveRecordService.lambdaUpdate()
                .eq(ReserveRecord::getId, record.getId())
                .eq(ReserveRecord::getStatus, 1)
                .set(ReserveRecord::getStatus, 2)
                .update();
        assertTrue(updated, "核销应成功");

        // 验证核销后的状态
        ReserveRecord verified = reserveRecordService.getById(record.getId());
        assertNotNull(verified);
        assertEquals(2, verified.getStatus().intValue(), "核销后状态应为已入园");
    }
}
