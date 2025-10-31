package org.example;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.server.EnableEurekaServer;

import java.time.LocalDateTime;

@EnableEurekaServer
@SpringBootApplication
public class ServiceRegistryApplication {

    private static final Logger logger = LoggerFactory.getLogger(ServiceRegistryApplication.class);

    // Inject the server port value from the configuration file
    @Value("${server.port}")
    private static int serverPort;

    public static void main(String[] args) {
        SpringApplication.run(ServiceRegistryApplication.class, args);

        logger.info("Server Port: {}", serverPort);
        logger.info("Eureka Service Registry Successfully Running {} ", LocalDateTime.now());
    }

}
