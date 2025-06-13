package com.camunda.engine.api;

import com.camunda.engine.service.ZeebeService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@AllArgsConstructor
@RestController
@RequestMapping("api/zeebe")
public class ZeebeController {

    private final ZeebeService zeebeService;

    @PostMapping("/start")
    public ResponseEntity<String> startProcessInstance(@RequestParam String processKey){
        String response = zeebeService.startProcessInstance(processKey);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/start-with-variables")
    public ResponseEntity<String> startProcessInstanceWithVariables(@RequestParam String processKey,
                                                                    @RequestBody Map<String, Object> variables) {
        String response = zeebeService.startProcessInstanceWithVariables(processKey, variables);
        return ResponseEntity.ok(response);
    }
}
