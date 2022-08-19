package com.akbank.chatbot.rulemanager.controller;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.ThreadContext;
import org.jeasy.rules.api.Facts;
import org.jeasy.rules.api.Rules;
import org.jeasy.rules.api.RulesEngineParameters;
import org.jeasy.rules.core.DefaultRulesEngine;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.akbank.chatbot.rulemanager.RuleConstants;
import com.akbank.chatbot.rulemanager.listener.AppRuleListener;
import com.akbank.chatbot.rulemanager.log.RuleExecutionLog;
import com.akbank.chatbot.rulemanager.mapper.model.ExecutionResponseDto;
import com.akbank.chatbot.rulemanager.mapper.model.FactInfo;
import com.akbank.chatbot.rulemanager.offer.service.ExecutionLogService;
import com.akbank.chatbot.rulemanager.offer.service.OfferService;
import com.akbank.chatbot.rulemanager.service.AccountInfoMappingService;
import com.akbank.chatbot.rulemanager.service.AppActionService;
import com.akbank.chatbot.rulemanager.service.ConfigurationService;
import com.akbank.chatbot.rulemanager.service.RuleService;
import com.akbank.chatbot.rulemanager.type.OfferReason;
import com.akbank.chatbot.rulemanager.util.RuleUtil;

@RestController
@RequestMapping("/execute")
public class ExecuteController {
	private static Logger logger = LoggerFactory.getLogger(ExecuteController.class);

	@Autowired
	RuleUtil ruleUtil;

	@Autowired
	OfferService offerService;

	@Autowired
	ConfigurationService config;

	@Autowired
	ExecutionLogService executionLogService;

	@Autowired
	AccountInfoMappingService accountInfoMappingService;

	@Autowired
	RuleService ruleService;

	@PostMapping(value = "/{code}")
	public ResponseEntity<ExecutionResponseDto> execute(@PathVariable String code, @RequestBody FactInfo factInfo,
			@RequestParam(required = false) String mode) {
		ExecutionResponseDto result = new ExecutionResponseDto();
		try {
			UUID uuid = UUID.randomUUID();
			result.setUuid(uuid.toString());
			ThreadContext.put(RuleConstants.LOG.THREAD_UUI_KEY, uuid.toString());
			result.setStartDate(new Date());
			result.setCode(code);

			if (factInfo != null) {
				result.setMbb(factInfo.getMbb());
			}

			if (StringUtils.isNoneBlank(code)) {
				if (config.isRuleMangerActive()) {
					Rules rules = ruleService.getRules(code);
					if (rules != null) {

						if (factInfo == null) {
							factInfo = new FactInfo();
						}

						logger.info("execute ruleName:{} mbb:", code);

						List<RuleExecutionLog> log = new ArrayList<>();

						Facts facts = new Facts();
						addFacts(facts, factInfo, code, log);
						
						RulesEngineParameters param = new RulesEngineParameters();
						param.setSkipOnFirstFailedRule(false);

						DefaultRulesEngine rulesEngine = new DefaultRulesEngine(param);
						rulesEngine.registerRuleListener(new AppRuleListener());

						// fire rule engine
						rulesEngine.fire(rules, facts);

						logger.info("execute result->{}", factInfo.executionResult());
						// save offer to mongo
						offerService.save(code, factInfo, factInfo.executionResult());

						fillResult(result, OfferReason.EXECUTON, factInfo.executionResult());

						if ("debug".equalsIgnoreCase(mode)) {
							result.setRuleExceutionLog(log);
						}
						result.setFacts(factInfo);

						return ResponseEntity.ok(result);
					} else {
						fillResult(result, OfferReason.ERROR, false, "No rule found for code");
						return ResponseEntity.status(HttpStatus.NOT_FOUND).body(result);
					}
				} else {
					fillResult(result, OfferReason.CONFIGURATION, false, "Rule manager is not active");
					logger.warn("execute rulemanager is not ACTIVE!!!");
					return ResponseEntity.ok(result);
				}
			} else {
				fillResult(result, OfferReason.ERROR, false, "Code is null or empty");
				return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(result);
			}
		} catch (Exception e) {
			throw e;
		} finally {
			ThreadContext.remove(RuleConstants.LOG.THREAD_UUI_KEY);
			executionLogService.save(result);
		}
	}

	private void addFacts(Facts facts, FactInfo factInfo, String code, List<RuleExecutionLog> log) {
		facts.put(RuleConstants.FACTS.FACT, factInfo);
		// rule code
		facts.put(RuleConstants.FACTS.ACTION_CODE, code);
		// add offer service
		facts.put(RuleConstants.FACTS.OFFER_SERVICE, offerService);
		// add account mapping info service
		facts.put(RuleConstants.FACTS.ACCOUNT_MAPPING_SERVICE, accountInfoMappingService);
		// mbb
		facts.put(RuleConstants.FACTS.MBB, factInfo.getMbb());

		facts.put(RuleConstants.FACTS.LOG, log);			
		
	}

	/**
	 * Fill result
	 * 
	 * @param result
	 * @param offerReason
	 * @param showOffer
	 */
	private void fillResult(ExecutionResponseDto result, OfferReason offerReason, boolean showOffer) {
		fillResult(result, offerReason, showOffer, null);
	}

	/**
	 * Fill result
	 * 
	 * @param result
	 * @param offerReason
	 * @param showOffer
	 * @param error
	 */
	private void fillResult(ExecutionResponseDto result, OfferReason offerReason, boolean showOffer, String error) {
		result.setReason(offerReason);
		result.setShowOffer(showOffer);
		result.setEndDate(new Date());
		result.setError(error);
		result.setCost(result.getEndDate().getTime() - result.getStartDate().getTime());
	}
}
