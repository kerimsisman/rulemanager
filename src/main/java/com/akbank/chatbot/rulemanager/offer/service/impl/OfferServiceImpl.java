package com.akbank.chatbot.rulemanager.offer.service.impl;

import java.time.LocalDate;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import org.apache.logging.log4j.ThreadContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.akbank.chatbot.rulemanager.RuleConstants;
import com.akbank.chatbot.rulemanager.mapper.model.FactInfo;
import com.akbank.chatbot.rulemanager.offer.entity.Offer;
import com.akbank.chatbot.rulemanager.offer.repository.OfferRepository;
import com.akbank.chatbot.rulemanager.offer.service.OfferService;

@Service
public class OfferServiceImpl implements OfferService {
	private static Logger logger = LoggerFactory.getLogger(OfferServiceImpl.class);

	@Autowired
	OfferRepository offerRepo;

	@Override
	public void save(String action, FactInfo factInfo, boolean isShowed) {
		String mbb = factInfo.getMbb();
		new Thread(() -> {
			String uuid = ThreadContext.get(RuleConstants.LOG.THREAD_UUI_KEY);
			Offer offer = new Offer();
			offer.setAction(action);
			offer.setDate(new Date());
			offer.setMbb(mbb);
			offer.setShowed(isShowed);
			logger.info("saveToMongo uuid:{} mbb:{} isShowed:{}", uuid, isShowed);
			offerRepo.save(offer);
		}).run();

	}

	public int showenOfferCount(String mbb, String action, Integer dayBefore) {
		String uuid = ThreadContext.get(RuleConstants.LOG.THREAD_UUI_KEY);
		LocalDate dateToSearch = LocalDate.now().plusDays(dayBefore.longValue() * -1l);
		logger.info("showenOfferCount uuid:{} mbb:{} action:{} dayBefore:{} dateToSearch:{}", uuid, mbb, dayBefore,
				dateToSearch);
		List<Offer> offerList = offerRepo.getOfferForMbb(mbb, action, true, dateToSearch);

		int result = (offerList != null && !offerList.isEmpty()) ? offerList.size() : 0;
		logger.info(" showenOfferCount uuid:{} mbb:{} action:{} dayBefore:{} dateToSearch:{} result-->{}", uuid, mbb,
				dayBefore, dateToSearch, result);
		return result;
	}

	public boolean isAnyOfferShowed(String mbb, String action, Integer limit) {
		String uuid = ThreadContext.get(RuleConstants.LOG.THREAD_UUI_KEY);
		logger.info("isAnyOfferShowed  uuid:{} mbb:{},  action:{},  limit:{}", uuid, mbb, action, limit);
		List<Offer> offerList = offerRepo.getLastOffersForMbb(mbb, action, limit);
		boolean result;
		if (offerList != null && !offerList.isEmpty()) {
			Optional<Offer> offer = offerList.stream().filter(o -> o.getShowed()).findFirst();
			if (offer.isPresent()) {
				result = true;
			} else {
				result = false;
			}
		} else {
			result = false;
		}

		logger.info("isAnyOfferShowed uuid:{} mbb:{},  action:{},  limit:{} result:{}", uuid, mbb, action, limit,
				result);
		return result;
	}
}
