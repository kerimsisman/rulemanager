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
import javax.persistence.Table;

import com.akbank.chatbot.rulemanager.entity.abs.AbstractEntity;

import lombok.Getter;
import lombok.Setter;

@Entity(name="appRule")
@Getter
@Setter
@Table(name="rule")
public class AppRule extends AbstractEntity {
	@ManyToOne(optional=true)
	@JoinColumn(name = "app_action_id", nullable = false)
	private AppAction appAction;

	@Column(name = "\"order\"")
	private Integer order;

	@OneToMany(fetch = FetchType.LAZY, mappedBy = "appRule", orphanRemoval = true, cascade = CascadeType.ALL)
	@OrderBy("order")
	private List<Condition> conditions;	 
}
