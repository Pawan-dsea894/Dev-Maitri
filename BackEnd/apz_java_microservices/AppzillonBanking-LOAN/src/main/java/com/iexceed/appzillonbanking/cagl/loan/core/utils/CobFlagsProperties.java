package com.iexceed.appzillonbanking.cagl.loan.core.utils;

public enum CobFlagsProperties {

	APPID_SELF_ONBOARDING("appIDSelfOnBoarding", "SELF ONBOARDING APP ID"),
	EXT_SYSTEM_DEDUPE_REQUIRED("extSystemDedupeRequired", "Flag to enable/disable external system dedupe check"),
	CASA_DELETE_RULE("accountDeleteRule", "Delete Rule for CASA"),
	DEPOSIT_DELETE_RULE("depositDeleteRule", "Delete Rule for Deposit"),
	CARDS_DELETE_RULE("cardsDeleteRule", "Delete Rule for Cards"),
	LOANS_DELETE_RULE("loanDeleteRule", "Delete Rule for loans"),
	FILE_UPLOAD("fileUpload", "Default file upload location"),
	DEFAULT_CARD_LOCATION("defaultCardLocation", "Default location for Card default themes"),
	ALLOW_PARTIAL_APPLICATION("allowPartiallyFilledApplication", "Flag for allowing partially filled applications"),
	DEMO_MODE("demoMode", "Demo mode flag"),
	ACCOUNT_STP("accountSTP", "Flag to enable direct application creation at CBS for Accounts"),
	DEPOSIT_STP("depositSTP", "Flag to enable direct application creation at CBS for Deposits"),
	CARD_STP("cardSTP", "Flag to enable direct application creation at CBS for Cards"),
	LOAN_STP("loanSTP", "Flag to enable direct application creation at CBS for Loans"),
	DEFAULT_CASA_PRODUCT("defaultCasaProductCode", "Default CASA product for Deposits"),
	DEFAULT_CASA_PRODUCTLN("defaultCasaProductCodeLN", "Default CASA product for Loans"),
	DEFAULT_CASA_GRP("defaultCasaProductGrpCode", "Default CASA product group for Deposits"),
	DEFAULT_BRANCH_CARDS("defaultBranchForCards", "Default branch for Cards"),
	EXTRACTED_ENTITIES_KEY("extractedEntities", "OCR KEY"),
	NATIONAL_ID_KEY("nationalId", "National Id KEY"),
	ADDRESS_KEY("address", "Address Key for OCR"),
	NUM_OF_REC_IN_WIDGET("numOfRecordsInWidget", "Number of records in each widget"),
	NUM_OF_DAYS_RECORDS("numOfDaysRecords", "Number of days to query"),
	
	ACC_CREATION_INTF("createCasaIntfName", "Interface name for account creation at external system"),
	DEP_ACC_CREATION_INTF("createDepositIntfName", "Interface name for deposit account creation at external system"),
	CARD_ACC_CREATION_INTF("createCardIntfName", "Interface name for card account creation at external system"),
	LOAN_ACC_CREATION_INTF("createLoanIntfName", "Interface name for loan account creation at external system"),
	
	CASA_DEP_ACC_CREATION_INTF("createCasaDepositIntfName", "Interface name for CASA and deposit account creation at external system"),
	CASA_LOAN_ACC_CREATION_INTF("createCasaLoanIntfName", "Interface name for CASA and loan account creation at external system");
	
	private final String key;
	private final String value;
	
	CobFlagsProperties(String key, String value) {
		this.key = key;
		this.value = value;
	}

	public String getKey() {
		return key;
	}

	public String getValue() {
		return value;
	}
}