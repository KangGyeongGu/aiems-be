package com.aiems.be;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.data.redis.RedisAutoConfiguration;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

@SpringBootApplication(exclude = RedisAutoConfiguration.class)
@ConfigurationPropertiesScan
public class AiemsBeApplication {

    public static void main(String[] args) {
        SpringApplication.run(AiemsBeApplication.class, args);
    }

}
