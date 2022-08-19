package com.akbank.chatbot.rulemanager.entity;

import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import com.akbank.chatbot.rulemanager.entity.abs.AbstractEntity;
import com.akbank.chatbot.rulemanager.type.Status;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Entity(name = "appAction")
@Getter
@Setter
@ToString
@Table(name = "action")
public class AppAction extends AbstractEntity {

	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	private Status status;

	private String name;

	@Column(name = "\"code\"", unique = true)
	private String code;

	private String description;

	@OneToMany(fetch = FetchType.LAZY, mappedBy = "appAction", orphanRemoval = true, cascade = CascadeType.ALL)
	private List<AppRule> rules;
}
