package com.akbank.chatbot.rulemanager.service.impl;

import java.util.List;
import java.util.Optional;

import javax.annotation.PostConstruct;

import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.akbank.chatbot.rulemanager.RuleConstants;
import com.akbank.chatbot.rulemanager.entity.Configuration;
import com.akbank.chatbot.rulemanager.mapper.model.ConfigurationDto;
import com.akbank.chatbot.rulemanager.repository.ConfigurationRepository;
import com.akbank.chatbot.rulemanager.service.ConfigurationService;

@Service
public class ConfigurationServiceImpl implements ConfigurationService {
	private static Logger logger = LoggerFactory.getLogger(ConfigurationServiceImpl.class);

	@Autowired
	ConfigurationRepository configurationRepo;

	@Autowired
	ModelMapper modelMapper;

	private boolean isRuleManagerActive = true;

	@PostConstruct
	public void init() {
		checkRuleManagerStatus();
	}

	public boolean isRuleMangerActive() {
		return isRuleManagerActive;
	}

	@Override
	public Configuration findById(Long id) {
		logger.info("findById {} sarted", id);
		Optional<Configuration> value = configurationRepo.findById(id);
		if (value.isPresent()) {
			logger.info("findById {} success", id);
			return value.get();
		} else {
			logger.info("findById {} error. Notfound", id);
			return null;
		}
	}

	@Override
	public List<Configuration> findAll() {
		return configurationRepo.findAll();
	}

	@Override
	public Long save(ConfigurationDto dto) {
		Configuration sourceObj = modelMapper.map(dto, Configuration.class);
		configurationRepo.save(sourceObj);
		return sourceObj.getId();
	}

	@Override
	public Boolean update(Configuration source, ConfigurationDto dto) {
		modelMapper.map(dto, source);
		configurationRepo.save(source);
		return true;
	}

	@Override
	public void delete(Configuration source) {
		configurationRepo.delete(source);
	}

	@Scheduled(cron = RuleConstants.CONFIGURATIONS.RULE_STATUS_CHECK_CRON)
	private void checkRuleManagerStatus() {
		logger.info("checkRuleManagerStatus sarted");
		Configuration result = configurationRepo.findByKey(RuleConstants.CONFIGURATIONS.RULE_MANAGER_STATUS_KEY);
		if (result != null && !"ACTIVE".equalsIgnoreCase(result.getValue())) {
			logger.info("checkRuleManagerStatus oldValue(isActive): {} newValue(isActive): {}", isRuleManagerActive,
					false);
			isRuleManagerActive = false;
		} else {
			logger.info("checkRuleManagerStatus oldValue(isActive): {} newValue(isActive): {}", isRuleManagerActive,
					true);

			isRuleManagerActive = true;
		}
		logger.info("checkRuleManagerStatus completed");
	}

}
