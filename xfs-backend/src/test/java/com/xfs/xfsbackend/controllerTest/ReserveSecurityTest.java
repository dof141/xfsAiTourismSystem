package com.xfs.xfsbackend.controllerTest;

import com.xfs.xfsbackend.utils.JwtUtils;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * 预约接口安全性测试
 * 验证 touristId 硬编码回退已移除
 */
@SpringBootTest
@AutoConfigureMockMvc
class ReserveSecurityTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private JwtUtils jwtUtils;

    @Test
    void testAddReserveWithoutTokenReturns401() throws Exception {
        mockMvc.perform(post("/api/reserve/add")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"areaId\":1,\"slotId\":1,\"reserveDate\":\"2026-05-10\"}"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void testMyListWithoutTokenReturns401() throws Exception {
        mockMvc.perform(get("/api/reserve/myList"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void testMyListWithValidTokenReturns200() throws Exception {
        String token = jwtUtils.generateToken(888L, "Tourist_1234", "tourist");
        mockMvc.perform(get("/api/reserve/myList")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200));
    }

    @Test
    void testVerifyRequiresAdminRole() throws Exception {
        String touristToken = jwtUtils.generateToken(888L, "Tourist_1234", "tourist");
        mockMvc.perform(post("/api/reserve/verify/V-TEST123")
                        .header("Authorization", "Bearer " + touristToken)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden());
    }

    @Test
    void testInitStockRequiresAdminRole() throws Exception {
        String touristToken = jwtUtils.generateToken(888L, "Tourist_1234", "tourist");
        mockMvc.perform(get("/api/reserve/initStock")
                        .header("Authorization", "Bearer " + touristToken))
                .andExpect(status().isForbidden());
    }
}
