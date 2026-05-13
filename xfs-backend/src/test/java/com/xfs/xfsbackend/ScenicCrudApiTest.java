package com.xfs.xfsbackend;

import com.xfs.xfsbackend.entity.ScenicArea;
import com.xfs.xfsbackend.entity.ScenicSpot;
import com.xfs.xfsbackend.utils.JwtUtils;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import com.fasterxml.jackson.databind.ObjectMapper;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class ScenicCrudApiTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private JwtUtils jwtUtils;

    @Test
    void testScenicAreaAndSpotLifecycle() throws Exception {
        // 1. 测试新增大景区
        ScenicArea area = new ScenicArea();
        area.setName("测试景区" + System.currentTimeMillis());
        area.setLevel("AAAA");
        area.setAddress("测试地址");

        String areaJson = objectMapper.writeValueAsString(area);
        mockMvc.perform(post("/api/area/save")
                .header("Authorization", "Bearer " + adminToken())
                .contentType(MediaType.APPLICATION_JSON)
                .content(areaJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200));

        // 2. 测试获取子景点列表 (针对已存在的ID=2)
        mockMvc.perform(get("/api/area/2/spots"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data").isArray());

        // 3. 测试新增子景点
        ScenicSpot spot = new ScenicSpot();
        spot.setAreaId(2L);
        spot.setName("测试子景点" + System.currentTimeMillis());
        spot.setPrice(10.0);

        String spotJson = objectMapper.writeValueAsString(spot);
        mockMvc.perform(post("/api/area/spot/save")
                .header("Authorization", "Bearer " + adminToken())
                .contentType(MediaType.APPLICATION_JSON)
                .content(spotJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200));
    }

    private String adminToken() {
        return jwtUtils.generateToken(1L, "admin", "admin");
    }
}
