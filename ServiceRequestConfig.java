package com.camunda.engine.config;


import jakarta.annotation.PostConstruct;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.*;
import org.springframework.context.event.EventListener;


import java.util.function.Function;
import java.util.stream.Collectors;

@Configuration
public class ServiceRequestConfig {

    private static final Logger log = LoggerFactory.getLogger(ServiceRequestConfig.class);


    @Bean
    public Map<String, ServiceRequest> serviceRequestMap(List<ServiceRequest> serviceRequestList) {
        return Optional.ofNullable(serviceRequestList)
                .orElse(Collections.emptyList())
                .stream()
                .filter(entry -> entry.getBpmnProcessId() != null)
                .collect(Collectors.toMap(entry -> entry.id, Function.identity()));
    }

    @Bean
    @ConfigurationProperties(prefix = "service-request")
    public List<ServiceRequest> serviceRequest() {
        return new ArrayList<>();
    }


    @PostConstruct
    public void logServiceRequestMappings() {
        // Access the applicationContext to get the bean after it's created
        log.info("=== SERVICE REQUEST MAPPINGS LOADED ===");
    }

    // Better approach: Use ApplicationListener
    @EventListener(ApplicationReadyEvent.class)
    public void onApplicationReady(ApplicationReadyEvent event) {
        Map serviceRequestMap =
                event.getApplicationContext().getBean("serviceRequestMap", Map.class);

        logServiceRequestMap(serviceRequestMap);
    }

    private void logServiceRequestMap(Map<String, ServiceRequest> serviceRequestMap) {
        log.info("=== SERVICE REQUEST MAPPINGS LOADED ===");
        log.info("Total service request mappings: {}", serviceRequestMap.size());

        // Log each mapping
        serviceRequestMap.forEach((id, serviceRequest) -> {
            log.info("ID: {} | Code: {} | Name: {} | BPMN Process: {}",
                    id,
                    serviceRequest.getCode(),
                    serviceRequest.getName(),
                    serviceRequest.getBpmnProcessId());
        });

        // Log by categories
        logServicesByCategory(serviceRequestMap);
        log.info("=== END SERVICE REQUEST MAPPINGS ===");
    }

    private void logServicesByCategory(Map<String, ServiceRequest> serviceRequestMap) {
        // Group by service categories
        Map<String, List<ServiceRequest>> categorizedServices = serviceRequestMap.values()
                .stream()
                .collect(Collectors.groupingBy(this::categorizeService));

        categorizedServices.forEach((category, services) -> {
            log.info("Category [{}]: {} services", category, services.size());
            services.forEach(service ->
                    log.debug("  - {} ({}): {}", service.getName(), service.getId(), service.getBpmnProcessId())
            );
        });
    }
    private String categorizeService(ServiceRequest service) {
        String id = service.getId();
        if (id.startsWith("250")) return "Housing Services";
        if (id.startsWith("710")) return "Exchange/Exemption Services";
        if (id.startsWith("1650")) return "Land Services";
        if (id.startsWith("70")) return "Post-Loan Services";
        if (id.startsWith("950")) return "Special Services";
        return "Other Services";
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ServiceRequest {
        private String id;
        private String code;
        private String name;
        private String bpmnProcessId;
    }
}
