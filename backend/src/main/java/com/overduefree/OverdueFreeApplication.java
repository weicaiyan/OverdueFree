package com.overduefree;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("com.overduefree.module")
public class OverdueFreeApplication {

    public static void main(String[] args) {
        SpringApplication.run(OverdueFreeApplication.class, args);
    }
}
