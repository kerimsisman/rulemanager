package com.akbank.chatbot.rulemanager.service;

import java.util.List;

import com.akbank.chatbot.rulemanager.entity.KeyDescription;
import com.akbank.chatbot.rulemanager.mapper.model.KeyDescriptionDto;

public interface KeyDescriptionService {
	/**
	 * Find AppAction by unique ID
	 * 
	 * @param id
	 * @return
	 */
	KeyDescription findById(Long id);

	/**
	 * Load all Actions
	 * 
	 * @return
	 */
	List<KeyDescription> findAll();

	/**
	 * Save Action dto
	 * 
	 * @param dto
	 * @return
	 */
	Long save(KeyDescriptionDto dto);

	/**
	 * 
	 * @param dto
	 * @return
	 */
	Boolean update(KeyDescription source, KeyDescriptionDto dto);

	/**
	 * Delete object
	 * 
	 * @param appAction
	 */
	void delete(KeyDescription keyDescription);
}
