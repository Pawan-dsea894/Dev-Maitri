package com.iexceed.appzillonbanking.core.utils;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.math.BigDecimal;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.sql.Date;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Random;
import java.util.regex.Pattern;
import java.util.zip.CRC32;
import java.util.zip.Checksum;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import javax.xml.bind.DatatypeConverter;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.iexceed.appzillonbanking.core.constants.CommonConstants;
import com.iexceed.appzillonbanking.core.payload.ErrorParameterValues;
import com.iexceed.appzillonbanking.core.payload.ExternalServiceDetails;
import com.iexceed.appzillonbanking.core.payload.Header;
import com.iexceed.appzillonbanking.core.payload.HeaderData;
import com.iexceed.appzillonbanking.core.payload.Response;
import com.iexceed.appzillonbanking.core.payload.ResponseBody;
import com.iexceed.appzillonbanking.core.payload.ResponseHeader;

@Component
public class CommonUtils {
	private static final Logger logger = LogManager.getLogger(CommonUtils.class);
	protected static Map<String, String> urlMap = null;
	protected static Map<String, String> cutOffMap = null;
	protected static Map<String, String> commonProperties = null;

	protected static Map<String, String> externalProperties = null;
	protected static Map<String, List<String>> productTypeMap = null;
	protected static Map<String, List<String>> statusCodeMap = null;
	private static Map<String, Map<String, ErrorParameterValues>> hostToApzErrorMap = null;

	public static final String FAILURE = "1";
	public static final String SUCCESS = "0";
	public static final String NR100 = "NR100";
	protected static Map<String, ExternalServiceDetails> externalServiceDetailsMap = null;
	private static Random random = new Random();
	protected static Map<String, String> errorCodeMap = null;

	public static final String ACCOUNT_NUMBER = "accountNo";
	public static final String ENCRYPTED_ACCOUNT_NUMBER = "encryptedAccountNumber";
	public static final String ACCOUNT_NUMBER_PARENT_NODE = "accountNumberParentNode";
	public static final String PRIMARY_CCY = "primaryCurrency";
	public static final String DATETIMEFORMAT = "yyyy-MM-dd HH:mm:ss";
	public static final String DDMMYYSLASHFORMAT = "dd/MM/yyyy";
	public static final String NO_RECORD = "No Record Found";

	private static final Charset UTF8 = StandardCharsets.UTF_8;

	public static String[] getCutOffMap(String keyname) {
		return cutOffMap.get(keyname).split(",");
	}

	private CommonUtils() {
	}

	public static ExternalServiceDetails getExternalServiceDetails(String keyname) {
		return externalServiceDetailsMap.get(keyname);
	}

	public static void initializeErrorCodeMap(Map<String, String> errorCodeMaps) {
		errorCodeMap = errorCodeMaps;
	}

	public static void generateResponseHeader(String serviceCode, ResponseHeader responseHeader) {
		/* Generates response for APIs with external service call */
		responseHeader.setHttpStatus(HttpStatus.OK);
		if (serviceCode.equals(Errors.SEVICENOTREACHABLEERROR.getErrorCode())
				|| serviceCode.equals(Errors.SERVICEERROR.getErrorCode())) {
			responseHeader.setResponseCode(FAILURE);
			responseHeader.setErrorCode(serviceCode);
			responseHeader.setResponseMessage(Errors.SERVICEERROR.getErrorMessage());
		} else if (serviceCode.equals(Errors.SEVICETIMEDOUTERROR.getErrorCode())) {
			responseHeader.setResponseCode(FAILURE);
			responseHeader.setHttpStatus(HttpStatus.REQUEST_TIMEOUT);
		} else {
			responseHeader.setResponseCode(SUCCESS);
		}
	}

	public static void generateHeaderForNoResult(ResponseHeader responseHeader) {
		responseHeader.setHttpStatus(HttpStatus.OK);
		// responseHeader.setErrorCode(Errors.NORECORD.getErrorCode());
		responseHeader.setResponseMessage(Errors.NORECORD.getErrorMessage());
		responseHeader.setResponseCode(Errors.NORECORD.getErrorCode());
	}

	public static void generateHeaderForDBError(ResponseHeader responseHeader) {
		responseHeader.setHttpStatus(HttpStatus.OK);
		responseHeader.setResponseCode(FAILURE);
		responseHeader.setResponseMessage(Errors.PERSISTENCEERROR.getErrorMessage());
	}

	public static void generateHeaderForGenericError(ResponseHeader responseHeader) {
		responseHeader.setHttpStatus(HttpStatus.OK);
		responseHeader.setResponseCode(FAILURE);
		responseHeader.setResponseMessage(Errors.PROCESSINGREQUESTERROR.getErrorMessage());
	}

	public static void generateHeaderForCalcError(ResponseHeader responseHeader) {
		responseHeader.setHttpStatus(HttpStatus.OK);
		responseHeader.setResponseCode(FAILURE);
		responseHeader.setResponseMessage(Errors.CALCULATIONERROR.getErrorMessage());
	}

