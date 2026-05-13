package com.xfs.xfsbackend.controller;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.xfs.xfsbackend.common.Result;
import com.xfs.xfsbackend.entity.ReserveRecord;
import com.xfs.xfsbackend.service.ReserveRecordService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.test.util.ReflectionTestUtils;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ReserveQrCodeAccessTest {

    private ReserveController reserveController;

    @Mock
    private ReserveRecordService reserveRecordService;

    @BeforeEach
    void setUp() {
        reserveController = new ReserveController();
        ReflectionTestUtils.setField(reserveController, "reserveRecordService", reserveRecordService);
    }

    @Test
    void touristCanGetOwnQrCode() {
        when(reserveRecordService.getOne(any(Wrapper.class), eq(false))).thenReturn(recordOwnedBy(100L));
        MockHttpServletRequest request = requestWithUser("tourist", 100L);

        Result<String> result = reserveController.getQrCode("XFS-OWN", request);

        assertEquals(200, result.getCode());
        assertNotNull(result.getData());
        assertTrue(result.getData().startsWith("data:image/png;base64,"));
    }

    @Test
    void touristCannotGetOtherUsersQrCode() {
        when(reserveRecordService.getOne(any(Wrapper.class), eq(false))).thenReturn(recordOwnedBy(100L));
        MockHttpServletRequest request = requestWithUser("tourist", 200L);

        Result<String> result = reserveController.getQrCode("XFS-OTHER", request);

        assertEquals(500, result.getCode());
        assertEquals("无权查看该预约二维码", result.getMsg());
    }

    @Test
    void adminCanGetAnyQrCode() {
        when(reserveRecordService.getOne(any(Wrapper.class), eq(false))).thenReturn(recordOwnedBy(100L));
        MockHttpServletRequest request = requestWithUser("admin", 1L);

        Result<String> result = reserveController.getQrCode("XFS-ADMIN", request);

        assertEquals(200, result.getCode());
        assertNotNull(result.getData());
    }

    private ReserveRecord recordOwnedBy(Long touristId) {
        ReserveRecord record = new ReserveRecord();
        record.setOrderNo("XFS-TEST");
        record.setTouristId(touristId);
        return record;
    }

    private MockHttpServletRequest requestWithUser(String role, Long userId) {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setAttribute("role", role);
        request.setAttribute("adminId", userId);
        return request;
    }
}
