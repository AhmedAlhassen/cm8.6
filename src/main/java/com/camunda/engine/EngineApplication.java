package com.camunda.engine;

import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.core.env.Environment;

@SpringBootApplication
public class EngineApplication {

	@Autowired
	private Environment env;

	public static void main(String[] args) {
		SpringApplication.run(EngineApplication.class, args);
	}

	@PostConstruct
	public void printLoadedProfile() {
		System.out.println("camunda.client.operate.client.profile = " +
				env.getProperty("camunda.client.operate.client.profile"));
	}
}
