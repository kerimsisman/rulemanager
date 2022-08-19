package com.akbank.chatbot.rulemanager.mapper.model;

import java.util.List;

import javax.persistence.EnumType;
import javax.persistence.Enumerated;

import com.akbank.chatbot.rulemanager.type.Status;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = false)
public class ActionDto extends BaseDto {
	@Enumerated(EnumType.STRING)
	private Status status;

	private String name;

	private String code;

	private String description;

	private List<RuleDto> rules;
}
