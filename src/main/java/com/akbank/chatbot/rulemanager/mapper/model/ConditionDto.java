package com.akbank.chatbot.rulemanager.mapper.model;

import java.util.List;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = false)
public class ConditionDto extends BaseDto {
	private Integer order;
	private String name;
	private List<ConditionDetailDto> conditionDetails;
}
