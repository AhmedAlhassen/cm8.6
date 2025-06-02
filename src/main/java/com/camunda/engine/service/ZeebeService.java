package com.camunda.engine.service;


import io.camunda.zeebe.client.ZeebeClient;
import io.camunda.zeebe.client.api.response.ProcessInstanceEvent;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Map;

@AllArgsConstructor
@Service
public class ZeebeService {

    private final ZeebeClient zeebeClient;


    public String startProcessInstance(String processKey) {
        ProcessInstanceEvent event = zeebeClient.newCreateInstanceCommand()
                .bpmnProcessId(processKey)
                .latestVersion()
                .send()
                .join();

        return "Started process with instance ID: " + event.getProcessInstanceKey();
    }

    // New method to start a process instance with variables
    public String startProcessInstanceWithVariables(String processKey, Map<String, Object> variables) {
        ProcessInstanceEvent event = zeebeClient.newCreateInstanceCommand()
                .bpmnProcessId(processKey)
                .latestVersion()
                .variables(variables) // Attach variables here
                .send()
                .join();

        return "Started process with instance ID: " + event.getProcessInstanceKey();
    }
}
