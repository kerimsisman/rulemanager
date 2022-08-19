package com.akbank.chatbot.rulemanager.offer.service;

import com.akbank.chatbot.rulemanager.mapper.model.FactInfo;

public interface OfferService {

	void save(String action, FactInfo factInfo, boolean isShowed);

	int showenOfferCount(String mbb, String action, Integer dayBefore);

	boolean isAnyOfferShowed(String mbb, String action, Integer limit);
}
