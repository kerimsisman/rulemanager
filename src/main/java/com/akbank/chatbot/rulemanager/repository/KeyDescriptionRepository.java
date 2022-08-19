package com.akbank.chatbot.rulemanager.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.akbank.chatbot.rulemanager.entity.KeyDescription;

public interface KeyDescriptionRepository extends JpaRepository<KeyDescription, Long> {
	KeyDescription findByKey(String key);
}
