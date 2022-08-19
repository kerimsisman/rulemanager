package com.akbank.chatbot.rulemanager.entity.listener;

import java.util.Date;

import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;

import org.apache.commons.lang3.StringUtils;

import com.akbank.chatbot.rulemanager.entity.abs.AbstractEntity;

public class PreActionListener {

	private static final String UNKNOWN = "unknown";

	@PrePersist
	private void beforeInsert(AbstractEntity object) {
		object.setCreatedAt(new Date());
		if (StringUtils.isAllBlank(object.getCreatedBy())) {
			object.setCreatedBy(UNKNOWN);
		}
	}

	@PreUpdate
	private void beforeUpdate(AbstractEntity object) {
		object.setUpdatedAt(new Date());
		if (StringUtils.isAllBlank(object.getUpdatedBy())) {
			object.setUpdatedBy(UNKNOWN);
		}
	}
}