	public static void generateHeaderForSuccess(ResponseHeader responseHeader) {
		responseHeader.setHttpStatus(HttpStatus.OK);
		responseHeader.setResponseCode(SUCCESS);
		responseHeader.setResponseMessage("");
	}

	public static void generateHeaderForFailure(ResponseHeader responseHeader, String errorMsg) {
		responseHeader.setHttpStatus(HttpStatus.OK);
		responseHeader.setResponseCode(FAILURE);
		responseHeader.setResponseMessage(errorMsg);
	}

	public static void generateHeaderForDbInsertionFailure(ResponseHeader responseHeader) {
		responseHeader.setHttpStatus(HttpStatus.OK);
		responseHeader.setResponseMessage(Errors.DATAINSERTIONFAILURE.getErrorMessage());
		responseHeader.setResponseCode(Errors.DATAINSERTIONFAILURE.getErrorCode());
	}

	public static void generateHeaderForDbUpdationFailure(ResponseHeader responseHeader) {
		responseHeader.setHttpStatus(HttpStatus.OK);
		responseHeader.setResponseMessage(Errors.DATAUPDATIONFAILURE.getErrorMessage());
		responseHeader.setResponseCode(Errors.DATAUPDATIONFAILURE.getErrorCode());
	}

	public static void generateHeaderForDbDeletionFailure(ResponseHeader responseHeader) {
		responseHeader.setHttpStatus(HttpStatus.OK);
		responseHeader.setResponseMessage(Errors.DATADELETIONFAILURE.getErrorMessage());
		responseHeader.setResponseCode(Errors.DATADELETIONFAILURE.getErrorCode());
	}

	public static void generateHeaderForChargeDataFailure(ResponseHeader responseHeader) {
		responseHeader.setHttpStatus(HttpStatus.OK);
		responseHeader.setResponseMessage("Please insert the charge details in TB_ABCH_CHARGE_MAINTENANCE table");
		responseHeader.setResponseCode(FAILURE);
	}

	public static void generateHeaderForChargeFeeFailure(ResponseHeader responseHeader) {
		responseHeader.setHttpStatus(HttpStatus.OK);
		responseHeader.setResponseMessage("Failed to obtain the charge amount");
		responseHeader.setResponseCode(FAILURE);
	}

	public static void generateHeaderForAddBeneficiaryFailure(ResponseHeader responseHeader) {
		responseHeader.setHttpStatus(HttpStatus.OK);
		responseHeader.setResponseMessage("Matching beneficiary records already exists");
		responseHeader.setResponseCode(FAILURE);
	}

	public static void generateHeaderForAddBeneficiaryIFSCFailure(ResponseHeader responseHeader, String responseMessage,
			String errorCode) {
		responseHeader.setHttpStatus(HttpStatus.OK);
		responseHeader.setResponseMessage(responseMessage);
		responseHeader.setErrorCode(errorCode);
		responseHeader.setResponseCode(FAILURE);
	}

	public static void generateHeaderForLimitsDataFailure(ResponseHeader responseHeader) {
		responseHeader.setHttpStatus(HttpStatus.OK);
		responseHeader.setResponseMessage("Please insert the primary currency value in TB_ABMI_COMMON_CODES table");
		responseHeader.setResponseCode(FAILURE);
	}

	public static void generateHeaderForScheduledTransferDateFailure(ResponseHeader responseHeader) {
		responseHeader.setHttpStatus(HttpStatus.OK);
		responseHeader.setResponseMessage("Schedule start date should be greater than today's date.");
		responseHeader.setResponseCode(FAILURE);
	}

	public static void generateHeaderForScheduledTransferFailure(ResponseHeader responseHeader) {
		responseHeader.setHttpStatus(HttpStatus.OK);
		responseHeader.setResponseMessage("Failure in adding/updating schedule transfer.");
		responseHeader.setResponseCode(FAILURE);
	}

	public static void generateHeaderForExchangeRateFailure(ResponseHeader responseHeader, String errorMsg) {
		responseHeader.setHttpStatus(HttpStatus.OK);
		responseHeader.setResponseMessage(errorMsg);
		responseHeader.setResponseCode(FAILURE);
	}

	public static void generateHeaderForBeneficiaryDeletionFailure(ResponseHeader responseHeader) {
		responseHeader.setHttpStatus(HttpStatus.OK);
		responseHeader.setResponseMessage(Errors.BENEFICIARYDELETIONERROR.getErrorMessage());
		responseHeader.setResponseCode(Errors.BENEFICIARYDELETIONERROR.getErrorCode());
	}

	public static void generateHeaderForDeleteBudgetFailure(ResponseHeader responseHeader) {
		responseHeader.setHttpStatus(HttpStatus.OK);
		responseHeader.setResponseMessage(
				"Invalid Flag Value. Please provide any of these flag values (DeleteExpense/DeleteSubExpense/DeleteAllSubExpense)");
		responseHeader.setResponseCode(FAILURE);
	}

