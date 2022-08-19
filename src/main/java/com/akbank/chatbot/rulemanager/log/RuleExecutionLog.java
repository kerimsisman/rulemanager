package com.akbank.chatbot.rulemanager.log;

import java.util.Date;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.experimental.NonFinal;

@Data
@AllArgsConstructor
@NonFinal
public class RuleExecutionLog implements Cloneable {
	private String ruleName;
	private int priority;
	private String mvel;
	private long cost;
	private Boolean status;
	private Date date;

	public RuleExecutionLog clone() throws CloneNotSupportedException {
		return (RuleExecutionLog) super.clone();
	}
}
