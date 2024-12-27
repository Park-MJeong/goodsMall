package com.goodsmall;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableAsync
@EnableScheduling
@SpringBootApplication
public class GoodsMallApplication {

    public static void main(String[] args) {
        SpringApplication.run(GoodsMallApplication.class, args);
    }

}
