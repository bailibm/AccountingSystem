package com.example.jizhang;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("com.example.jizhang.mapper")
public class JiZhangApplication {

    public static void main(String[] args) {
        SpringApplication.run(JiZhangApplication.class, args);
    }

}
