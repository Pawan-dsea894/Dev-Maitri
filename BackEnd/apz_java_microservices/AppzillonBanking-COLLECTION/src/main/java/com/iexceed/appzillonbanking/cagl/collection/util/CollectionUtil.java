package com.iexceed.appzillonbanking.cagl.collection.util;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import org.json.JSONObject;

import lombok.experimental.UtilityClass;

@UtilityClass
public class CollectionUtil {

	public static final String DT_TIME_FORMAT = "yyyy-MM-dd HH:mm:ss";
	
	public static final String DT_TIME_FORMAT_TZ = "yyyy-MM-dd'T'HH:mm:ss";

	public static final String DT_FORMAT = "yyyyMMdd";

	/*
	 * Method to generate unique CustDtlId
	 * 
	 */
	public static String generateCustDtlId(String id, String kendraId, String meetingDt, String applnType) {
		StringBuilder sb = new StringBuilder();
		String txnDt = meetingDt.replaceAll("-", "");
		String applnId = "S-";
		if (applnType.startsWith("R")) {
			applnId = "R-";
		} else if (applnType.startsWith("N")) {
			applnId = "N-";
		}
		return sb.append(applnId).append(id).append("-").append(kendraId).append("-").append(txnDt).toString();
	}

	public static String generateDeathIntimationApplnId(String custId, String kendraId) {
		StringBuilder sb = new StringBuilder();
		String txnDt = LocalDate.now().format(DateTimeFormatter.ofPattern(DT_FORMAT));
		return sb.append("D-").append(kendraId).append("-").append(custId).append("-").append(txnDt).toString();
	}

	public static String getStringValue(JSONObject json, String key) {
		return json.has(key) ? json.getString(key) : null;
	}

	public static Integer getIntValue(JSONObject json, String key) {
		return json.has(key) ? json.getInt(key) : null;
	}
}
