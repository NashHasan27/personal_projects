package org.example.service;

import org.example.model.ServiceConfig;

import java.util.List;

public interface ConfigService {

    List<ServiceConfig> fetchServiceConfigs();
    ServiceConfig registerMicroservices(ServiceConfig serviceConfig);
}
