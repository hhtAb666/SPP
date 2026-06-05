/**
 * Spring Boot 应用程序启动类
 */
package com.campus.sport;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.mybatis.spring.annotation.MapperScan;

@SpringBootApplication
@MapperScan("com.campus.sport.mapper")
public class CampusSportVenueApplication {

    public static void main(String[] args) {
        SpringApplication.run(CampusSportVenueApplication.class, args);
    }

}
