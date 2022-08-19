package com.akbank.chatbot.rulemanager.mapper.model;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = false)
public class KeyDescriptionDto extends BaseDto {
	private String key;

	private String name;

	private String description;

	private Class valueType;

	private boolean service;

	private String serviceMethod;

	private String factName;
}
