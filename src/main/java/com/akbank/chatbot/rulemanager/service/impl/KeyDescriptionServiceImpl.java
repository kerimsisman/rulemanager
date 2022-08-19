package com.akbank.chatbot.rulemanager.service.impl;

import java.util.List;
import java.util.Optional;

import javax.transaction.Transactional;

import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.akbank.chatbot.rulemanager.entity.KeyDescription;
import com.akbank.chatbot.rulemanager.mapper.model.KeyDescriptionDto;
import com.akbank.chatbot.rulemanager.repository.KeyDescriptionRepository;
import com.akbank.chatbot.rulemanager.service.KeyDescriptionService;

@Service
public class KeyDescriptionServiceImpl implements KeyDescriptionService {
	private static Logger logger = LoggerFactory.getLogger(KeyDescriptionServiceImpl.class);

	@Autowired
	KeyDescriptionRepository keyDescriptionRepo;

	@Autowired
	ModelMapper modelMapper;

	@Override
	public KeyDescription findById(Long id) {
		logger.info("findById {} sarted", id);
		Optional<KeyDescription> value = keyDescriptionRepo.findById(id);
		if (value.isPresent()) {
			logger.info("findById {} success", id);
			return value.get();
		} else {
			logger.info("findById {} error. Notfound", id);
			return null;
		}
	}

	@Override
	public List<KeyDescription> findAll() {
		return keyDescriptionRepo.findAll();
	}

	@Override
	@Transactional
	public Long save(KeyDescriptionDto dto) {
		KeyDescription object = modelMapper.map(dto, KeyDescription.class);
		object.setId(null);
		keyDescriptionRepo.save(object);
		return object.getId();
	}

	@Override
	@Transactional
	public Boolean update(KeyDescription source, KeyDescriptionDto dto) {
		modelMapper.map(dto, source);
		keyDescriptionRepo.save(source);
		return true;
	}

	@Override
	@Transactional
	public void delete(KeyDescription keyDescription) {
		keyDescriptionRepo.delete(keyDescription);

	}

}