	public static void generateHeaderForDataExists(ResponseHeader responseHeader) {
		responseHeader.setHttpStatus(HttpStatus.OK);
		responseHeader.setResponseMessage(Errors.DATAEXISTS.getErrorMessage());
		responseHeader.setResponseCode(CommonConstants.FAILURE);
		responseHeader.setErrorCode(Errors.DATAEXISTS.getErrorCode());
	}

	public static void generateHeaderForNoBillPay(ResponseHeader responseHeader) {
		responseHeader.setHttpStatus(HttpStatus.OK);
		responseHeader.setResponseMessage(Errors.NOBILLPAY.getErrorMessage());
		responseHeader.setErrorCode(Errors.NOBILLPAY.getErrorCode());
		responseHeader.setResponseCode(CommonConstants.FAILURE);
	}

	public static void generateHeaderForBillerInactive(ResponseHeader responseHeader) {
		responseHeader.setHttpStatus(HttpStatus.OK);
		responseHeader.setResponseMessage(Errors.INACTIVEBILLER.getErrorMessage());
		responseHeader.setErrorCode(Errors.INACTIVEBILLER.getErrorCode());
		responseHeader.setResponseCode(CommonConstants.FAILURE);
	}

	public static void generateHeaderForFaqUpload(ResponseHeader responseHeader) {
		responseHeader.setHttpStatus(HttpStatus.OK);
		responseHeader.setResponseCode(Errors.FAQFILENAMEERROR.getErrorCode());
		responseHeader.setResponseMessage(Errors.FAQFILENAMEERROR.getErrorMessage());
	}

	public static String getUrlFromCommonUrlMap(String urlKey) {
		return urlMap.get(urlKey);
	}

	public static String getCommonProperties(String commonKey) {
		return commonProperties.get(commonKey);
	}

	public static String getExternalProperties(String commonKey) {
		return externalProperties.get(commonKey);
	}

	// POC sample method for GENERIC non-hystrix implementation
	public static String restTemplateHelper(String externalServiceRequest, String url) {
		RestTemplate restTemplate = new RestTemplate();
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		HttpEntity<String> requestBodyAndHeader = new HttpEntity<>(externalServiceRequest, headers);
		// externalServiceResponse validations not added as of now.
		return restTemplate.postForObject(url, requestBodyAndHeader, String.class);
	}

	// POC sample method for GENERIC non-hystrix implementation
	public static void initializeServiceUrls(Map<String, String> serviceUrlMap) {
		urlMap = serviceUrlMap;
	}

	public static void initializeProductTypeMap(Map<String, List<String>> productTypeMaps) {
		productTypeMap = productTypeMaps;
	}

	public static void initializeStatusCodeMap(Map<String, List<String>> statusCodeMaps) {
		statusCodeMap = statusCodeMaps;
	}

	public static void initializeCutOffTimings(Map<String, String> cutOffTimingsMap) {
		cutOffMap = cutOffTimingsMap;
	}

	public static void initializeCommonProperties(Map<String, String> commonValues) {
		commonProperties = commonValues;
	}

	public static void initializeExternalProperties(Map<String, String> commonValues) {
		externalProperties = commonValues;
	}

	public static void initializeHostToApzErrorMap(Map<String, Map<String, ErrorParameterValues>> map) {
		hostToApzErrorMap = map;
	}

	public static int typeIdConverter(String typeId) {
		int numericTypeId = 0;
		if (typeId.equalsIgnoreCase("NEFT")) {
			numericTypeId = 3;
		} else if (typeId.equalsIgnoreCase("RTGS")) {
			numericTypeId = 2;
		} else if (typeId.equalsIgnoreCase("IMPS")) {
			numericTypeId = 1;
		}
		return numericTypeId;
	}

	public static LocalDate currentDateLDCustomDate(String datee) {
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern(CommonUtils.getExternalProperties("dateFormat"));
		return LocalDate.parse(datee, formatter);
	}

	public static LocalDate currentDateLD() {
		LocalDate now = LocalDate.now();
		DateTimeFormatter dtf = DateTimeFormatter.ofPattern(CommonUtils.getExternalProperties("dateFormat"));
		dtf.format(now);
		return now;
	}

	public static boolean isSameMonthOrDayLD(LocalDate datetocompate, boolean dayFLag) {
		LocalDate currentDateLD = currentDateLD();
		String strMonthFormat = "MM";
		String strYearFormat = "yyyy";
		String strDayFormat = "dd";

		DateTimeFormatter smfLD = DateTimeFormatter.ofPattern(strMonthFormat);
		DateTimeFormatter syfLD = DateTimeFormatter.ofPattern(strYearFormat);
		DateTimeFormatter sdfLD = DateTimeFormatter.ofPattern(strDayFormat);

		String fMonth = smfLD.format(datetocompate);
		String cMonth = smfLD.format(currentDateLD);
		String fYear = syfLD.format(datetocompate);
		String cYear = syfLD.format(currentDateLD);
		String fDay = sdfLD.format(datetocompate);
		String cDay = sdfLD.format(currentDateLD);
		if (!dayFLag) {// dayflag false, check same month only
			if (fYear.equals(cYear) && fMonth.equals(cMonth)) {
				return true;
			}
		} else {
			// dayflag true, check same day also
			return fYear.equals(cYear) && fMonth.equals(cMonth) && fDay.equals(cDay);
		}
		return false;
	}

