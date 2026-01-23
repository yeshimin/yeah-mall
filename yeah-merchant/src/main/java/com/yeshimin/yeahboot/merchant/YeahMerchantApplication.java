package com.yeshimin.yeahboot.merchant;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@SpringBootApplication(scanBasePackages = {"com.yeshimin.yeahboot"})
//@MapperScan("com.yeshimin.yeahboot.**.mapper") // 单体模式注释掉这个；如果各admin、app等模块独立打包，则需要启用
public class YeahMerchantApplication {

    public static void main(String[] args) {
        SpringApplication.run(YeahMerchantApplication.class, args);
    }
}