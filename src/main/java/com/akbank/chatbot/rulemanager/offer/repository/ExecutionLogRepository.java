package com.akbank.chatbot.rulemanager.offer.repository;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.akbank.chatbot.rulemanager.offer.entity.ExecutionLog;

public interface ExecutionLogRepository  extends MongoRepository<ExecutionLog, String> {

}
