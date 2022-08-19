package com.akbank.chatbot.rulemanager.service;

import java.util.List;

import com.akbank.chatbot.rulemanager.entity.Configuration;
import com.akbank.chatbot.rulemanager.mapper.model.ConfigurationDto;

public interface ConfigurationService {

	/**
	 * Check and return if rule manager is active
	 * 
	 * @return
	 */
	boolean isRuleMangerActive();

	/**
	 * Load given configuration
	 * 
	 * @return
	 */
	Configuration findById(Long id);

	/**
	 * Load all Configurations
	 * 
	 * @return
	 */
	List<Configuration> findAll();

	/**
	 * Save Configuration dto
	 * 
	 * @param dto
	 * @return
	 */
	Long save(ConfigurationDto dto);

	/**
	 * 
	 * @param dto
	 * @return
	 */
	Boolean update(Configuration source, ConfigurationDto dto);

	/**
	 * Delete object
	 * 
	 * @param source
	 */
	void delete(Configuration source);

}
