package org.example.client;

import org.example.model.CustomerServiceModel;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

///A separate class within (claims-service) with the sole responsibility to handle and consume another backend microservices leveraged by Feign Client
@FeignClient(name = "customer-service")
public interface CustomerClient {

    @GetMapping("v1/customer/customers/{customerId}")
    CustomerServiceModel getCustomerById(@PathVariable("customerId") Long customerId);

    @GetMapping("v1/customer/customerList")
    List<CustomerServiceModel> getAllCustomers();

    @GetMapping("v1/customer/init")
    String getCustomerInit();

    @DeleteMapping("v1/customer/deleteCustomer")
    void deleteCustById(@RequestParam("id") Long id);
}
