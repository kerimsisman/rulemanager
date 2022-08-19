package com.akbank.chatbot.rulemanager.offer.entity;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.EnumType;
import javax.persistence.Enumerated;

import org.springframework.data.mongodb.core.mapping.Document;

import com.akbank.chatbot.rulemanager.log.RuleExecutionLog;
import com.akbank.chatbot.rulemanager.mapper.model.FactInfo;
import com.akbank.chatbot.rulemanager.type.OfferReason;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Document(collection = "executionLog")
public class ExecutionLog implements Cloneable {
	private String uuid;
	private String code;
	private String mbb;
	private boolean showOffer;
	@Enumerated(EnumType.STRING)
	private OfferReason reason;
	private long cost;

	@JsonIgnore
	private Date startDate;
	@JsonIgnore
	private Date endDate;
	@JsonIgnore
	private String error;

	@JsonIgnore
	private FactInfo facts;

	@JsonInclude(Include.NON_NULL)
	private List<RuleExecutionLog> ruleExceutionLog;

	public ExecutionLog clone() throws CloneNotSupportedException {
		ExecutionLog clonedObject = (ExecutionLog) super.clone();
		List<RuleExecutionLog> clonedRuleExceutionLog = new ArrayList<RuleExecutionLog>();
		if (ruleExceutionLog != null) {
			for (RuleExecutionLog row : ruleExceutionLog) {
				clonedRuleExceutionLog.add(row.clone());
			}
		}
		clonedObject.setRuleExceutionLog(clonedRuleExceutionLog);
		clonedObject.setFacts(facts != null ? facts.clone() : null);
		return clonedObject;
	}

}
