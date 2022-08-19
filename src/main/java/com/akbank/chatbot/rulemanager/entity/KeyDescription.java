package com.akbank.chatbot.rulemanager.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

import com.akbank.chatbot.rulemanager.entity.abs.AbstractEntity;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Entity(name = "keyDescription")
@Getter
@Setter
@ToString
@Table
public class KeyDescription extends AbstractEntity {

	@Column(name = "\"key\"", unique = true, nullable = false)
	private String key;

	@Column(nullable = false)
	private String name;

	@Column(nullable = true)
	private String description;

	@Column(nullable = true)
	private Class valueType;

	private boolean service;

	@Column(nullable = true)
	private String serviceMethod;

	private String factName;
}
