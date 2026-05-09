package com.xfs.xfsbackend.controllerTest;

import com.xfs.xfsbackend.config.AdminRequired;
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
 * 管理员权限拦截器集成测试
 * 验证 @AdminRequired 注解的接口保护行为
 */
@SpringBootTest
@AutoConfigureMockMvc
class AdminRoleInterceptorTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private JwtUtils jwtUtils;

    private String adminToken() {
        return jwtUtils.generateToken(1L, "admin", "admin");
    }

    private String touristToken() {
        return jwtUtils.generateToken(888L, "Tourist_1234", "tourist");
    }

    @Test
    void testNoTokenReturns401() throws Exception {
        mockMvc.perform(get("/api/stats/cards"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void testAdminCanAccessStatsCards() throws Exception {
        mockMvc.perform(get("/api/stats/cards")
                        .header("Authorization", "Bearer " + adminToken()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200));
    }

    @Test
    void testTouristCannotAccessStatsCards() throws Exception {
        mockMvc.perform(get("/api/stats/cards")
                        .header("Authorization", "Bearer " + touristToken()))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.code").value(403));
    }

    @Test
    void testTouristCannotVerifyTicket() throws Exception {
        mockMvc.perform(post("/api/reserve/verify/V-TESTCODE")
                        .header("Authorization", "Bearer " + touristToken())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden());
    }

    @Test
    void testTouristCannotDeleteArea() throws Exception {
        mockMvc.perform(delete("/api/area/1")
                        .header("Authorization", "Bearer " + touristToken()))
                .andExpect(status().isForbidden());
    }

    @Test
    void testTouristCanAccessReserveMyList() throws Exception {
        // /api/reserve/myList 不需要 @AdminRequired，游客应可访问
        mockMvc.perform(get("/api/reserve/myList")
                        .header("Authorization", "Bearer " + touristToken()))
                .andExpect(status().isOk());
    }

    @Test
    void testPublicEndpointsWithoutToken() throws Exception {
        // 景区列表是公开接口，不需要 token
        mockMvc.perform(get("/api/area/list"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200));
    }

    @Test
    void testTokenWithoutRoleTreatedAsTourist() throws Exception {
        // 使用旧版无 role 的 token，应被视为游客
        String noRoleToken = jwtUtils.generateToken(1L, "admin");
        mockMvc.perform(get("/api/stats/cards")
                        .header("Authorization", "Bearer " + noRoleToken))
                .andExpect(status().isForbidden());
    }
}
