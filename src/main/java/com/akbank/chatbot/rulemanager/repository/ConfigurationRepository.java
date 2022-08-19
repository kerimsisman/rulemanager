package com.akbank.chatbot.rulemanager.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.akbank.chatbot.rulemanager.entity.Configuration;

public interface ConfigurationRepository extends JpaRepository<Configuration, Long> {
	Configuration findByKey(String key);
}
