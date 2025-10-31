package org.example.service.implementation;

import org.example.model.ServiceConfig;
import org.example.repository.ConfigRepository;
import org.example.service.ConfigService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ConfigServiceImpl implements ConfigService {

    private static final Logger logger = LoggerFactory.getLogger(ConfigServiceImpl.class);
    private final ConfigRepository configRepository;

    @Autowired
    public ConfigServiceImpl(ConfigRepository configRepository) {
        this.configRepository = configRepository;
    }

    @Override
    public List<ServiceConfig> fetchServiceConfigs() {
        // Since the backend services are inserted and registered into DB
        // The goal is to dynamically retrieving from DB
        return configRepository.findAll();
    }

    @Override
    @Transactional
    public ServiceConfig registerMicroservices(ServiceConfig serviceConfig) {
        //Dynamically handling the service discovery by registering/inserting the backend services into DB
        return configRepository.save(serviceConfig);
    }
}
