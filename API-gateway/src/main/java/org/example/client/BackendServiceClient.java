package org.example.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "customer-service") // Use the service name registered in Eureka
public interface BackendServiceClient {

    @GetMapping("/api/resource") // Define the endpoint you want to call
    String getResource(@RequestParam("param") String param);
}

