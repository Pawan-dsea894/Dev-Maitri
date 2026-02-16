package com.iexceed.appzillonbanking.core.config;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.web.client.ResponseErrorHandler;

import java.io.IOException;

public class CustomClientErrorHandler implements ResponseErrorHandler {

	private static final Logger logger = LogManager.getLogger(CustomClientErrorHandler.class);
	
	@Override
	public boolean hasError(ClientHttpResponse clientHttpResponse) throws IOException {
		return clientHttpResponse.getStatusCode().is4xxClientError();
	}
	
	@Override
	public void handleError(ClientHttpResponse clientHttpResponse) throws IOException {
		logger.error("HTTP Status Code: " + clientHttpResponse.getStatusCode().value());
	}
}
