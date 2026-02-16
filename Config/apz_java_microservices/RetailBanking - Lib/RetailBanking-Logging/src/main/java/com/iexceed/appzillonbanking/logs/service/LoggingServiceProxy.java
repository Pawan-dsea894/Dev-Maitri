package com.iexceed.appzillonbanking.logs.service;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.iexceed.appzillonbanking.logs.payload.LogData;

@Component
public class LoggingServiceProxy {

	private static final Logger logger = LogManager.getLogger(LoggingServiceProxy.class);

	@Autowired
	private LoggingService loggingService;

	public void logTransactionDetails(LogData logData, JSONObject interfaceJsonContent, Boolean isJSONAdapterCall) {
		try {
			loggingService.logTransactionDetails(logData, interfaceJsonContent, isJSONAdapterCall);
		} catch (Exception e) {
			logger.error("logTransactionDetails ERROR = " , e);
		}
	}
}
