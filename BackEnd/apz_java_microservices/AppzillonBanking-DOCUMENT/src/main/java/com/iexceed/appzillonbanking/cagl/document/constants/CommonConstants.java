package com.iexceed.appzillonbanking.cagl.document.constants;

import lombok.experimental.UtilityClass;

@UtilityClass
public class CommonConstants {

	public static final String RELATED_NODE = "relatednode";
	public static final String ALIAS = "alias";
	public static final String SOAP_ACTION = "SOAPAction";
	public static final String DEFAULT_VALUE = "defaultvalue";
	public static final String XMLNS_COLON = "xmlns:";
	public static final String API_REQUEST = "apiRequest";

	public static final String FAILURE = "1";
	public static final String SUCCESS = "0";

	public static final String YES = "Y";
	public static final String SUCCESS_FLAG_S = "S";
	public static final String FAILURE_FLAG_F = "F";
	public static final String LOG_LEVEL_BOTH = "BOTH";
	public static final String LOG_LEVEL_REQUEST = "REQUEST";
	public static final String LOG_LEVEL_RESPONSE = "RESPONSE";
	public static final int MAX_TXN_LOG_LEN = 255;
	public static final String SOURCE_APZ = "APPZILLON";
	public static final String SERVICE_TXN_LOG_REQ = "servicetransactionlogging";
	public static final String SERVICE_TXN_LOG_MODE = "servicepayloadlogging";

	// loan constants
	public static final String PURPOSE = "purpose";
	public static final String CHARGES = "charges";
	public static final String CBAMT = "cbAmt";
	public static final String SHORTDESC = "shortDesc";
	public static final String PRODID = "productId";
	public static final String PRODTYPE = "prodType";
	public static final String SPOUSEINS = "spouseIns";
	public static final String INSURPER = "insurPer";
	public static final String DISBMODE = "disbMode";
	public static final String APPLICATIONID = "applicationId";
	public static final String VERSION_NO = "versionNo";
	public static final String CUSTOMERID = "customerId";
	public static final String LOANMODE = "loanMode";
	public static final String ADDLINE1 = "addLine1";
	public static final String STATE = "state";
	public static final String DISTRICT = "district";
	public static final String PINCODE = "pincode";
	public static final String VILLAGELOCALITY = "villageLocality";
	public static final String MEM_RELATION = "memRelation";
	public static final String STATUS = "status";
	
	public static final String FEATURE_DASHBOARD_WIDGETS = "DashboardStatus";
	public static final String FEATURE_SEARCH = "SearchApplication";
	// Roles
	public static final String ACCESS_PERMISSION_INITIATOR = "I";
	public static final String ACCESS_PERMISSION_APPROVER = "A";
	public static final String ACCESS_PERMISSION_BOTH = "B";
	public static final String ACCESS_PERMISSION_VIEWONLY = "VO";
	public static final String ACCESS_PERMISSION_VERIFIER = "V";

	public static final String IRIS_MSG = "IRIS_message";
}
