package com.iexceed.appzillonbanking.cbs.constants;

import lombok.experimental.UtilityClass;

@UtilityClass
public class CommonConstants {

	public static final String RELATED_NODE = "relatednode";
	public static final String ALIAS = "alias";
	public static final String SOAP_ACTION ="SOAPAction";
	public static final String DEFAULT_VALUE = "defaultvalue";
	public static final String XMLNS_COLON = "xmlns:";
	public static final String API_REQUEST = "apiRequest";

	public static final String FAILURE = "1";
	public static final String SUCCESS = "0";
	public static final String RESP_SUCCESS_STATUS = "SUCCESS";

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
	
}