	public static boolean isNumeric(String text) {
		Pattern digitPattern = Pattern.compile("\\d+");
		return digitPattern.matcher(text).matches();
	}

	public static boolean isNullOrEmpty(String str) {
		return (str == null || "".equalsIgnoreCase(str));
	}

	public static boolean isAplhaNumeric(String text) {
		Pattern digitPattern = Pattern.compile("[a-zA-z0-9]+");
		return digitPattern.matcher(text).matches();
	}

	public static boolean isAlphabet(String text) {
		Pattern digitPattern = Pattern.compile("[a-zA-z]+");
		return digitPattern.matcher(text).matches();
	}

	public static String getStackTrace(final Exception e) {
		String lStackTrace;
		StringWriter lSw;
		PrintWriter lPw;
		lSw = new StringWriter();
		lPw = new PrintWriter(lSw);
		e.printStackTrace(lPw);
		lStackTrace = lSw.toString();
		return lStackTrace;
	}

	public static String convertDatetoCronDate(final String second, final String minute, final String hour,
			final String dayOfMonth, final String month, final String dayOfWeek, final String year) {
		return String.format("%1$s %2$s %3$s %4$s %5$s %6$s %7$s", second, minute, hour, dayOfMonth, month, dayOfWeek,
				year);
	}

	public static String quarterlyScheduleCronGenerator(final String second, final String minute, final String hour,
			final String dayOfMonth, final String month, final String dayOfWeek, final String year) {
		return String.format("%1$s %2$s %3$s %4$s %5$s/3 %6$s %7$s", second, minute, hour, dayOfMonth, month, dayOfWeek,
				year);
	}

	public static String cronGenerator(final String second, final String minute, final String hour,
			final String dayOfMonth, final String month, final String dayOfWeek, final String year) {
		if (year.length() > 2) {
			return String.format("%1$s %2$s %3$s %4$s %5$s %6$s %7$s", second, minute, hour, dayOfMonth, month,
					dayOfWeek, year);
		} else {
			return String.format("%1$s %2$s %3$s %4$s %5$s %6$s", second, minute, hour, dayOfMonth, month, dayOfWeek);
		}
	}

	/* Begin auth helper methods */

	public static String generateReferenceCode(int lengthOfCode) {
		String values = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";
		char[] refCode = new char[lengthOfCode];
		for (int i = 0; i < lengthOfCode; i++) {
			refCode[i] = values.charAt(random.nextInt(values.length()));
		}
		logger.debug("Random String generated for Auth Ref Code is -> " + Arrays.toString(refCode));
		return String.valueOf(refCode);
	}

	/* End auth helper methods */
	public static Integer generateOneTimePassword(int min, int max) {
		// same as generate random number above.
		return random.nextInt((max - min) + 1) + min;
	}

	public static long compareTwoTimeStamps(Timestamp currentTime, Timestamp oldTime) {
		logger.debug(" current time " + currentTime + " old time " + oldTime);
		long milliseconds1 = oldTime.getTime();
		long milliseconds2 = currentTime.getTime();
		long diff = milliseconds2 - milliseconds1;
		/* returning difference in minutes */
		return diff / (60 * 1000);
	}

	public static Timestamp currentTimeStamp() {
		Timestamp timestamp = null;
		long millis = System.currentTimeMillis();
		timestamp = new Timestamp(millis);
		return timestamp;
	}

	public static String getTxnRefNum(String pUserId) {
		logger.debug("Start : getTxnRefNum with pUserId = " + pUserId);
		String refNumber = null;

		if (pUserId == null || pUserId.isEmpty())
			refNumber = String.valueOf(new SecureRandom().nextInt(Integer.parseInt("999999999")));
		else
			refNumber = pUserId + String.valueOf(new SecureRandom().nextInt(Integer.parseInt("99999")));

		logger.debug("End : getTxnRefNum with resp = " + refNumber);
		return refNumber;
	}

	public static HeaderData headerData(Map<String, String> requestHeader) {
		HeaderData headerData = new HeaderData();

		for (Map.Entry<String, String> entry : requestHeader.entrySet()) {
			if (entry.getKey().equalsIgnoreCase("appId")) {
				headerData.setAppId(entry.getValue());

			} else if (entry.getKey().equalsIgnoreCase("customerId")) {
				headerData.setCustomerId(entry.getValue());
			} else if (entry.getKey().equalsIgnoreCase("ifaceName")) {
				headerData.setCustomerId(entry.getValue());
			}

		}
		return headerData;

	}

	public static void commonLogMessage(String message, String data) {
		/*
		 * While passing the error message to print , please follow the below structure
		 * "MODULE NAME >> LAYER >> FUNCTION NAME >> FOR WHAT YOU WANT TO PRINT ", VALUE
		 * TO BE PRINT
		 */
		if (logger.isDebugEnabled()) {
			logger.debug(message + data);
		} else {
			logger.info(message + data);
		}
	}

