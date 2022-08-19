package com.akbank.chatbot.rulemanager;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.scheduling.annotation.EnableScheduling;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;

@SpringBootApplication
@EnableScheduling
@EnableCaching
@OpenAPIDefinition(info = @Info(title = "RuleManager API", version = "1.0", description = "This Api project for proactive help project"))
public class RulemanagerApplication {

	public static void main(String[] args) {
		SpringApplication.run(RulemanagerApplication.class, args);
	}

}
