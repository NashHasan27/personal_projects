package org.example;

import java.time.LocalDateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableDiscoveryClient
@EnableFeignClients
public class ClaimServiceApplication {

        private static final Logger logger = LoggerFactory.getLogger(ClaimServiceApplication.class);

        public static void main(String[] args) {
            SpringApplication.run(ClaimServiceApplication.class,args);

            logger.info("Claim Service Application Successfully Running {} ", LocalDateTime.now());
        }
    }
