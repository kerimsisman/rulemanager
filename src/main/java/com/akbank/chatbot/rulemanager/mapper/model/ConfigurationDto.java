package com.akbank.chatbot.rulemanager.mapper.model;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = false)
public class ConfigurationDto extends BaseDto {
	private String key;
	private String value;
	private String description;
}
