package com.akbank.chatbot.rulemanager.mapper.model;

import lombok.Data;

@Data
public class FactKeyValue   implements Cloneable{
	private String key;
	private String value;
	
	public FactKeyValue clone() throws CloneNotSupportedException {
		return (FactKeyValue) super.clone();
	}
}
