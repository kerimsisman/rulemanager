package com.akbank.chatbot.rulemanager.mapper.model;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.Date;

import lombok.Data;

@Data
public class AccountInfo implements Cloneable {
	private Long id;
	private String currency;
	private String accountType;
	private BigDecimal amount;
	private Date expiryDate;

	public long getElaspedExpiryDate() {
		return ChronoUnit.DAYS.between(expiryDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate(),
				LocalDate.now());
	}
	
	public AccountInfo clone() throws CloneNotSupportedException {
		return (AccountInfo) super.clone();
	}

}
