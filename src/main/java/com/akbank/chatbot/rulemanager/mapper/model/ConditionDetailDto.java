package com.akbank.chatbot.rulemanager.mapper.model;

import javax.persistence.EnumType;
import javax.persistence.Enumerated;

import com.akbank.chatbot.rulemanager.type.ComparisonType;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = false)
public class ConditionDetailDto extends BaseDto {

	private Integer order;
	@Enumerated(EnumType.STRING)
	private ComparisonType compareType;

	private String sourceKey;

	private String sourceMethodInput;

	private String sourceMathematicalOperation;

	// Comparasion parameters
	private String comparisonValue;

	private String comparisonKey;

	private String comparisonValueMathematicalOperation;

	private String comparisonMethodInput;
}
