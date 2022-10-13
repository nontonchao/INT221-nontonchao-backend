package com.example.oasip_back_nontonchao;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;


@SpringBootApplication
@EnableConfigurationProperties({
        FileStorageProperties.class
})
public class OasipBackNontonchaoApplication {
    public static void main(String[] args) {
        SpringApplication.run(OasipBackNontonchaoApplication.class, args);
    }
}
