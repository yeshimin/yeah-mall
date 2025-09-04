package com.yeshimin.yeahboot;

import com.yeshimin.yeahboot.admin.YeahAdminApplication;
import com.yeshimin.yeahboot.common.service.IdService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestConstructor;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Slf4j
@SpringBootTest(classes = YeahAdminApplication.class)
@TestConstructor(autowireMode = TestConstructor.AutowireMode.ALL)
@RequiredArgsConstructor
public class IdServiceTests {

    private final IdService idService;

    @Test
    public void testAlphabetShuffle() {
        String alphabet = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
        List<Character> chars = new ArrayList<>();
        for (char c : alphabet.toCharArray()) {
            chars.add(c);
        }

        Collections.shuffle(chars); // 核心洗牌方法

        StringBuilder shuffled = new StringBuilder();
        for (char c : chars) {
            shuffled.append(c);
        }

        System.out.println("Shuffled Alphabet: " + shuffled);
    }

    @Test
    public void testNextEncodedId() {
        for (int i = 0; i < 10; i++) {
            String id = idService.nextEncodedId();
            log.info("id: {}", id);
        }
    }
}
