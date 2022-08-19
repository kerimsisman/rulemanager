package com.akbank.chatbot.rulemanager.type;

public enum ComparisonType {
	EQUALS("=="),
	/**
	 * 
	 */
	LESS("<"),
	/**
	 * 
	 */
	LESS_AND_EQUAL("<="),
	/**
	 * 
	 */
	GREATER(">"),
	/**
	 * 
	 */
	GREATER_AND_EQUAL(">="),
	/**
	 * 
	 */
	NOT_EQUAL("!=");

	private String sign;

	ComparisonType(String sign) {
		this.sign = sign;
	}

	public String getSign() {
		return this.sign;
	}

}
