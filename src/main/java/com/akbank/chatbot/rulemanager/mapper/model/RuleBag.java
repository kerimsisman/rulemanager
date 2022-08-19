package com.akbank.chatbot.rulemanager.mapper.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RuleBag {
	private String conditionName;
	private String mvelString;
}
