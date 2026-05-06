package com.xfs.xfsbackend.serviceTest;

import com.xfs.xfsbackend.entity.ReserveRecord;
import com.xfs.xfsbackend.service.ReserveRecordService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@SpringBootTest
public class ReserveRecordServiceTest {
    @Autowired
   private ReserveRecordService reserveRecordService;

    /**
     * 测试核销码的唯一性
     */
    @Test
    public void setVerifycodeTest(){
        ReserveRecord record = new ReserveRecord();
        record.setOrderNo("XFS12345678901");
        record.setTouristId(1L);
        record.setAreaId(1L);
        record.setSlotId(1L);
        record.setReserveDate("2026-05-06");
        record.setPayAmount(new BigDecimal("50.00"));
        record.setPayStatus(1);       // 1 已支付
        record.setVerifyCode("ABC123");
        record.setStatus(1);          // 1 待入园
        record.setVerifyCode("V-123456");
        for (int i = 0; i < 2; i++) {
            reserveRecordService.save(record);
            record.setOrderNo(record.getOrderNo()+i);
        }
    }
}