	public static String getErrorMessage(String key) {
		return getExternalProperties(key);
	}

	public static LocalDateTime timeNow() {

		return LocalDateTime.now();
	}

	public static LocalDate dateNow() {

		return LocalDate.now();
	}

	public static String txnTypeConversion(int type) {
		String[] typeArray = new String[] { "0", "1", "2", "3", "4", "5" };
		return typeArray[type];

	}

	// Only used for soapAdapter
	public static <T> T converResponseToObject(String jsonString, Class<T> type) {

		ObjectMapper mapper = new ObjectMapper();
		try {
			return mapper.readValue(jsonString, type);
		} catch (Exception e) {
			logger.error(e.getMessage());
		}
		return null;

	}

	public static Date currentDate() {
		long millis = System.currentTimeMillis();
		return new Date(millis);
	}

	public static Response setError(String errorMsg, String errorCode) {
		logger.debug("Start : setError");
		logger.debug("errorMsg = " + errorMsg);
		logger.debug("errorCode = " + errorCode);

		Response response = new Response();
		ResponseHeader externalServiceRespHeader = new ResponseHeader();
		ResponseBody externalServiceRespBody = new ResponseBody();

		externalServiceRespHeader.setResponseCode(errorCode);
		externalServiceRespHeader.setResponseMessage(errorMsg);
		externalServiceRespBody.setResponseObj("");

		response.setResponseHeader(externalServiceRespHeader);
		response.setResponseBody(externalServiceRespBody);

		logger.debug("End : setError");
		return response;
	}

	public static Response setSuccessResp(Object apiResponse) {
		logger.debug("Start : setSuccessResp");
		logger.debug("apiResponse = " + apiResponse);

		Response response = new Response();
		ResponseHeader externalServiceRespHeader = new ResponseHeader();
		ResponseBody externalServiceRespBody = new ResponseBody();

		externalServiceRespHeader.setResponseCode("0");
		externalServiceRespHeader.setResponseMessage("");
		externalServiceRespBody.setResponseObj(apiResponse.toString());

		response.setResponseHeader(externalServiceRespHeader);
		response.setResponseBody(externalServiceRespBody);

		logger.debug("End : setSuccessResp");
		return response;
	}

	public static boolean isJSONValid(String jsonInString) {
		logger.debug("Start : isJSONValid");
		boolean validJson = false;

		try {
			if ((jsonInString == null) || (jsonInString.isEmpty())) {
				validJson = false;
			} else {
				final ObjectMapper mapper = new ObjectMapper();
				mapper.readTree(jsonInString);
				validJson = true;
			}
		} catch (Exception e) {
			logger.debug("JSON error = " + e);
			validJson = false;
		}

		logger.debug("End : isJSONValid with response = " + validJson);
		return validJson;
	}

	public static Header obtainHeader(Map<String, String> requestHeader) {
		ObjectMapper mapper = new ObjectMapper();
		mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
		Header header = mapper.convertValue(requestHeader, Header.class);
		return header;
	}

	public static Header obtainHeader(String appId, String interfaceId, String userId, String masterTxnRefNo,
			String deviceId) {

		Header header = new Header();
		header.setAppId(appId);
		header.setInterfaceId(interfaceId);

		header.setUserId(userId);
		header.setMasterTxnRefNo(masterTxnRefNo);
		header.setDeviceId(deviceId);
		return header;
	}

	public static Header obtainWidgetsHeader(String appId, String interfaceId, String userId, String masterTxnRefNo,
			String deviceId, String deviceType) {

		Header header = new Header();
		header.setAppId(appId);
		header.setInterfaceId(interfaceId);

		header.setUserId(userId);
		header.setMasterTxnRefNo(masterTxnRefNo);
		header.setDeviceId(deviceId);

		header.setDeviceType(deviceType);
		return header;
	}

	public static Header obtainDeviceHeader(String appId, String interfaceId, String userId, String masterTxnRefNo,
			String deviceId, String deviceType) {
		Header header = new Header();
		header.setAppId(appId);
		header.setInterfaceId(interfaceId);
		header.setUserId(userId);
		header.setMasterTxnRefNo(masterTxnRefNo);
		header.setDeviceId(deviceId);
		header.setDeviceType(obtainDeviceType(deviceType));
		return header;
	}

	public static String obtainDeviceType(String deviceType) {

		logger.debug("Start : obtainDeviceType with request = " + deviceType);
		String modifiedDeviceType = "";

		if ((deviceType != null) && (!(deviceType.equalsIgnoreCase("")))) {
			logger.debug("Resetting the device type value");

			if ((deviceType.equalsIgnoreCase("WEB")) || (deviceType.equalsIgnoreCase("WEBCONTAINER"))) {
				modifiedDeviceType = "WEB";
			} else if ((deviceType.equalsIgnoreCase("ANDROID")) || (deviceType.equalsIgnoreCase("IOS"))) {
				modifiedDeviceType = "MOBILE";
			}
		}

		logger.debug("End : obtainDeviceType with response = " + modifiedDeviceType);
		return modifiedDeviceType;
	}

