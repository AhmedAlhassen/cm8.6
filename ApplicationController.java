package com.camunda.engine.application.api;


import com.camunda.engine.application.IllegalServiceRequestType;
import com.camunda.engine.application.dto.NewApplicationDto;
import com.camunda.engine.application.service.ApplicationService;
import com.camunda.engine.dto.ProcessInstanceWrapper;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/camunda/application")
@AllArgsConstructor
public class ApplicationController {

    private final ApplicationService applicationService;

    @PostMapping(value = "/start-workflow", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    ResponseEntity<ProcessInstanceWrapper> startProcess(@RequestBody NewApplicationDto newApplicationDto) throws IllegalServiceRequestType {
        this.applicationService.startNewApplication(newApplicationDto);
     return new ResponseEntity<>(new ProcessInstanceWrapper("12789"), HttpStatus.OK);
    }
}
