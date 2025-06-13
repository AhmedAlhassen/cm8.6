package com.camunda.engine.service;


import com.camunda.engine.dto.ProcessInstanceDTO;
import io.camunda.zeebe.client.ZeebeClient;
import io.camunda.zeebe.client.api.command.CreateProcessInstanceCommandStep1;
import io.camunda.zeebe.client.api.response.ProcessInstanceEvent;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class CamundaProcessesService {

    ZeebeClient zeebeClient;

    public ProcessInstanceEvent startNewProcess(ProcessInstanceDTO processInstanceDTO){

        CreateProcessInstanceCommandStep1.CreateProcessInstanceCommandStep2 bpmProcessInstanceCommand = zeebeClient
                .newCreateInstanceCommand()
                .bpmnProcessId(processInstanceDTO.getBpmnProcessId());

        CreateProcessInstanceCommandStep1.CreateProcessInstanceCommandStep3 createProcessInstanceCommandStep;

        if (processInstanceDTO.getVersion() != null) {
            createProcessInstanceCommandStep = bpmProcessInstanceCommand.version(processInstanceDTO.getVersion());
        } else {
            createProcessInstanceCommandStep = bpmProcessInstanceCommand.latestVersion();
        }
        if (processInstanceDTO.getVariables() != null) {
            createProcessInstanceCommandStep = createProcessInstanceCommandStep.variables(processInstanceDTO.getVariables());
        }
        if (processInstanceDTO.getStartBeforeElement() != null) {
            createProcessInstanceCommandStep = createProcessInstanceCommandStep.startBeforeElement(processInstanceDTO.getStartBeforeElement());
        }

        return createProcessInstanceCommandStep.send().join();
    }

    public ProcessInstanceEvent startNewProcess(String bpmProcessId, Map<String, Object> variables){

        return zeebeClient
                .newCreateInstanceCommand()
                .bpmnProcessId(bpmProcessId)
                .latestVersion()
                .variables(variables)
                .send()
                .join();
    }

    public ProcessInstanceEvent startNewProcess(String bpmProcessId){

        return zeebeClient
                .newCreateInstanceCommand()
                .bpmnProcessId(bpmProcessId)
                .latestVersion()
                .send()
                .join();
    }
}
