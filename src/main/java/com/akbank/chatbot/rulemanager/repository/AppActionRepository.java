package com.akbank.chatbot.rulemanager.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.akbank.chatbot.rulemanager.entity.AppAction;

public interface AppActionRepository extends JpaRepository<AppAction, Long> {
	/**
	 * Find by code
	 * 
	 * @param name
	 * @return
	 */
	AppAction findByCode(String name);

	/**
	 * Find All
	 * 
	 * @return
	 */
	@Query("SELECT DISTINCT p.code FROM appAction p WHERE p.code IS NOT NULL")
	List<String> findAllDisitnctCode();
}
