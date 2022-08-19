package com.akbank.chatbot.rulemanager.service.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import javax.transaction.Transactional;

import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.akbank.chatbot.rulemanager.entity.AppAction;
import com.akbank.chatbot.rulemanager.entity.AppRule;
import com.akbank.chatbot.rulemanager.entity.Condition;
import com.akbank.chatbot.rulemanager.entity.ConditionDetail;
import com.akbank.chatbot.rulemanager.entity.abs.BaseEntity;
import com.akbank.chatbot.rulemanager.mapper.model.ActionDto;
import com.akbank.chatbot.rulemanager.mapper.model.BaseDto;
import com.akbank.chatbot.rulemanager.mapper.model.ConditionDetailDto;
import com.akbank.chatbot.rulemanager.mapper.model.ConditionDto;
import com.akbank.chatbot.rulemanager.mapper.model.RuleDto;
import com.akbank.chatbot.rulemanager.repository.AppActionRepository;
import com.akbank.chatbot.rulemanager.repository.KeyDescriptionRepository;
import com.akbank.chatbot.rulemanager.service.AppActionService;
import com.akbank.chatbot.rulemanager.service.RuleService;
import com.akbank.chatbot.rulemanager.util.RuleUtil;

@Service
public class AppActionServiceImpl implements AppActionService {
	private static Logger logger = LoggerFactory.getLogger(AppActionServiceImpl.class);

	@Autowired
	AppActionRepository appActionRepo;

	@Autowired
	KeyDescriptionRepository keyDescriptionRepository;

	@Autowired
	ModelMapper modelMapper;

	@Autowired
	RuleUtil ruleUtil;

	@Autowired
	RuleService ruleService;

	public AppAction findByCode(String name) {
		return appActionRepo.findByCode(name);
	}

	public AppAction findById(Long id) {
		logger.info("findById {} sarted", id);
		Optional<AppAction> value = appActionRepo.findById(id);
		if (value.isPresent()) {
			logger.info("findById {} success", id);
			return value.get();
		} else {
			logger.info("findById {} error. Notfound", id);
			return null;
		}
	}

	public List<AppAction> findAll() {
		return appActionRepo.findAll();
	}

	@Transactional
	public void delete(AppAction appAction) {
		appActionRepo.delete(appAction);
		// TODO delete this line after redis pub-sub
		ruleService.loadRules();
	}

	@Transactional
	public Long save(ActionDto dto) {
		// clear and set code
		String code = ruleUtil.clearTurkishChars(dto.getCode());
		code = code.replaceAll("\\s+", "_").toUpperCase();
		dto.setCode(code);

		AppAction appAction = modelMapper.map(dto, AppAction.class);
		mapParentObjects(appAction);

		appActionRepo.save(appAction);

		// TODO delete this line after redis pub-sub
		ruleService.loadRules();

		return appAction.getId();
	}

	@Transactional
	public Boolean update(AppAction source, ActionDto input) {
		// Map child objects
		fillRules(source, input);
		source.setCode(input.getCode());
		source.setName(input.getName());
		source.setStatus(input.getStatus());
		source.setDescription(input.getDescription());
		appActionRepo.save(source);

		// TODO delete this line after redis pub-sub
		ruleService.loadRules();
		return true;
	}

	/**
	 * Map parent oject for rule,condition and conditionDetails
	 * 
	 * @param appAction
	 */
	private void mapParentObjects(AppAction appAction) {
		// fill rules
		appAction.getRules().stream().forEach(r -> {
			r.setId(null);
			r.setAppAction(appAction);
			if (r.getConditions() != null && !r.getConditions().isEmpty()) {
				r.getConditions().forEach(c -> {
					c.setId(null);
					c.setAppRule(r);
					if (c.getConditionDetails() != null && !c.getConditionDetails().isEmpty()) {
						c.getConditionDetails().forEach(d -> {
							d.setId(null);
							d.setCondition(c);
						});
					}
				});
			}
		});

	}

	public void fillRules(AppAction source, ActionDto dto) {
		List<AppRule> removeList = new ArrayList<>();
		List<RuleDto> insertList = new ArrayList<>();
		List<AppRule> updateList = new ArrayList<>();
		// Find update and delete operation
		for (AppRule row : source.getRules()) {
			if (dto.getRules() == null || dto.getRules().isEmpty()
					|| !ruleUtil.isExistsInInput(row.getId(), new ArrayList<BaseDto>(dto.getRules()))) {
				removeList.add(row);
			} else {
				updateList.add(row);
			}
		}

		// Find Insert operation
		for (RuleDto rowDto : dto.getRules()) {
			if (rowDto.getId() == null) {
				insertList.add(rowDto);
			} else if (!ruleUtil.isExistsInSource(rowDto.getId(), new ArrayList<BaseEntity>(source.getRules()))) {
				rowDto.setId(null);
				insertList.add(rowDto);
			}
		}

		// delete
		if (!removeList.isEmpty())
			source.getRules().removeAll(removeList);

		// Update
		for (AppRule updateRow : updateList) {
			Optional<RuleDto> matched = dto.getRules().stream().filter(c -> updateRow.getId().equals(c.getId()))
					.findFirst();
			if (matched.isPresent() && matched.get() != null) {
				updateRow.setOrder(matched.get().getOrder());
				// fill condition
				fillCondition(updateRow, matched.get());
			}
		}

		// insert
		for (RuleDto insertRow : insertList) {
			if (source.getRules() == null) {
				source.setRules(new ArrayList<>());
			}
			AppRule mapedRule = modelMapper.map(insertRow, AppRule.class);
			mapedRule.setAppAction(source);
			source.getRules().add(mapedRule);
			if (mapedRule.getConditions() != null) {
				for (Condition condition : mapedRule.getConditions()) {
					condition.setId(null);
					condition.setAppRule(mapedRule);
					if (condition.getConditionDetails() != null) {
						for (ConditionDetail conditionDetail : condition.getConditionDetails()) {
							conditionDetail.setId(null);
							conditionDetail.setCondition(condition);
						}
					}
				}
			}
		}
	}

