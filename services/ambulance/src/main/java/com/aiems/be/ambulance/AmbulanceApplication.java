package com.aiems.be.ambulance;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

@SpringBootApplication(scanBasePackages = "com.aiems.be")
@ConfigurationPropertiesScan(basePackages = "com.aiems.be")
public class AmbulanceApplication {
    public static void main(String[] args) {
        SpringApplication.run(AmbulanceApplication.class, args);
    }
}
