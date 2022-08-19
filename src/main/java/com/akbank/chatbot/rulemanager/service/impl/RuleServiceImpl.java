package com.akbank.chatbot.rulemanager.service.impl;

import java.util.HashMap;

import javax.annotation.PostConstruct;

import org.jeasy.rules.api.Rules;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.akbank.chatbot.rulemanager.RuleConstants;
import com.akbank.chatbot.rulemanager.entity.AppAction;
import com.akbank.chatbot.rulemanager.repository.AppActionRepository;
import com.akbank.chatbot.rulemanager.service.RuleService;
import com.akbank.chatbot.rulemanager.type.Status;
import com.akbank.chatbot.rulemanager.util.RuleUtil;

@Service
public class RuleServiceImpl implements RuleService {
	private static Logger logger = LoggerFactory.getLogger(RuleServiceImpl.class);
	@Autowired
	RuleUtil ruleUtil;

	@Autowired
	AppActionRepository appActionRepo;

	HashMap<String, Rules> loadedRules = new HashMap<>();

	// TODO this method must be called by redis pub-sub for multi instance. If a
	// rule changed it must be refreshed on all instances
	@PostConstruct
	public void loadRules() {
		logger.info("loadRules started!");
		HashMap<String, Rules> tmpRules = new HashMap<>();
		for (AppAction appAction : appActionRepo.findAll()) {
			try {
				if (appAction.getStatus() == Status.ACTIVE) {
					logger.info("loadRules actionId:{} create-started", appAction.getId());
					Rules rulesObject = ruleUtil.createExpresion(appAction, RuleConstants.FACTS.SET_RESULT);
					tmpRules.put(appAction.getCode(), rulesObject);
					logger.info("loadRules actionId:{} create completed", appAction.getId());
				} else {
					logger.info("loadRules actionId:{} is not ACTIVE", appAction.getId());
				}
			} catch (Exception e) {
				logger.error("loadRules actionId:{} create-error", appAction.getId(), e);
			}
		}
		loadedRules = tmpRules;
		logger.info("loadRules completed!");
	}

	public Rules getRules(String code) {
		return loadedRules.get(code);
	}
}
