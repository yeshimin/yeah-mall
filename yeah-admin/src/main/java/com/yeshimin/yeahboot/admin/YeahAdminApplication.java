package com.yeshimin.yeahboot.admin;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = {"com.yeshimin.yeahboot"})
@MapperScan("com.yeshimin.yeahboot.**.mapper")
public class YeahAdminApplication {

    public static void main(String[] args) {
        SpringApplication.run(YeahAdminApplication.class, args);
    }
}