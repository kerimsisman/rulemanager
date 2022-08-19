package com.akbank.chatbot.rulemanager.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

import com.akbank.chatbot.rulemanager.entity.abs.AbstractEntity;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Entity(name = "configuration")
@Getter
@Setter
@ToString
@Table
public class Configuration extends AbstractEntity {
	@Column(name = "\"key\"", unique = true, nullable = false)
	private String key;

	private String value;

	private String description;
}
