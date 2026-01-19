package com.yeshimin.yeahboot.app;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = {"com.yeshimin.yeahboot"})
//@MapperScan("com.yeshimin.yeahboot.**.mapper") // 单体模式注释掉这个；如果各admin、app等模块独立打包，则需要启用
public class YeahAppApplication {

    public static void main(String[] args) {
        SpringApplication.run(YeahAppApplication.class, args);
    }
}