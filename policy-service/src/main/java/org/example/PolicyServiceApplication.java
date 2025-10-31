package org.example;

import java.time.LocalDateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication
@EnableDiscoveryClient
public class PolicyServiceApplication {

    private static final Logger logger = LoggerFactory.getLogger(PolicyServiceApplication.class);

    public static void main(String[] args) {
        SpringApplication.run(PolicyServiceApplication.class,args);

        logger.info("Policy Service Application Successfully Running {} ", LocalDateTime.now());
    }
}

