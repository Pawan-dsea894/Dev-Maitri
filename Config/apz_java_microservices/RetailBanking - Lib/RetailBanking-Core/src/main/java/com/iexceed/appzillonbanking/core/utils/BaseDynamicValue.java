package com.iexceed.appzillonbanking.core.utils;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Component;

import java.security.SecureRandom;

@Component
public class BaseDynamicValue {

	private static final Logger logger = LogManager.getLogger(BaseDynamicValue.class);

	public String generateValue(String defaultValue) {
		logger.debug("Start : generateValue with defaultValue = " + defaultValue);
		String randomValue = null;

		try {
			if (defaultValue.contains("generateRandomSequence")) {
				randomValue = generateRandomSequence(defaultValue);
			}

			/*
			 * else if (defaultValue.contains("generateSessionToken")) { randomValue =
			 * generateSessionToken(); }
			 */
		}

		catch (Exception e) {
			logger.error("Error occurred while generating the random value, error msg = " + e);
			randomValue = null;
		}

		logger.debug("End : generateValue with randomValue = " + randomValue);
		return randomValue;
	}

	public String generateRandomSequence(String defaultValue) {
		String randomValue = null;
		int startIndex = StringUtils.ordinalIndexOf(defaultValue, "~", 2);
		int endIndex = StringUtils.ordinalIndexOf(defaultValue, "~", 3);

		if (startIndex == -1 || endIndex == -1) {
			logger.debug("Please configure the value of default value properly");
			return null;
		}

		String randomType = defaultValue.substring(startIndex + 1, endIndex);
		logger.debug("randomType = " + randomType);

		if (randomType == null || randomType.trim().equals("")) {
			logger.debug("Please configure the value of default value properly");
			return null;
		}

		if (randomType.equalsIgnoreCase("NUM")) {
			randomValue = generateNumberSequence(defaultValue);
		}

		else if (randomType.equalsIgnoreCase("ALPHANUM")) {
			randomValue = generateAlphaNumericSequence(defaultValue);
		}

		return randomValue;
	}

	public String generateNumberSequence(String defaultValue) {
		StringBuilder sb = new StringBuilder();

		int startIndex = StringUtils.ordinalIndexOf(defaultValue, "~", 3);
		int endIndex = StringUtils.ordinalIndexOf(defaultValue, "~", 4);

		if (startIndex == -1 || endIndex == -1) {
			logger.debug("Please configure the value of default value properly");
			return null;
		}

		int length = Integer.parseInt(defaultValue.substring(startIndex + 1, endIndex));
		logger.debug("length = " + length);

		for (int i = 0; i < length; i++) {
			int random = new SecureRandom().nextInt(Integer.parseInt("9"));
			sb.append(random);
		}

		return sb.toString();
	}

	public String generateAlphaNumericSequence(String defaultValue) {
		StringBuilder sb = new StringBuilder();
		String AlphaNumericString = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvxyz";

		int startIndex = StringUtils.ordinalIndexOf(defaultValue, "~", 3);
		int midIndex = StringUtils.ordinalIndexOf(defaultValue, "~", 4);
		int endIndex = StringUtils.ordinalIndexOf(defaultValue, "~", 5);

		if (startIndex == -1 || midIndex == -1 || endIndex == -1) {
			logger.debug("Please configure the value of default value properly");
			return null;
		}

		int alphabetLength = Integer.parseInt(defaultValue.substring(startIndex + 1, midIndex));
		logger.debug("alphabetLength = " + alphabetLength);

		int numberLength = Integer.parseInt(defaultValue.substring(midIndex + 1, endIndex));
		logger.debug("numberLength = " + numberLength);
		for (int i = 0; i < alphabetLength; i++) {
			int index = (AlphaNumericString.length() * new SecureRandom().nextInt());
			sb.append(AlphaNumericString.charAt(index));
		}

		for (int i = 0; i < numberLength; i++) {
			int random = new SecureRandom().nextInt(Integer.parseInt("9"));
			sb.append(random);
		}

		return sb.toString();
	}

	/*
	 * public String generateSessionToken() {
	 * logger.debug("Start : obtainSessionToken"); String sessionToken = null;
	 * 
	 * AdapterUtil adapterUtil = new AdapterUtil(); RestService restService = new
	 * RestService();
	 * 
	 * try { String sessionTokenApiUrl =
	 * adapterUtil.getPropertyValueFromServer("SessionTokenApiUrl");
	 * logger.debug("sessionTokenApiUrl = " + sessionTokenApiUrl);
	 * 
	 * String username =
	 * adapterUtil.getPropertyValueFromServer("SessionTokenUsername");
	 * logger.debug("username = " + username);
	 * 
	 * String password =
	 * adapterUtil.getPropertyValueFromServer("SessionTokenPassword");
	 * logger.debug("password = " + password);
	 * 
	 * if ((sessionTokenApiUrl == null) || (sessionTokenApiUrl.trim().equals("")) ||
	 * (username == null) || (username.trim().equals("")) || (password == null) ||
	 * (password.trim().equals(""))) { logger.
	 * debug("Unable to fetch the request parameters, hence unable to obtain the session token"
	 * ); return sessionToken; }
	 * 
	 * JSONArray headerParams = new JSONArray(); //Add any header parameters if
	 * required
	 * 
	 * //Add your request body parameters here which will be used to generate the
	 * session token JSONObject sessionTokenRequest = new JSONObject();
	 * sessionTokenRequest.put("USERNAME", username);
	 * sessionTokenRequest.put("PASSWORD", password);
	 * 
	 * String sessionTokenResp =
	 * restService.executeRestApi(sessionTokenRequest.toString(), headerParams,
	 * "POST", "application/json", sessionTokenApiUrl);
	 * logger.debug("sessionTokenResp = " + sessionTokenResp);
	 * 
	 * if ((sessionTokenResp == null) || (sessionTokenResp.trim().equals(""))) {
	 * logger.debug("Null response received from session token API"); return
	 * sessionToken; }
	 * 
	 * JSONObject sessionTokenRespJson = new JSONObject(sessionTokenResp); boolean
	 * errorFlag = false;
	 * 
	 * if (sessionTokenRespJson.has("ERRORCODE")) { String errorCode =
	 * sessionTokenRespJson.get("ERRORCODE").toString(); logger.debug("errorCode = "
	 * + errorCode);
	 * 
	 * if (errorCode.equals("0")) {
	 * logger.debug("Rest api execution is successful"); sessionToken =
	 * sessionTokenRespJson.getJSONObject("RESULT").get("SESSIONID").toString(); }
	 * 
	 * else { logger.debug("Setting error flag as true since error code is not 0");
	 * errorFlag = true; } }
	 * 
	 * else { logger.
	 * debug("Setting error flag as true since error code object is not present");
	 * errorFlag = true; }
	 * 
	 * if (errorFlag) {
	 * logger.debug("Setting the failure response for session token API"); return
	 * sessionToken; } }
	 * 
	 * catch (Exception e) { logger.
	 * debug("Error occured while obtaining the seesion token, error message = " +
	 * e); sessionToken = null; }
	 * 
	 * logger.debug("End : obtainSessionToken with response = " + sessionToken);
	 * return sessionToken; }
	 */
}