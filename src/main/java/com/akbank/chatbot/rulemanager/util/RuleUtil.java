package com.akbank.chatbot.rulemanager.util;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.jeasy.rules.api.Rule;
import org.jeasy.rules.api.Rules;
import org.jeasy.rules.mvel.MVELRule;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.akbank.chatbot.rulemanager.RuleConstants;
import com.akbank.chatbot.rulemanager.entity.AppAction;
import com.akbank.chatbot.rulemanager.entity.AppRule;
import com.akbank.chatbot.rulemanager.entity.Condition;
import com.akbank.chatbot.rulemanager.entity.ConditionDetail;
import com.akbank.chatbot.rulemanager.entity.KeyDescription;
import com.akbank.chatbot.rulemanager.entity.abs.BaseEntity;
import com.akbank.chatbot.rulemanager.mapper.model.BaseDto;
import com.akbank.chatbot.rulemanager.mapper.model.RuleBag;
import com.akbank.chatbot.rulemanager.repository.KeyDescriptionRepository;

@Service
public class RuleUtil {
	private static Logger logger = LoggerFactory.getLogger(RuleUtil.class);

	@Autowired
	ModelMapper modelMapper;

	@Autowired
	KeyDescriptionRepository keyDescriptionRepo;

	/**
	 * Create rules from appAction
	 * 
	 * @param appAction
	 * @param resultCommand
	 * @return
	 */
	public Rules createExpresion(AppAction appAction, String resultCommand) {
		Rules rules = new Rules();
		if (appAction != null) {
			for (AppRule appRule : appAction.getRules()) {
				List<RuleBag> ruleBagList = createRuleList(appRule);

				int idx = 0;
				for (RuleBag bag : ruleBagList) {
					if (StringUtils.isNoneBlank(bag.getMvelString())) {
						logger.info("appAction.getName()->{}", bag.getMvelString());
						Rule rule = new MVELRule().priority(idx++)
								.name("#actionId:" + appAction.getId() + " actionName:" + appAction.getName()
										+ " #ruleId:" + appRule.getId() + " " + bag.getConditionName())
								.description(bag.getMvelString().trim()).when(bag.getMvelString().trim())
								.then(resultCommand);

						rules.register(rule);
					}

				}

			}
		}
		return rules;
	}

	/**
	 * Create and list from conditions
	 * 
	 * @param appRule
	 * @return
	 */
	public List<RuleBag> createRuleList(AppRule appRule) {
		List<RuleBag> andRuleList = new ArrayList<>();
		for (Condition condition : appRule.getConditions()) {
			List<String> orList = new ArrayList<>();
			for (ConditionDetail detail : condition.getConditionDetails()) {

				String value = detail.getComparisonValue() != null ? detail.getComparisonValue() : "";
				if (StringUtils.isBlank(value) && detail.getComparisonKey() != null) {
					value = createMethod(detail.getComparisonMethodInput(), detail.getComparisonKey(),
							detail.getComparisonValueMathematicalOperation());
				} else {
					KeyDescription keyDescription = keyDescriptionRepo.findByKey(detail.getSourceKey());
					if (keyDescription != null && keyDescription.getClass() != null
							&& keyDescription.getValueType().getName().equals(String.class.getName())) {
						value = "'" + value + "'";
					}

				}

				String sign = "";
				if (detail.getCompareType() != null) {
					sign = detail.getCompareType().getSign();
				}

				String signAndValue = "";
				if (StringUtils.isNotBlank(sign) && StringUtils.isNotBlank(value)) {
					signAndValue = " " + sign + " " + value;
				}

				orList.add(createMethod(detail.getSourceMethodInput(), detail.getSourceKey(),
						detail.getSourceMathematicalOperation()) + signAndValue);

			}

			if (!orList.isEmpty()) {
				String conditonDetail = String.join(" || ", orList);
				if (condition.getConditionDetails().size() > 1) {
					conditonDetail = "(" + conditonDetail + ")";
				}
				String conditionName = "#conditionId:" + condition.getId() + " conditionName:" + condition.getName();
				andRuleList.add(new RuleBag(conditionName, conditonDetail));
			}
		}
		return andRuleList;
	}

	/**
	 * Fill input values from conditions
	 * 
	 * @param detail
	 * @param getMethod
	 * @return
	 */
	private String createMethod(String input, String key, String mathematicalOperation) {
		if (StringUtils.isNoneBlank(key)) {
			KeyDescription keyDescription = keyDescriptionRepo.findByKey(key);
			if (keyDescription != null) {
				String result = "";
				if (!keyDescription.isService()) {
					result = keyDescription.getFactName() + ".getAs" + keyDescription.getValueType().getSimpleName()
							+ "(" + "'" + keyDescription.getKey() + "')";
				} else {
					result = keyDescription.getFactName() + "." + keyDescription.getServiceMethod();
				}

				if (StringUtils.isBlank(result)) {
					logger.error("createMethod getMethod is null");
				}
				if (StringUtils.isNotBlank(input)) {
					result = result.replace(RuleConstants.CONDITION_PARAMETERS.INPUT, input);
				}
				if (StringUtils.isNotBlank(mathematicalOperation)) {
					result = result + mathematicalOperation.trim();
				}
				return result;
			} else {
				logger.warn("createMethod key not exist on DB");
				return "";
			}
		} else {
			logger.warn("createMethod attributeType is null");
			return "";
		}
	}

	/**
	 * Clear turkish chars
	 * 
	 * @param str
	 * @return
	 */
	public String clearTurkishChars(String str) {
		String ret = str;
		char[] turkishChars = new char[] { 0x131, 0x130, 0xFC, 0xDC, 0xF6, 0xD6, 0x15F, 0x15E, 0xE7, 0xC7, 0x11F,
				0x11E };
		char[] englishChars = new char[] { 'i', 'I', 'u', 'U', 'o', 'O', 's', 'S', 'c', 'C', 'g', 'G' };
		for (int i = 0; i < turkishChars.length; i++) {
			ret = ret.replaceAll(new String(new char[] { turkishChars[i] }),
					new String(new char[] { englishChars[i] }));
		}
		return ret;
	}

	public boolean isExistsInSource(Long id, List<BaseEntity> list) {
		boolean exists = false;
		for (BaseEntity row : list) {
			if (row != null && row.getId() != null && row.getId().equals(id)) {
				exists = true;
				break;
			}
		}
		return exists;
	}

	public boolean isExistsInInput(Long id, List<BaseDto> list) {
		boolean exists = false;
		for (BaseDto row : list) {
			if (row != null && row.getId() != null && row.getId().equals(id)) {
				exists = true;
				break;
			}
		}
		return exists;
	}

	// TODO delete this code after hesapListesi service call.
	public String readFromInputStream(String filePath) throws IOException {
		Path path = Paths.get(filePath);
		StringBuilder sb = new StringBuilder();
		Files.readAllLines(path).stream().forEach(l -> sb.append(l));
		return sb.toString();
	}
}
