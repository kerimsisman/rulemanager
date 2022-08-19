package com.akbank.chatbot.rulemanager.offer.repository;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.mongodb.repository.Aggregation;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import com.akbank.chatbot.rulemanager.offer.entity.Offer;

public interface OfferRepository extends MongoRepository<Offer, String> {
	@Query("{'mbb' : ?0, 'action' : ?1, 'showed' : ?2, 'date' : { $gte: ?3} }")
	public List<Offer> getOfferForMbb(String mbb, String action, Boolean showed, LocalDate startDate);
	

	@Aggregation(pipeline = {
		    "{ '$match': { 'mbb' : ?0, 'action' : ?1 } }", 
		    "{ '$sort' : { 'date' : -1 } }", 
		    "{ '$limit' : ?2 }"
		  })
	public List<Offer> getLastOffersForMbb(String mbb, String action,Integer limit);
}