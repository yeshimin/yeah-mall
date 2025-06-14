package com.yeshimin.yeahboot;

import com.yeshimin.yeahboot.admin.YeahAdminApplication;
import com.yeshimin.yeahboot.generator.CodeGenerator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestConstructor;

@Slf4j
@SpringBootTest(classes = YeahAdminApplication.class)
@TestConstructor(autowireMode = TestConstructor.AutowireMode.ALL)
@RequiredArgsConstructor
public class CodeGeneratorTests {

    private final CodeGenerator codeGenerator;

    @Test
    public void testGenerate() {
        codeGenerator.generate();
    }
}
