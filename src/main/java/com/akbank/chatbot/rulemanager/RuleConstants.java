package com.akbank.chatbot.rulemanager;

public class RuleConstants {

	public static final class FACTS {
		public static final String ACTION_INFO = "actionInfo";
		public static final String FACT = "fact";
		public static final String ACTION_CODE = "actionCode";
		public static final String OFFER_SERVICE = "offerService";
		public static final String ACCOUNT_MAPPING_SERVICE = "accountInfoMappingService";
		public static final String MBB = "mbb";
		public static final String SET_RESULT = FACT + ".setResult(true)";
		public static final String GET_RESULT = FACT + ".getResult()";
		public static final String LOG = "log";
	}

	public static final class CONDITION_PARAMETERS {
		public static final String INPUT = "#INPUT#";
	}

	public static final class CONFIGURATIONS {
		public static final String RULE_MANAGER_STATUS_KEY = "RULE_MANAGER_STATUS";
		public static final String RULE_STATUS_CHECK_CRON = "0/30 * * * * *";
	}

	public static final class LOG {
		public static final String THREAD_UUI_KEY = "ruleManager-execute-uuid";
	}

}
