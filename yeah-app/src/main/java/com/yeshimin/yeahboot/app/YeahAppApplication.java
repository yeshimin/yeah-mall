package com.yeshimin.yeahboot.app;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = {"com.yeshimin.yeahboot"})
@MapperScan("com.yeshimin.yeahboot.**.mapper")
public class YeahAppApplication {

    public static void main(String[] args) {
        SpringApplication.run(YeahAppApplication.class, args);
    }
}