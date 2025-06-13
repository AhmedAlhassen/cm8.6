package com.camunda.engine.application.dto;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@ToString
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class NewApplicationDto {
   private String serviceRequestId;
   private String emirateId;
   private String applicationId;
   private String startBeforeElement;

}
