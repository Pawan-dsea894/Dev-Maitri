package com.iexceed.appzillonbanking.core.constants;

import lombok.experimental.UtilityClass;

import java.awt.*;

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
	public static final String NO_RECORD = "No Record Found";

	public static final String ACTIVESTATUS = "A";

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

	public static final String ERROR_CONST = "Error occurred while executing the rest service, error = ";
	public static final String PATH_PARAMETERS_LEFT_FILLER = "{";
	public static final String PATH_PARAMETERS_RIGHT_FILLER = "}";

	public static final String POST = "POST";
	public static final String PRIMARY_ACC_UPDT = "PRIMARY_ACC_UPDT";
	public static final String EMAIL_UPDT = "EMAIL_UPDT";
	public static final String MOBILE_UPDT = "MOBILE_UPDT";
	public static final String COMM_ADDRESS_UPDT = "COMM_ADDRESS_UPDT";

	public static final String ARIAL_FONT = "Arial";
	public static final String ARIAL_BOLD_FONT = "ArialBold";
	public static final int FONT_SIZE_9 = 9;
	public static final Color DARK_GRAY_COLOR = Color.DARK_GRAY;
	public static final Color LIGHT_GRAY_COLOR = Color.GRAY;
	public static final String ACCOUNT_CCY = "accountCcy";
	public static final String INSTALLMENT_NO = "installmentNo";
	public static final String PRINCIPAL_AMT = "principalAmt";
	public static final String INTEREST_AMT = "interestAmt";
	public static final String EMI_AMT = "emiAmt";
	public static final String PRINCIPAL_OUTSTANDING = "principalOutstanding";
	public static final String INTEREST_OUTSTANDING = "interestOutstanding";
	public static final String PAGE_FOOTER_DETAILS = "Iexceed Technologies | +91 123 980 888 | info@i-exceed.com";

	public static final String MSG_TYPE_CUSTOMER = "C";
	public static final String MSG_TYPE_ADMIN = "A";
	public static final String UNREAD_FLAG = "U";
	public static final String OPEN_STATUS = "OPEN";

	public static final String PADDING = "AES/CBC/PKCS5Padding";
	public static final String SHA_1 = "SHA-1";
	public static final String AES = "AES";
	public static final String SHA_256 = "SHA-256";
	public static final String SECURITY_KEY = "securitykey";
	public static final String CUSTOMER_SEGMENT = "customerSegmentEnabled";

	public static final String STATUS = "status";
	public static final String ERR_CODE = "errorCode";
	public static final String ERR_DESC = "errorDesc";
	public static final String ERR_MESSAGE = "errorMessage";

	public static final String API_RESPONSE = "apiResponse";
	public static final String RESPONSE_BODY = "ResponseBody";
	public static final String RESPONSE_OBJ = "responseObj";

	public static final String DEFAULT_SEGMENT = "defaultSegment";
	public static final String SINGLE_CURRENCY_LIMIT = "singleCurrencyLimit";

	public static final String RESPONSE_STATUS = "FAILURE";
	public static final String RESPONSE_STATUS_HOLD = "HOLD";
	public static final String APP_ID = "APZCBO";
	public static final String INTERFACE_NAME = "loanCBCheck";
	
	public static final String EXT_API_SUC_STATUS = "SUCCESS";
	public static final String EXT_API_ERR_STATUS = "ERROR";
	public static final String AUDIT_TABLE_PENDING_STATUS = "PENDING";
	public static final String AUDIT_TABLE_INPROGRESS_STATUS = "INPROGRESS";
	public static final String AUDIT_TABLE_SUCCESS_STATUS = "SUCCESS";
	public static final String AUDIT_TABLE_FAIL_STATUS = "FAIL";
	public static final String CB_CHECK_INTF_JSON_NAME = "loanCBCheck";
	public static final String PRECLOSURE_INTF_JSON_NAME = "preClosureLoan";
	

	public static final String SCHEDULER_QUERY = "select application_id,version_no,kmid from (select row_number() over(partition by resp.application_id order by version_no desc) as rownumber,\r\n"
			+ "resp.application_id,version_no,kmid from tb_uaob_cb_response resp \r\n"
			+ "join tb_uaco_application_master master on resp.application_id = master.application_id \r\n"
			+ "where status = " + String.format("'%s'", CommonConstants.RESPONSE_STATUS) + ") " + "where rownumber = 1";

}
