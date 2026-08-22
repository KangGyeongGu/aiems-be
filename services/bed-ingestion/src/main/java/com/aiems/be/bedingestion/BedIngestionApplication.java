package com.aiems.be.bedingestion;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

@SpringBootApplication(scanBasePackages = "com.aiems.be")
@ConfigurationPropertiesScan(basePackages = "com.aiems.be")
public class BedIngestionApplication {
    public static void main(String[] args) {
        SpringApplication.run(BedIngestionApplication.class, args);
    }
}
