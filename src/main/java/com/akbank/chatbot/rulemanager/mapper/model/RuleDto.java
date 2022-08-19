package com.akbank.chatbot.rulemanager.mapper.model;

import java.util.List;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper=false)
public class RuleDto extends BaseDto {
	private Integer order;
	
	private List<ConditionDto> conditions;
}
