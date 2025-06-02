package com.camunda.engine.service;


import io.camunda.operate.CamundaOperateClient;
import io.camunda.operate.exception.OperateException;
import io.camunda.operate.model.ProcessInstance;
import io.camunda.operate.model.ProcessInstanceState;
import io.camunda.operate.search.*;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;


@AllArgsConstructor
@Service
public class ProcessInstanceService {

    private final CamundaOperateClient camundaOperateClient;

    /**
     * Find all child processes for a given parent process ID
     */
    public List<ProcessInstance> findChildProcessesByParentProcessId(Long parentProcessKey) throws OperateException {
        ProcessInstanceFilter childFilter = ProcessInstanceFilter.builder()
                .parentKey(parentProcessKey)
                .build();

        SearchQuery childQuery = SearchQuery.<ProcessInstance>builder()
                .filter(childFilter)
                .size(100)
                .sort(new Sort("startDate", SortOrder.ASC))
                .build();

        return camundaOperateClient.searchProcessInstances(childQuery);
    }

    /**
     * Find child processes with specific state and BPMN process ID
     */
    public List<ProcessInstance> findChildProcessesByParentAndCriteria(
            Long parentProcessKey,
            String bpmnProcessId,
            ProcessInstanceState state) throws OperateException {


        ProcessInstanceFilterBuilder filterBuilder = ProcessInstanceFilter.builder()
                .parentKey(parentProcessKey);

        if (bpmnProcessId != null) {
            filterBuilder.bpmnProcessId(bpmnProcessId);
        }

        if (state != null) {
            filterBuilder.state(state);
        }

        ProcessInstanceFilter childFilter = filterBuilder.build();

        SearchQuery childQuery = SearchQuery.<ProcessInstance>builder()
                .filter(childFilter)
                .size(50)
                .sort(new Sort("startDate", SortOrder.DESC))
                .build();


        return camundaOperateClient.searchProcessInstances(childQuery);
    }

    /**
     * Find the latest active child process by parent process ID
     */
    public ProcessInstance findLatestActiveProcessByParentProcessId(Long parentProcessKey) throws OperateException {
        ProcessInstanceFilter childFilter = ProcessInstanceFilter.builder()
                .parentKey(parentProcessKey)
                .state(ProcessInstanceState.ACTIVE)
                .build();

        SearchQuery query = SearchQuery.<ProcessInstance>builder()
                .filter(childFilter)
                .size(1)
                .sort(new Sort("startDate", SortOrder.DESC))
                .build();

        List<ProcessInstance> results = camundaOperateClient.searchProcessInstances(query);
        return results.isEmpty() ? null : results.get(0);
    }

    /**
     * Find the latest active child process by parent process ID
     * @param parentProcessKey The parent process instance key
     * @param bpmnProcessId Optional: filter by specific BPMN process ID
     * @return The latest active child process instance, or null if none found
     */
    public ProcessInstance findLatestActiveProcessByParentProcessId(
            Long parentProcessKey,
            String bpmnProcessId) throws OperateException {

        ProcessInstanceFilterBuilder filterBuilder = ProcessInstanceFilter.builder()
                .parentKey(parentProcessKey)
                .state(ProcessInstanceState.ACTIVE);

        // Add optional BPMN process ID filter
        if (bpmnProcessId != null && !bpmnProcessId.trim().isEmpty()) {
            filterBuilder.bpmnProcessId(bpmnProcessId);
        }

        ProcessInstanceFilter childFilter = filterBuilder.build();

        SearchQuery query = SearchQuery.<ProcessInstance>builder()
                .filter(childFilter)
                .size(1)
                .sort(new Sort("startDate", SortOrder.DESC))
                .build();

        List<ProcessInstance> results = camundaOperateClient.searchProcessInstances(query);
        return results.isEmpty() ? null : results.get(0);
    }

    public ProcessInstance findProcessByInstanceKey(Long instanceKey) throws OperateException {
        return camundaOperateClient.getProcessInstance(instanceKey);
    }
}
