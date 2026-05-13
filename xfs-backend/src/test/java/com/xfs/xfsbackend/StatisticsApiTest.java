package com.xfs.xfsbackend;

import com.xfs.xfsbackend.utils.JwtUtils;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class StatisticsApiTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private JwtUtils jwtUtils;

    @Test
    void testGetStatCards() throws Exception {
        mockMvc.perform(get("/api/stats/cards")
                        .header("Authorization", "Bearer " + adminToken()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.todayReserve").exists())
                .andExpect(jsonPath("$.data.verifyRate").exists());
    }

    @Test
    void testGetAreaHeat() throws Exception {
        mockMvc.perform(get("/api/stats/areaHeat")
                        .header("Authorization", "Bearer " + adminToken()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data").isArray());
    }

    @Test
    void testGetTrend() throws Exception {
        mockMvc.perform(get("/api/stats/trend")
                        .header("Authorization", "Bearer " + adminToken()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.dates").isArray())
                .andExpect(jsonPath("$.data.values").isArray());
    }

    private String adminToken() {
        return jwtUtils.generateToken(1L, "admin", "admin");
    }
}
