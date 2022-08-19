package com.akbank.chatbot.rulemanager.service.impl;

import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.ThreadContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.akbank.chatbot.rulemanager.RuleConstants;
import com.akbank.chatbot.rulemanager.mapper.model.AccountInfo;
import com.akbank.chatbot.rulemanager.mapper.model.FactInfo;
import com.akbank.chatbot.rulemanager.service.AccountInfoMappingService;
import com.akbank.chatbot.rulemanager.tmp.HesapListesiRecord;
import com.akbank.chatbot.rulemanager.util.RuleUtil;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

@Service
public class AccountInfoMappingServiceImpl implements AccountInfoMappingService {
	private static Logger logger = LoggerFactory.getLogger(AccountInfoMappingServiceImpl.class);

	@Autowired
	RuleUtil ruleUtil;

	public boolean mapAccountList(FactInfo factInfo) {
		String uuid = ThreadContext.get(RuleConstants.LOG.THREAD_UUI_KEY);
		try {
			logger.info("mapAccountList uuid:{} mbb:{}", uuid, factInfo.getMbb());
			List<AccountInfo> accountInfoList = new ArrayList<>();

			// load hesap listesi from service
			List<HesapListesiRecord> hesapListesi = loadHesapListesi();
			for (HesapListesiRecord record : hesapListesi) {
				AccountInfo maped = new AccountInfo();
				maped.setAccountType(record.getHesapTuru());
				maped.setAmount(record.getBakiye());
				maped.setCurrency(record.getDovizCinsi());
				maped.setExpiryDate(record.getVadeBitisTarihi());
				accountInfoList.add(maped);
			}

			// set account list
			factInfo.setAccountInfoList(accountInfoList);
			logger.info("mapAccountList uuid:{} mbb:{} completed true", uuid, factInfo.getMbb());

			return true;
		} catch (Exception e) {
			logger.error("mapAccountList uuid:{} mbb:{} Error", uuid, factInfo.getMbb(), e);
			return false;
		}
	}

	// TODO remove this method, this data will be loaded from service
	private List<HesapListesiRecord> loadHesapListesi() throws Exception {
		String json = ruleUtil.readFromInputStream("C:\\hesapListesi\\hesapListesi.txt");

		ObjectMapper objectMapper = new ObjectMapper();
		List<HesapListesiRecord> list = new ArrayList<>();
		try {
			TypeReference<List<HesapListesiRecord>> typeRef = new TypeReference<List<HesapListesiRecord>>() {
			};
			list = objectMapper.readValue(json, typeRef);
		} catch (Exception e) {
			System.out.println("exception " + e);
		}
		return list;
	}

}
