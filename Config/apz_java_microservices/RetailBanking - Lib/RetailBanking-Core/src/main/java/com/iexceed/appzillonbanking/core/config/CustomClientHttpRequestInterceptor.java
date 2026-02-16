package com.iexceed.appzillonbanking.core.config;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;

import java.io.IOException;

public class CustomClientHttpRequestInterceptor implements ClientHttpRequestInterceptor {

	private static final Logger logger = LogManager.getLogger(CustomClientHttpRequestInterceptor.class);
	
	@Override
	public ClientHttpResponse intercept(HttpRequest request, byte[] bytes, ClientHttpRequestExecution execution) throws IOException {
		// log the http request
		logger.info("URI: {}" +request.getURI());
		logger.info("HTTP Method: {}" +request.getMethod());
		logger.info("HTTP Headers: {}" +request.getHeaders());

		return execution.execute(request, bytes);
	}
}
