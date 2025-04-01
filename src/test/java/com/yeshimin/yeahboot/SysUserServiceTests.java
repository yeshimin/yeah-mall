package com.yeshimin.yeahboot;

import com.yeshimin.yeahboot.domain.dto.SysUserCreateDto;
import com.yeshimin.yeahboot.domain.entity.SysUserEntity;
import com.yeshimin.yeahboot.service.SysUserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class SysUserServiceTests {

    @Autowired
    private SysUserService sysUserService;

    @Test
    public void testCreateUser() {
        SysUserCreateDto dto = new SysUserCreateDto();
        dto.setUsername("yeshimin");
        dto.setPassword("123456");
        SysUserEntity result = sysUserService.create(dto);
        assert result.getId() != null;
    }
}