	public static String convertPojoToString(Object demoServiceRequest) throws JSONException, JsonProcessingException {
		ObjectMapper mapperObj = new ObjectMapper();
		mapperObj.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
		JSONObject restApiRequest = new JSONObject(mapperObj.writeValueAsString(demoServiceRequest));
		return restApiRequest.toString();
	}

	public static Response modifyAccountNumber(Response externalServiceResponse, String maskingFlag,
			int maskingStartPosition, int maskingCharCount, String maskingChar) throws JSONException {

		logger.debug("Start : modifyAccountNumber");
		Response maskedExternalServiceResponse = null;

		if ((maskingFlag != null) && (maskingFlag.equalsIgnoreCase("Y"))) {
			logger.debug("Masking is enabled");
			maskedExternalServiceResponse = maskAndEncryptAccountNumber(externalServiceResponse, maskingStartPosition,
					maskingCharCount, maskingChar);
		} else {
			logger.debug("Masking is disabled");
			maskedExternalServiceResponse = externalServiceResponse;
		}

		logger.debug("End : modifyAccountNumber with response = " + maskedExternalServiceResponse);
		return maskedExternalServiceResponse;
	}

	public static Response maskAndEncryptAccountNumber(Response externalServiceResponse, int maskingStartPosition,
			int maskingCharCount, String maskingChar) throws JSONException {

		logger.debug("Start : maskAndEncryptAccountNumber");

		if (CommonUtils.isJSONValid((String) externalServiceResponse.getResponseBody().getResponseObj())) {

			JSONObject externalServiceRespJson = new JSONObject(
					externalServiceResponse.getResponseBody().getResponseObj());
			logger.debug("externalServiceRespJson = " + externalServiceRespJson);

			if (externalServiceRespJson.has(getExternalProperties(ACCOUNT_NUMBER))) {
				String debitAccountNumber = externalServiceRespJson.get(getExternalProperties(ACCOUNT_NUMBER))
						.toString();
				logger.debug("debitAccountNumber = " + debitAccountNumber);

				String maskedAccountNumber = maskingLogic(debitAccountNumber, maskingStartPosition, maskingCharCount,
						maskingChar);
				logger.debug("maskedAccountNumber = " + maskedAccountNumber);

				String encryptedAccountNumber = AppzillonAESUtils.encrypt(debitAccountNumber);
				logger.debug("encryptedAccountNumber = " + encryptedAccountNumber);

				logger.debug("Replacing the plain text account number with masked and encrypted account number");
				externalServiceRespJson.remove(getExternalProperties(ACCOUNT_NUMBER));
				externalServiceRespJson.put(getExternalProperties(ACCOUNT_NUMBER), maskedAccountNumber);
				externalServiceRespJson.put(ENCRYPTED_ACCOUNT_NUMBER, encryptedAccountNumber);
				externalServiceResponse.getResponseBody().setResponseObj(externalServiceRespJson.toString());
			} else if ((externalServiceRespJson.has(getExternalProperties(ACCOUNT_NUMBER_PARENT_NODE)))
					&& (externalServiceRespJson
							.get(CommonUtils.getExternalProperties(ACCOUNT_NUMBER_PARENT_NODE)) instanceof JSONArray)) {
				JSONArray accountArray = new JSONArray(
						externalServiceRespJson.get(getExternalProperties(ACCOUNT_NUMBER_PARENT_NODE)).toString());
				logger.debug("accountArray = " + accountArray);

				for (int i = 0; i < accountArray.length(); i++) {
					JSONObject currAccountObj = accountArray.getJSONObject(i);

					if (currAccountObj.has(getExternalProperties(ACCOUNT_NUMBER))) {
						String debitAccountNumber = currAccountObj.get(getExternalProperties(ACCOUNT_NUMBER))
								.toString();
						logger.debug("debitAccountNumber = " + debitAccountNumber);

						String maskedAccountNumber = maskingLogic(debitAccountNumber, maskingStartPosition,
								maskingCharCount, maskingChar);
						logger.debug("maskedAccountNumber = " + maskedAccountNumber);

						String encryptedAccountNumber = AppzillonAESUtils.encrypt(debitAccountNumber);
						logger.debug("encryptedAccountNumber = " + encryptedAccountNumber);

						logger.debug(
								"Replacing the plain text account number with masked and encrypted account number");
						currAccountObj.remove(getExternalProperties(ACCOUNT_NUMBER));
						currAccountObj.put(getExternalProperties(ACCOUNT_NUMBER), maskedAccountNumber);
						currAccountObj.put(ENCRYPTED_ACCOUNT_NUMBER, encryptedAccountNumber);
					}
				}

				logger.debug("Replacing the complete array response");
				externalServiceRespJson.put(getExternalProperties(ACCOUNT_NUMBER_PARENT_NODE), accountArray);
				externalServiceResponse.getResponseBody().setResponseObj(externalServiceRespJson.toString());
			} else {
				logger.debug(
						"Account number field is not present in the external service response, returning the response without masking and encryption");
			}
		} else {
			logger.debug(
					"Error response received from external service, returning the response without masking and encryption");
		}

		logger.debug("End : maskAndEncryptAccountNumber");
		return externalServiceResponse;
	}

