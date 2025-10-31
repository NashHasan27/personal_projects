package org.example.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient("customer-service")
public interface CustomerClient {

    @GetMapping("/v1/customer/customer-email/{id}")
    String getCustomerEmail(@PathVariable Long id);
}
