package com.xfs.xfsbackend.service;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.xfs.xfsbackend.entity.ReserveRecord;
import com.xfs.xfsbackend.entity.ScenicArea;
import com.xfs.xfsbackend.entity.TimeSlot;
import com.xfs.xfsbackend.mapper.ReserveRecordMapper;
import com.xfs.xfsbackend.mapper.ScenicAreaMapper;
import com.xfs.xfsbackend.mapper.TimeSlotMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ReserveRecordServiceValidationTest {

    private ReserveRecordService reserveRecordService;

    @Mock
    private ReserveRecordMapper reserveRecordMapper;

    @Mock
    private TimeSlotMapper timeSlotMapper;

    @Mock
    private ScenicAreaMapper scenicAreaMapper;

    @Mock
    private StringRedisTemplate stringRedisTemplate;

    @Mock
    private ValueOperations<String, String> valueOperations;

    @BeforeEach
    void setUp() {
        reserveRecordService = new ReserveRecordService();
        ReflectionTestUtils.setField(reserveRecordService, "baseMapper", reserveRecordMapper);
        ReflectionTestUtils.setField(reserveRecordService, "timeSlotMapper", timeSlotMapper);
        ReflectionTestUtils.setField(reserveRecordService, "scenicAreaMapper", scenicAreaMapper);
        ReflectionTestUtils.setField(reserveRecordService, "stringRedisTemplate", stringRedisTemplate);
    }

    @Test
    void validateReserveRejectsPastDate() {
        ReserveRecord record = validRecord();
        record.setReserveDate(LocalDate.now().minusDays(1).toString());

        String result = reserveRecordService.validateReserve(record);

        assertEquals("不能预约过去的日期", result);
    }

    @Test
    void validateReserveRejectsOfflineArea() {
        ReserveRecord record = validRecord();
        ScenicArea offlineArea = new ScenicArea();
        offlineArea.setId(1L);
        offlineArea.setStatus(0);
        when(scenicAreaMapper.selectById(1L)).thenReturn(offlineArea);

        String result = reserveRecordService.validateReserve(record);

        assertEquals("预约景区不存在或已下架", result);
    }

    @Test
    void validateReserveRejectsMissingTimeSlot() {
        ReserveRecord record = validRecord();
        when(scenicAreaMapper.selectById(1L)).thenReturn(activeArea());
        when(timeSlotMapper.selectById(1L)).thenReturn(null);

        String result = reserveRecordService.validateReserve(record);

        assertEquals("预约时段不存在！", result);
    }

    @Test
    void validateReserveRejectsDuplicateActiveReservation() {
        ReserveRecord record = validRecord();
        when(scenicAreaMapper.selectById(1L)).thenReturn(activeArea());
        when(timeSlotMapper.selectById(1L)).thenReturn(validSlot());
        when(reserveRecordMapper.selectCount(any(Wrapper.class))).thenReturn(1L);

        String result = reserveRecordService.validateReserve(record);

        assertEquals("您已预约过该景区当前日期和时段，请勿重复预约", result);
    }

    @Test
    void doReserveCreatesOrderWhenValidationAndStockPass() {
        ReserveRecord record = validRecord();
        when(scenicAreaMapper.selectById(1L)).thenReturn(activeArea());
        when(timeSlotMapper.selectById(1L)).thenReturn(validSlot());
        when(reserveRecordMapper.selectCount(any(Wrapper.class))).thenReturn(0L);
        when(stringRedisTemplate.hasKey(anyString())).thenReturn(true);
        when(stringRedisTemplate.opsForValue()).thenReturn(valueOperations);
        when(valueOperations.decrement(anyString())).thenReturn(9L);
        when(reserveRecordMapper.insert(record)).thenReturn(1);

        String result = reserveRecordService.doReserve(record);

        assertEquals("success", result);
        assertNotNull(record.getOrderNo());
        assertNotNull(record.getVerifyCode());
        assertEquals(1, record.getStatus());
        assertEquals(1, record.getPayStatus());
        verify(valueOperations).decrement(eq("ticket_stock:1:" + record.getReserveDate() + ":1"));
    }

    @Test
    void doReserveUsesAreaIdInRedisStockKey() {
        ReserveRecord record = validRecord();
        record.setAreaId(2L);
        when(scenicAreaMapper.selectById(2L)).thenReturn(activeArea(2L));
        when(timeSlotMapper.selectById(1L)).thenReturn(validSlot());
        when(reserveRecordMapper.selectCount(any(Wrapper.class))).thenReturn(0L);
        when(stringRedisTemplate.hasKey(anyString())).thenReturn(true);
        when(stringRedisTemplate.opsForValue()).thenReturn(valueOperations);
        when(valueOperations.decrement(anyString())).thenReturn(9L);
        when(reserveRecordMapper.insert(record)).thenReturn(1);

        String result = reserveRecordService.doReserve(record);

        assertEquals("success", result);
        verify(stringRedisTemplate).hasKey("ticket_stock:2:" + record.getReserveDate() + ":1");
        verify(valueOperations).decrement("ticket_stock:2:" + record.getReserveDate() + ":1");
    }

    @Test
    void doReserveRollsBackRedisAndReturnsDuplicateMessageWhenDatabaseUniqueConstraintConflicts() {
        ReserveRecord record = validRecord();
        when(scenicAreaMapper.selectById(1L)).thenReturn(activeArea());
        when(timeSlotMapper.selectById(1L)).thenReturn(validSlot());
        when(reserveRecordMapper.selectCount(any(Wrapper.class))).thenReturn(0L);
        when(stringRedisTemplate.hasKey(anyString())).thenReturn(true);
        when(stringRedisTemplate.opsForValue()).thenReturn(valueOperations);
        when(valueOperations.decrement(anyString())).thenReturn(9L);
        when(reserveRecordMapper.insert(record)).thenThrow(new DuplicateKeyException("duplicate reservation"));

        String result = reserveRecordService.doReserve(record);

        String redisKey = "ticket_stock:1:" + record.getReserveDate() + ":1";
        verify(valueOperations).increment(redisKey);
        assertTrue(result.contains("重复预约") || result.contains("请勿重复预约"),
                "重复键冲突应返回明确的重复预约提示，实际返回：" + result);
    }

    private ReserveRecord validRecord() {
        ReserveRecord record = new ReserveRecord();
        record.setTouristId(100L);
        record.setAreaId(1L);
        record.setSlotId(1L);
        record.setReserveDate(LocalDate.now().plusDays(1).toString());
        return record;
    }

    private ScenicArea activeArea() {
        return activeArea(1L);
    }

    private ScenicArea activeArea(Long id) {
        ScenicArea area = new ScenicArea();
        area.setId(id);
        area.setStatus(1);
        return area;
    }

    private TimeSlot validSlot() {
        TimeSlot slot = new TimeSlot();
        slot.setId(1L);
        slot.setMaxPeople(10);
        return slot;
    }
}