	public static String maskingLogic(String debitAccountNumber, int maskingStartPosition, int maskingCharCount,
			String maskingChar) {

		logger.debug("Start : maskingLogic");
		String maskedAccountNumber = null;

		StringBuilder maskingCharacter = new StringBuilder();
		StringBuilder charsToBeMasked = new StringBuilder();

		/*
		 * StringBuilder charsToBeMasked = new StringBuilder(
		 * debitAccountNumber.substring(maskingStartPosition, maskingStartPosition +
		 * maskingCharCount)); logger.debug("charsToBeMasked = " + charsToBeMasked);
		 * 
		 * for (int i = 0; i < charsToBeMasked.length(); i++) {
		 * maskingCharacter.append(maskingChar); }
		 * 
		 * charsToBeMasked.replace(0, charsToBeMasked.length(),
		 * maskingCharacter.toString()); logger.debug("maskingCharacters = " +
		 * charsToBeMasked);
		 * 
		 * maskedAccountNumber = debitAccountNumber.substring(0, maskingStartPosition) +
		 * charsToBeMasked + debitAccountNumber.substring(maskingStartPosition +
		 * maskingCharCount);
		 */

		if (null != debitAccountNumber && !"null".equalsIgnoreCase(debitAccountNumber)
				&& !"".equalsIgnoreCase(debitAccountNumber)) {
			int endPos = maskingStartPosition + maskingCharCount;
			if (maskingStartPosition <= endPos) {
				if (maskingStartPosition >= debitAccountNumber.length()) {
					logger.warn("masking start position is greater than debitAccountNumber ::");
					return debitAccountNumber;
				} else if ((maskingStartPosition < debitAccountNumber.length())
						&& endPos >= debitAccountNumber.length()) {
					charsToBeMasked
							.append(debitAccountNumber.substring(maskingStartPosition, debitAccountNumber.length()));
					maskingCharCount = charsToBeMasked.length();
					logger.warn("startPos is less than accNo and endPos is >= accNo");
				} else {
					charsToBeMasked.append(debitAccountNumber.substring(maskingStartPosition,
							maskingStartPosition + maskingCharCount));
					logger.warn("startPos and endPos is valid");
					logger.warn("charsToBeMasked = {}", charsToBeMasked);
				}
				for (int i = 0; i < charsToBeMasked.length(); i++) {
					maskingCharacter.append(maskingChar);
				}
				charsToBeMasked.replace(0, charsToBeMasked.length(), maskingCharacter.toString());
				logger.warn("characters to be masked = {}", charsToBeMasked);
				logger.warn("maskingCharacters count = {}", maskingCharCount);
				maskedAccountNumber = debitAccountNumber.substring(0, maskingStartPosition) + charsToBeMasked
						+ debitAccountNumber.substring(maskingStartPosition + maskingCharCount);
			} else {
				logger.warn("masking start position is greater than end position ::");
				return debitAccountNumber;
			}
		}

		logger.debug("End : maskingLogic");
		return maskedAccountNumber;
	}

	public static String maskAccountNumber(String debitAccountNumber, String maskingFlag, int maskingStartPosition,
			int maskingCharCount, String maskingChar) {

		logger.debug("Start : maskAccountNumber with request = " + debitAccountNumber);
		String maskedAccountNumber = null;

		if ((maskingFlag != null) && (maskingFlag.equalsIgnoreCase("Y"))) {
			logger.debug("Masking is enabled");
			maskedAccountNumber = maskingLogic(debitAccountNumber, maskingStartPosition, maskingCharCount, maskingChar);
			logger.debug("maskedAccountNumber = " + maskedAccountNumber);
		} else {
			logger.debug("Masking is disabled");
			maskedAccountNumber = debitAccountNumber;
		}

		logger.debug("End : maskAccountNumber with response = " + maskedAccountNumber);
		return maskedAccountNumber;
	}

	public static String encryptAccountNumber(String debitAccountNumber, String maskingFlag) {

		logger.debug("Start : encryptAccountNumber with request = " + debitAccountNumber);
		String encryptedAccountNumber = AppzillonAESUtils.encrypt(debitAccountNumber);
		logger.debug("End : encryptAccountNumber with response = " + encryptedAccountNumber);
		return encryptedAccountNumber;
	}

