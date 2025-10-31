package org.example.controller;

import org.example.model.ServiceConfig;
import org.example.service.ConfigService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin
@RestController
@RequestMapping("v1/gateway")
public class ApiGatewayController {

    private final ConfigService configService;

    @Autowired
    public ApiGatewayController(ConfigService configService) {
        this.configService = configService;
    }

    @GetMapping
    public ResponseEntity<String> initGateway(){
            return ResponseEntity.ok().body("API Gateway Initialized!");
    }

    @GetMapping("/serviceList")
    public ResponseEntity<List<ServiceConfig>> fetchServiceLists(){
        List<ServiceConfig> serviceConfigList = configService.fetchServiceConfigs();
        return ResponseEntity.ok(serviceConfigList);
    }

    @PostMapping("/serviceRegister")
    public ResponseEntity<ServiceConfig> registerServices(@RequestBody ServiceConfig serviceConfig){
        try {
            ServiceConfig configServices = configService.registerMicroservices(serviceConfig);
            return ResponseEntity.ok(configServices);
        } catch (Exception e) {
            // Log error and return a meaningful response
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }
}
