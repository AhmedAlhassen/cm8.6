package com.camunda.engine.api;

import com.camunda.engine.service.ProcessInstanceService;
import io.camunda.operate.exception.OperateException;
import io.camunda.operate.model.ProcessInstance;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@AllArgsConstructor
@RestController
@RequestMapping("/api/processes")
public class ProcessController {
    private final ProcessInstanceService processInstanceService;

    @GetMapping("/parent/{parentKey}/latest-active")
    public ResponseEntity<ProcessInstance> getLatestActiveChild(@PathVariable Long parentKey) {
        try {
            ProcessInstance latestActive = processInstanceService.findLatestActiveProcessByParentProcessId(parentKey);
            if (latestActive != null) {
                return ResponseEntity.ok(latestActive);
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (OperateException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/parent/{parentKey}/latest-active/{bpmnProcessId}")
    public ResponseEntity<ProcessInstance> getLatestActiveChildByProcessId(
            @PathVariable Long parentKey,
            @PathVariable String bpmnProcessId) {
        try {
            ProcessInstance latestActive = processInstanceService.findLatestActiveProcessByParentProcessId(parentKey, bpmnProcessId);
            return latestActive != null ? ResponseEntity.ok(latestActive) : ResponseEntity.notFound().build();
        } catch (OperateException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}
