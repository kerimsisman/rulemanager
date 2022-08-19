package com.akbank.chatbot.rulemanager.mapper.model;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import org.springframework.data.annotation.Transient;

import org.apache.commons.lang3.StringUtils;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class FactInfo implements Cloneable {
	@Transient
	private String mbb;

	private List<FactKeyValue> keyValueList;

	private List<AccountInfo> accountInfoList;

	@Transient
	private Boolean result;

	public String getAsString(String key) {
		return filterKeyValue(key);
	}

	public Boolean getAsBoolean(String key) {
		String object = filterKeyValue(key);
		return Boolean.valueOf(object);
	}

	public BigDecimal getAsBigDecimal(String key) {
		String object = filterKeyValue(key);
		if (object != null) {
			if (StringUtils.isNumeric(object)) {
				return BigDecimal.valueOf(Long.valueOf(object));
			} else {
				return null;
			}
		} else {
			return null;
		}
	}

	public Integer getAsInteger(String key) {
		String object = filterKeyValue(key);
		if (object != null) {
			if (StringUtils.isNumeric(object.trim())) {
				return Integer.valueOf(object.trim());
			} else {
				return null;
			}
		} else {
			return null;
		}
	}

	public Double getAsDouble(String key) {
		String object = filterKeyValue(key);
		if (object != null) {
			if (StringUtils.isNumeric(object)) {
				return Double.valueOf(object);
			} else {
				return null;
			}
		} else {
			return null;
		}
	}

	private String filterKeyValue(String key) {
		Optional<FactKeyValue> reuslt = keyValueList.stream().filter(k -> key.equals(k.getKey())).findFirst();
		return reuslt.isPresent() ? reuslt.get().getValue() : null;
	}

	public Boolean accountExists(String currency, String accountType) {
		return accountExists(currency, accountType, null, null, null, null);
	}

	public Boolean accountExists(String currency, String accountType, BigDecimal totamlAmount) {
		return accountExists(currency, accountType, totamlAmount, null, null, null);
	}

	public Boolean accountExists(String currency, String accountType, BigDecimal totamlAmount, Long expiredDueDate) {
		return accountExists(currency, accountType, totamlAmount, expiredDueDate, null, null);
	}

	public Boolean accountExists(String currency, String accountType, BigDecimal totamlAmount, Long expiredDueDate,
			BigDecimal greaterThanAmount) {
		return accountExists(currency, accountType, totamlAmount, expiredDueDate, greaterThanAmount, null);
	}

	public Boolean accountExists(String currency, String accountType, BigDecimal totamlAmount, Long expiredDueDate,
			BigDecimal greaterThanAmount, BigDecimal lessThanAmount) {

		List<AccountInfo> list = filter(currency, accountType, expiredDueDate, greaterThanAmount, lessThanAmount);
		if (list == null || list.isEmpty()) {
			return false;
		}

		if (totamlAmount == null) {
			return true;
		} else {
			BigDecimal sum = BigDecimal.ZERO;
			if (!list.isEmpty()) {
				sum = list.stream().map(x -> x.getAmount()).reduce(BigDecimal.ZERO, BigDecimal::add);
			}
			// totamlAmount equals or greater than
			return totamlAmount.compareTo(sum) <= 0;
		}
	}

	public BigDecimal totalAmount(String currency, String accountType) {
		return totalAmount(currency, accountType, null, null, null);
	}

	public BigDecimal totalAmount(String currency, String accountType, Long expiredDueDate) {
		return totalAmount(currency, accountType, expiredDueDate, null, null);
	}

	public BigDecimal totalAmount(String currency, String accountType, Long expiredDueDate,
			BigDecimal greaterThanAmount) {
		return totalAmount(currency, accountType, expiredDueDate, greaterThanAmount, null);
	}

	public BigDecimal totalAmount(String currency, String accountType, Long expiredDueDate,
			BigDecimal greaterThanAmount, BigDecimal lessThanAmount) {
		BigDecimal sum = BigDecimal.ZERO;
		List<AccountInfo> list = filter(currency, accountType, expiredDueDate, greaterThanAmount, lessThanAmount);
		if (list != null && !list.isEmpty()) {
			sum = list.stream().map(x -> x.getAmount()).reduce(BigDecimal.ZERO, BigDecimal::add);
		}
		return sum;
	}

	public Integer accountCount(String currency, String accountType) {
		return accountCount(currency, accountType, null, null, null);
	}

	public Integer accountCount(String currency, String accountType, Long expiredDueDate) {
		return accountCount(currency, accountType, expiredDueDate, null, null);
	}

	public Integer accountCount(String currency, String accountType, Long expiredDueDate,
			BigDecimal greaterThanAmount) {
		return accountCount(currency, accountType, expiredDueDate, greaterThanAmount, null);
	}

	public Integer accountCount(String currency, String accountType, Long expiredDueDate, BigDecimal greaterThanAmount,
			BigDecimal lessThanAmount) {
		List<AccountInfo> list = filter(currency, accountType, expiredDueDate, greaterThanAmount, lessThanAmount);
		return list != null ? list.size() : 0;
	}

	/**
	 * Filter acountInfo List
	 * 
	 * @param at
	 * @param amount
	 * @param expiredDueDate
	 * @return
	 */
	private List<AccountInfo> filter(String currency, String accountType, Long expiredDueDate,
			BigDecimal greaterThanAmount, BigDecimal lessThanAmount) {
		return accountInfoList.stream()
				.filter(a -> (StringUtils.isBlank(currency) || currency.equalsIgnoreCase(a.getCurrency()))
						&& (StringUtils.isBlank(accountType) || accountType.equalsIgnoreCase(a.getAccountType()))
						&& (greaterThanAmount == null || greaterThanAmount.compareTo(a.getAmount()) <= 0)
						&& (lessThanAmount == null || lessThanAmount.compareTo(a.getAmount()) > 0)
						&& (expiredDueDate == null
								|| (a.getElaspedExpiryDate() >= 0 && expiredDueDate >= a.getElaspedExpiryDate())))
				.collect(Collectors.toList());
	}

	public boolean executionResult() {
		if (this.getResult() == null) {
			return false;
		} else {
			return this.getResult() == null ? false : this.getResult();
		}
	}

	public FactInfo clone() throws CloneNotSupportedException {
		FactInfo clonedObject = (FactInfo) super.clone();

		List<FactKeyValue> clonedKeyValueList = new ArrayList<FactKeyValue>();
		if (keyValueList != null) {
			for (FactKeyValue row : keyValueList) {
				clonedKeyValueList.add(row.clone());
			}
		}

		List<AccountInfo> clonedAccountInfoList = new ArrayList<AccountInfo>();
		if (accountInfoList != null) {
			for (AccountInfo row : accountInfoList) {
				clonedAccountInfoList.add(row.clone());
			}
		}

		clonedObject.setAccountInfoList(clonedAccountInfoList);
		clonedObject.setKeyValueList(clonedKeyValueList);
		return clonedObject;
	}

}
