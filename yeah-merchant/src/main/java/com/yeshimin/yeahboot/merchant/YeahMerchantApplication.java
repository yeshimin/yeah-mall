package com.yeshimin.yeahboot.merchant;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@SpringBootApplication(scanBasePackages = {"com.yeshimin.yeahboot"})
public class YeahMerchantApplication {

    public static void main(String[] args) {
        SpringApplication.run(YeahMerchantApplication.class, args);
    }
}
