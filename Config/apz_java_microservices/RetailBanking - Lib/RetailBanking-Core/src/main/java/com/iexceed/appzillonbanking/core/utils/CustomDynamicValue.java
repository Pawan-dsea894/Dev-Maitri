package com.iexceed.appzillonbanking.core.utils;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Component;

@Component
public class CustomDynamicValue {

	private static final Logger logger = LogManager.getLogger(CustomDynamicValue.class);

	public String generateValue(String defaultValue) {
		logger.debug("Start : generateValue with defaultValue = " + defaultValue);
		String randomValue = null;
		//Write your project specific logic here
		if ("generateTrnTimestamp".equalsIgnoreCase(defaultValue)) {
			LocalDateTime ldDateTime = LocalDateTime.now();
			DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd/mm/yyyy HH:MM:SS");
			randomValue = ldDateTime.format(dtf);
		}
		logger.debug("CustomDynamicValue:generateValue::{}", randomValue);
		return randomValue;
	}
}