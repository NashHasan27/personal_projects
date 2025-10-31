package org.example.configuration;

import org.example.model.ServiceConfig;
import org.example.service.ConfigService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import io.github.resilience4j.circuitbreaker.CircuitBreakerRegistry;
import io.github.resilience4j.circuitbreaker.CircuitBreakerConfig;
import org.springframework.cloud.gateway.filter.factory.SpringCloudCircuitBreakerFilterFactory;

import java.time.LocalDateTime;
import java.util.List;

@Configuration
public class GatewayConfig {

    private static final Logger logger = LoggerFactory.getLogger(GatewayConfig.class);
    private final ConfigService configService;

    @Autowired
    public GatewayConfig(ConfigService configService) {
        this.configService = configService;
    }

    @Bean
    public RouteLocator routeConfig(RouteLocatorBuilder builder,CircuitBreakerRegistry circuitBreakerRegistry) {
        List<ServiceConfig> serviceConfigs = configService.fetchServiceConfigs();
        RouteLocatorBuilder.Builder routesBuilder = builder.routes();

        //Building Circuit Breaker
        CircuitBreakerConfig circuitBreakerConfig = CircuitBreakerConfig.custom()
                .failureRateThreshold(50)
                .minimumNumberOfCalls(5)
                .automaticTransitionFromOpenToHalfOpenEnabled(true)
                .waitDurationInOpenState(java.time.Duration.ofMillis(10000))
                .slidingWindowSize(10)
                .slidingWindowType(CircuitBreakerConfig.SlidingWindowType.COUNT_BASED)
                .permittedNumberOfCallsInHalfOpenState(3)
                .build();

        circuitBreakerRegistry.circuitBreaker("CircuitBreaker", circuitBreakerConfig);

        for (ServiceConfig configNative : serviceConfigs) {
            routesBuilder
                    .route(configNative.getServiceId(), r -> r.path(configNative.getServicePath())
                            .filters(f -> f.circuitBreaker(c -> c.setName("CircuitBreaker").setFallbackUri("forward:/fallback"))
                                    .addResponseHeader("X-Response-Time", LocalDateTime.now().toString()))
                            .uri("lb://" + configNative.getServiceName()));
        }
        return routesBuilder.build();
    }
}
