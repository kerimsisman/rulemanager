package com.akbank.chatbot.rulemanager.service;

import java.util.List;

import com.akbank.chatbot.rulemanager.entity.AppAction;
import com.akbank.chatbot.rulemanager.mapper.model.ActionDto;

public interface AppActionService {
	/**
	 * Find by code
	 * 
	 * @param name
	 * @return
	 */
	AppAction findByCode(String name);

	/**
	 * Find AppAction by unique ID
	 * 
	 * @param id
	 * @return
	 */
	AppAction findById(Long id);

	/**
	 * Load all Actions
	 * 
	 * @return
	 */
	List<AppAction> findAll();

	/**
	 * Save Action dto
	 * 
	 * @param dto
	 * @return
	 */
	Long save(ActionDto dto);

	/**
	 * 
	 * @param dto
	 * @return
	 */
	Boolean update(AppAction source, ActionDto dto);

	/**
	 * Delete object
	 * 
	 * @param appAction
	 */
	void delete(AppAction appAction);

}
