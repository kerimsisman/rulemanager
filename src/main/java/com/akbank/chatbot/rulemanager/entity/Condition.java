package com.akbank.chatbot.rulemanager.entity;

import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;

import com.akbank.chatbot.rulemanager.entity.abs.AbstractEntity;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Entity
@Getter
@Setter
@ToString
public class Condition extends AbstractEntity {

	@ManyToOne(optional = true)
	@JoinColumn(name = "app_rule_id", nullable = false)
	private AppRule appRule;

	private String name;

	@Column(name = "\"order\"")
	private Integer order;

	@OneToMany(fetch = FetchType.LAZY, mappedBy = "condition", orphanRemoval = true, cascade = CascadeType.ALL)
	@OrderBy("order")
	private List<ConditionDetail> conditionDetails;

}
