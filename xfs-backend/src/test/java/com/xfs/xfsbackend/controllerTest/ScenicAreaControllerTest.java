package com.xfs.xfsbackend.controllerTest;

import com.xfs.xfsbackend.common.Result;
import com.xfs.xfsbackend.controller.ScenicAreaController;
import com.xfs.xfsbackend.entity.ScenicArea;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;


@SpringBootTest
public class ScenicAreaControllerTest {
    @Autowired
    private ScenicAreaController scenicAreaController;
    @Test
    public void getHotListTest(){
        // 调用接口（前提是 controller 已注入，且 service 能返回数据）
        Result<List<ScenicArea>> hotListResult = scenicAreaController.getHotList();



        // 2. 获取实际列表
        List<ScenicArea> list = hotListResult.getData();
        assertNotNull(list);
        assertEquals(3, list.size());  // 预期只取前3条

        // 3. 循环遍历每一项
        for (ScenicArea area : list) {
            // 这里可以对每个 area 做你想做的校验
            assertNotNull(area);
            // 例如：view_count 应该大于 0
            assertTrue(area.getViewCount() > 0, "view_count 应大于 0");
        }

        // 如果想验证顺序（降序），用相邻比较
        for (int i = 0; i < list.size() - 1; i++) {
            assertTrue(list.get(i).getViewCount() >= list.get(i + 1).getViewCount(),
                    "列表未按 view_count 降序排列");
        }
    }
}
