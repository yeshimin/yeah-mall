package com.yeshimin.yeahboot;

import com.alibaba.fastjson2.JSON;
import com.yeshimin.yeahboot.admin.YeahAdminApplication;
import com.yeshimin.yeahboot.upms.domain.vo.JwtPayloadVo;
import com.yeshimin.yeahboot.upms.service.JwtService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestConstructor;

@Slf4j
@SpringBootTest(classes = YeahAdminApplication.class)
@TestConstructor(autowireMode = TestConstructor.AutowireMode.ALL)
@RequiredArgsConstructor
public class JwtServiceTests {

    private final JwtService jwtService;

    @Test
    public void testDecode() {
        String token = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJhdWQiOiIxIiwiaWF0IjoxNzUwMjIzMDEwLCJzdWIiOiJhZG1pbiIsImV4cCI6MTc1MDI1OTAxMCwidGVybSI6IndlYiJ9.xg6TKmji0ir1dfUc1fz_0xDPIYocAyVpC1rs3xQeTWw";
        JwtPayloadVo result = jwtService.decodePayload(token);
        log.info("result: {}", JSON.toJSONString(result));
    }
}
