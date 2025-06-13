package com.camunda.engine.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
@FieldDefaults(level = AccessLevel.PRIVATE)
@ToString
public class ProcessInstanceDTO {
    String bpmnProcessId;
    Integer version;
    Map<String, Object> variables;
    String startBeforeElement;
    Boolean keepOldProcessVariables = false;
    Boolean keepOldProcessRunning = true;
    String oldProcessInstanceId;
}