	public static String decryptAccountNumber(String debitAccountNumber, String maskingFlag) {

		logger.debug("Start : decryptAccountNumber with request = " + debitAccountNumber);
		String decryptedAccountNumber = null;

		decryptedAccountNumber = AppzillonAESUtils.decrypt(debitAccountNumber);
		/*
		 * if((maskingFlag!=null) && (maskingFlag.equalsIgnoreCase("Y"))) {
		 * logger.debug("Masking is enabled");
		 *
		 * }
		 *
		 * else { logger.debug("Masking is disabled"); decryptedAccountNumber =
		 * debitAccountNumber; }
		 */

		logger.debug("End : decryptAccountNumber with response = " + decryptedAccountNumber);
		return decryptedAccountNumber;
	}

	public static Timestamp convertStringToDate(String dateStr, String conversionFormat) {
		Timestamp timestamp = null;
		java.util.Date date;
		try {
			SimpleDateFormat sdf = new SimpleDateFormat(conversionFormat);
			date = sdf.parse(dateStr);
			timestamp = new Timestamp(date.getTime());
		} catch (ParseException e) {
			logger.error("Date Parse Exception:" + e.getMessage());
		}
		return timestamp;
	}

	public static String decryptCommonUTF8(String pass) {

		String psk = "vhja6T8DYVHF3kaD";
		String iv = "vhja6T8DYVHF3kaD";
		String resultStr = "";
		try {
			byte[] chiperText = DatatypeConverter.parseHexBinary(pass);
			String encryptionKey = psk;
			final Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
			final SecretKeySpec key = new SecretKeySpec(encryptionKey.getBytes(UTF8), "AES");
			cipher.init(Cipher.ENCRYPT_MODE, key, new IvParameterSpec(iv.getBytes(UTF8)));
			byte[] chip = cipher.doFinal(chiperText);
			resultStr = new String(chip, UTF8);
		} catch (Exception ex) {
			logger.error("Decription failed ::: " + ex.getMessage());
		}
		return resultStr;
	}

	public static boolean checkStringNullOrEmpty(String searchStr) {
		return null == searchStr || "".equalsIgnoreCase(searchStr) || "null".equalsIgnoreCase(searchStr);
	}

	public static String addCommasToNumericString(String digits) {
		logger.debug("Start : addCommasToNumericString with request = " + digits);
		StringBuilder result = new StringBuilder();

		for (int i = 1; i <= digits.length(); ++i) {
			char ch = digits.charAt(digits.length() - i);
			if (i % 3 == 1 && i > 1) {
				result.insert(0, ",");
			}
			result.insert(0, ch);
		}

		logger.debug("End : addCommasToNumericString with response = " + result);
		return result.toString();
	}

	public static String formatLakh(double d, String locale) {
		String s = String.format(Locale.UK, locale, Math.abs(d));
		s = s.replaceAll("(.+)(...\\...)", "$1,$2");
		while (s.matches("\\d{3,},.+")) {
			s = s.replaceAll("(\\d+)(\\d{2},.+)", "$1,$2");
		}
		return d < 0 ? ("-" + s) : s;
	}

	public String decryptedNumber(String number) {

		String decryptedNumber = AppzillonAESUtils.decrypt(number);
		if (!checkStringNullOrEmpty(decryptedNumber)) {
			return decryptedNumber;
		} else {
			return number;
		}
	}

	public static long getCRC32Checksum(byte[] bytes) {
		Checksum crc32 = new CRC32();
		crc32.update(bytes, 0, bytes.length);
		return crc32.getValue();
	}
	
	public static String formatDateTimeWithMicroseconds(LocalDateTime dateTime, String amPm) {
		int microseconds = dateTime.getNano() / 1000; // Convert nanoseconds to microseconds

		return String.format("%02d-%02d-%02d %02d:%02d:%02d.%09d %s", dateTime.getDayOfMonth(),
				dateTime.getMonthValue(), dateTime.getYear() % 100, dateTime.getHour(), dateTime.getMinute(),
				dateTime.getSecond(), microseconds, amPm);
	}
	
	public static String generateRandomNumStr() {
		return System.currentTimeMillis() + "" + random.nextInt(9999);
	}

	public static BigDecimal generateRandomNum() {
		return new BigDecimal(
				(System.currentTimeMillis() + "" + (new SecureRandom().nextInt(9999) + 1000)).substring(5, 17));
	}

	public static String generateApplicationId(String loanType, String branchId,String userId) {
		String formattedBranchId = branchId.substring(branchId.length() - 4);
		String digitsOnly = userId.replaceAll("\\D", "");
		String lastDigits = digitsOnly.length() <= 6 ? digitsOnly : digitsOnly.substring(digitsOnly.length() - 6);
		String formattedUserId = String.format("%05d", Integer.parseInt(lastDigits));
		LocalDateTime now = LocalDateTime.now();
		String year = String.format("%02d", now.getYear() % 100);
		String month = String.format("%02d", now.getMonthValue());
		String day = String.format("%02d", now.getDayOfMonth());
		String hour = String.format("%02d", now.getHour());
		String minute = String.format("%02d", now.getMinute());
		String second = String.format("%02d", now.getSecond());
		String millisecond = String.format("%03d", now.getNano() / 1_000_000);
		return loanType + formattedBranchId +formattedUserId+ year + month + day + hour + minute + second + millisecond;
	}

}
