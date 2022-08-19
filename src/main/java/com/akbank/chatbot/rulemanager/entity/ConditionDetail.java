package com.akbank.chatbot.rulemanager.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import com.akbank.chatbot.rulemanager.entity.abs.AbstractEntity;
import com.akbank.chatbot.rulemanager.type.ComparisonType;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Entity
@Getter
@Setter
@ToString
public class ConditionDetail extends AbstractEntity {
	@ManyToOne(optional = true)
	@JoinColumn(name = "condition_id", nullable = false)
	private Condition condition;

	@Column(name = "\"order\"")
	private Integer order;

	@Enumerated(EnumType.STRING)
	private ComparisonType compareType;

	@JoinColumn(referencedColumnName = "key", name = "source_key", nullable = false)
	@Column
	private String sourceKey;

	private String sourceMethodInput;

	private String sourceMathematicalOperation;

	// Comparasion parameters
	private String comparisonValue;

	@JoinColumn(referencedColumnName = "key", name = "comparison_key", nullable = true)
	@Column
	private String comparisonKey;

	private String comparisonValueMathematicalOperation;

	private String comparisonMethodInput;
}
