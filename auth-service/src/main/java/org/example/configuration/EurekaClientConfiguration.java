package org.example.configuration;

import com.netflix.discovery.AbstractDiscoveryClientOptionalArgs;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class EurekaClientConfiguration {
    @Bean
    public AbstractDiscoveryClientOptionalArgs<?> optionalArgs() {
        return new AbstractDiscoveryClientOptionalArgs<>() {};
    }
}