	private void fillCondition(AppRule source, RuleDto dto) {
		List<Condition> removeList = new ArrayList<>();
		List<ConditionDto> insertList = new ArrayList<>();
		List<Condition> updateList = new ArrayList<>();
		// Find update and delete operation
		for (Condition rule : source.getConditions()) {
			if (dto.getConditions() == null || dto.getConditions().isEmpty()
					|| !ruleUtil.isExistsInInput(rule.getId(), new ArrayList<BaseDto>(dto.getConditions()))) {
				removeList.add(rule);
			} else {
				updateList.add(rule);
			}
		}

		// Find Insert operation
		for (ConditionDto rowDto : dto.getConditions()) {
			if (rowDto.getId() == null) {
				insertList.add(rowDto);
			} else if (!ruleUtil.isExistsInSource(rowDto.getId(), new ArrayList<BaseEntity>(source.getConditions()))) {
				rowDto.setId(null);
				insertList.add(rowDto);
			}
		}

		// delete
		if (!removeList.isEmpty())
			source.getConditions().removeAll(removeList);

		// Update
		for (Condition updateRow : updateList) {
			Optional<ConditionDto> matched = dto.getConditions().stream()
					.filter(c -> updateRow.getId().equals(c.getId())).findFirst();
			if (matched.isPresent() && matched.get() != null) {
				updateRow.setOrder(matched.get().getOrder());
				updateRow.setName(matched.get().getName());
				fillConditionDetail(updateRow, matched.get());
			}
		}

		// insert
		for (ConditionDto insertRow : insertList) {
			if (source.getConditions() == null) {
				source.setConditions(new ArrayList<>());
			}
			Condition mapedRule = modelMapper.map(insertRow, Condition.class);
			mapedRule.setAppRule(source);
			source.getConditions().add(mapedRule);
			if (mapedRule.getConditionDetails() != null) {
				for (ConditionDetail conditionDetail : mapedRule.getConditionDetails()) {
					conditionDetail.setCondition(mapedRule);
				}
			}
		}
	}

	private void fillConditionDetail(Condition source, ConditionDto dto) {
		List<ConditionDetail> removeList = new ArrayList<>();
		List<ConditionDetailDto> insertList = new ArrayList<>();
		List<ConditionDetail> updateList = new ArrayList<>();
		// Find update and delete operation
		for (ConditionDetail rule : source.getConditionDetails()) {
			if (dto.getConditionDetails() == null || dto.getConditionDetails().isEmpty()
					|| !ruleUtil.isExistsInInput(rule.getId(), new ArrayList<BaseDto>(dto.getConditionDetails()))) {
				removeList.add(rule);
			} else {
				updateList.add(rule);
			}
		}

		// Find Insert operation
		for (ConditionDetailDto rowDto : dto.getConditionDetails()) {
			if (rowDto.getId() == null) {
				insertList.add(rowDto);
			} else if (!ruleUtil.isExistsInSource(rowDto.getId(),
					new ArrayList<BaseEntity>(source.getConditionDetails()))) {
				rowDto.setId(null);
				insertList.add(rowDto);
			}
		}

		// delete
		if (!removeList.isEmpty())
			source.getConditionDetails().removeAll(removeList);

		// Update
		for (ConditionDetail updateRow : updateList) {
			Optional<ConditionDetailDto> matched = dto.getConditionDetails().stream()
					.filter(c -> updateRow.getId().equals(c.getId())).findFirst();
			if (matched.isPresent() && matched.get() != null) {
				ConditionDetailDto inputObj = matched.get();
				modelMapper.map(inputObj, updateRow);
			}
		}

		// insert
		for (ConditionDetailDto insertRow : insertList) {
			if (source.getConditionDetails() == null) {
				source.setConditionDetails(new ArrayList<>());
			}
			ConditionDetail mapedRule = modelMapper.map(insertRow, ConditionDetail.class);
			mapedRule.setCondition(source);
			source.getConditionDetails().add(mapedRule);
		}
	}

}
