package com.akbank.chatbot.rulemanager.listener;

import java.util.Date;
import java.util.List;

import org.jeasy.rules.api.Facts;
import org.jeasy.rules.api.Rule;
import org.jeasy.rules.api.RuleListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.akbank.chatbot.rulemanager.RuleConstants;
import com.akbank.chatbot.rulemanager.log.RuleExecutionLog;
import com.akbank.chatbot.rulemanager.mapper.model.FactInfo;

public class AppRuleListener implements RuleListener {
	private static Logger logger = LoggerFactory.getLogger(AppRuleListener.class);

	public boolean beforeEvaluate(Rule rule, Facts facts) {
		boolean result = true;
		Object obj = facts.get(RuleConstants.FACTS.FACT);
		if (obj != null) {
			FactInfo factObj = ((FactInfo) obj);
			if (factObj.getResult() == null && rule.getPriority() == 0) {
				result = true;
			} else {
				result = factObj.getResult() == null ? false : factObj.getResult();
			}
		} else {
			result = true;
		}

		if (result) {
			facts.put(rule.getName() + "-" + rule.getPriority(), new Date());
		}

		return result;

	}

	public void afterEvaluate(Rule rule, Facts facts, boolean evaluationResult) {
		Object obj = facts.get(RuleConstants.FACTS.FACT);
		if (obj != null) {
			FactInfo factObj = ((FactInfo) obj);
			factObj.setResult(evaluationResult);
		}

		try {
			Object startDateObj = facts.get(rule.getName() + "-" + rule.getPriority());
			Object logObj = facts.get(RuleConstants.FACTS.LOG);
			if (logObj != null) {
				long cost = 0;
				Date now = new Date();
				if (startDateObj != null) {
					Date startDate = (Date) startDateObj;
					cost = now.getTime() - startDate.getTime();
				}

				List<RuleExecutionLog> log = (List<RuleExecutionLog>) logObj;

				log.add(new RuleExecutionLog(rule.getName(), rule.getPriority(), rule.getDescription(), cost,
						evaluationResult, now));
			}
		} catch (Exception e) {
			logger.error("afterEvaluate-Error", e);
		}

	}

	public void onEvaluationError(Rule rule, Facts facts, Exception exception) {
		System.out.println("");
	}

	@Override
	public void onSuccess(Rule rule, Facts facts) {
		System.out.println("");
	}

	@Override
	public void onFailure(Rule rule, Facts facts, Exception exception) {
		System.out.println("");
	}
}
