package com.akbank.chatbot.rulemanager.offer.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.akbank.chatbot.rulemanager.offer.entity.ExecutionLog;
import com.akbank.chatbot.rulemanager.offer.repository.ExecutionLogRepository;
import com.akbank.chatbot.rulemanager.offer.service.ExecutionLogService;

@Service
public class ExecutionLogServiceImpl implements ExecutionLogService {

	@Autowired
	ExecutionLogRepository executionLogRepo;

	@Override
	public void save(ExecutionLog executionLog) {
		new Thread(() -> {
			executionLogRepo.save(executionLog);
		}).run();
	}

}
