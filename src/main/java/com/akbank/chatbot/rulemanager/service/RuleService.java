package com.akbank.chatbot.rulemanager.service;

import org.jeasy.rules.api.Rules;

public interface RuleService {
	void loadRules();

	Rules getRules(String code);
}
