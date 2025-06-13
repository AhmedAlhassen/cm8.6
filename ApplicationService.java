package com.camunda.engine.application.service;


import com.camunda.engine.application.IllegalServiceRequestType;
import com.camunda.engine.application.dto.NewApplicationDto;
import com.camunda.engine.config.ServiceRequestConfig;
import com.camunda.engine.dto.ProcessInstanceDTO;
import com.camunda.engine.service.CamundaProcessesService;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Objects;
import java.util.Optional;

@Service
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ApplicationService {

    @Qualifier("serviceRequestMap")
     Map<String, ServiceRequestConfig.ServiceRequest> serviceRequestMap;
     ObjectMapper objectMapper;
     CamundaProcessesService camundaProcessesService;

     public long startNewApplication(NewApplicationDto newApplicationDto) throws IllegalServiceRequestType {
         String bpmnProcessId = Optional.ofNullable(serviceRequestMap.get(newApplicationDto.getServiceRequestId()))
                 .map(ServiceRequestConfig.ServiceRequest::getBpmnProcessId)
                 .orElseThrow(() -> new IllegalServiceRequestType(newApplicationDto.getServiceRequestId()));

         Map<String,Object> payload = objectMapper.convertValue(newApplicationDto, Map.class);
         ProcessInstanceDTO processInstanceDTO = new ProcessInstanceDTO();
         processInstanceDTO.setVariables(payload);
         processInstanceDTO.setBpmnProcessId(bpmnProcessId);
         processInstanceDTO.setStartBeforeElement(newApplicationDto.getStartBeforeElement());

        var process = this.camundaProcessesService.startNewProcess(processInstanceDTO);

         return process.getProcessInstanceKey();
     }
}
