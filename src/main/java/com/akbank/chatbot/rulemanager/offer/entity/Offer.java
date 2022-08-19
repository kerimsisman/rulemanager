package com.akbank.chatbot.rulemanager.offer.entity;


import java.util.Date;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.lang.NonNull;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;

@Data
@NoArgsConstructor
@RequiredArgsConstructor
@AllArgsConstructor
@Document(collection = "offer")
public class Offer {

	@Id
	private String id;

	@NonNull
	private String mbb;

	@NonNull
	private Date date;

	@NonNull
	private String action;

	private Boolean showed;
}
