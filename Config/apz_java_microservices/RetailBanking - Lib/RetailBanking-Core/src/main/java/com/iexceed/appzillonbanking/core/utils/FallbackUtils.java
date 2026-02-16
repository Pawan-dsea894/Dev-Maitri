package com.iexceed.appzillonbanking.core.utils;

import com.iexceed.appzillonbanking.core.constants.CommonConstants;
import com.iexceed.appzillonbanking.core.exception.CustomException;
import com.iexceed.appzillonbanking.core.payload.Response;
import com.iexceed.appzillonbanking.core.payload.ResponseBody;
import com.iexceed.appzillonbanking.core.payload.ResponseHeader;

import reactor.core.publisher.Mono;

public class FallbackUtils {

	public static Response dbFallback() {
		ResponseHeader responseHeader = new ResponseHeader();
		CommonUtils.generateHeaderForDBError(responseHeader);
		return getFallbackResponse(responseHeader);
	}

	public static Mono<Response> dbFallbackMono() {
		ResponseHeader responseHeader = new ResponseHeader();
		CommonUtils.generateHeaderForDBError(responseHeader);
		return getFallbackResponseMono(responseHeader);
	}

	public static Mono<Response> genericFallbackMono() {
		ResponseHeader responseHeader = new ResponseHeader();
		CommonUtils.generateHeaderForGenericError(responseHeader);
		return getFallbackResponseMono(responseHeader);
	}

	public static Mono<Object> genericFallbackMonoObject() {
		ResponseHeader responseHeader = new ResponseHeader();
		CommonUtils.generateHeaderForGenericError(responseHeader);
		return getFallbackResponseMonoObject(responseHeader);
	}

	/**
	 * Method to generate Response based on the Custom Exception Handling and
	 * returning the response in responseObj
	 * 
	 * @author akshay.upadhya
	 * @since 08.03.2022
	 * @param ex
	 * @return
	 */
	public static Mono<Response> genericCustomExceptionFallbackMono(CustomException ex) {
		ResponseHeader responseHeader = new ResponseHeader();
		ResponseBody responseBody = new ResponseBody();
		Response fallbackCustomExceptionResponse = new Response();

		responseHeader.setErrorMessage("");
		responseHeader.setResponseCode(CommonConstants.FAILURE);
		responseHeader.setResponseMessage("");

		responseBody.setResponseObj(ex.getMessage());

		fallbackCustomExceptionResponse.setResponseHeader(responseHeader);
		fallbackCustomExceptionResponse.setResponseBody(responseBody);

		return Mono.just(fallbackCustomExceptionResponse);
	}

	public static Response genericFallback() {
		ResponseHeader responseHeader = new ResponseHeader();
		CommonUtils.generateHeaderForGenericError(responseHeader);
		return getFallbackResponse(responseHeader);
	}

	private static Mono<Response> getFallbackResponseMono(ResponseHeader responseHeader) {
		ResponseBody responseBody = new ResponseBody();
		responseBody.setResponseObj("");

		Response fallbackResponse = new Response();
		fallbackResponse.setResponseHeader(responseHeader);
		fallbackResponse.setResponseBody(responseBody);
		return Mono.just(fallbackResponse);
	}

	private static Mono<Object> getFallbackResponseMonoObject(ResponseHeader responseHeader) {
		ResponseBody responseBody = new ResponseBody();
		responseBody.setResponseObj("");

		Response fallbackResponse = new Response();
		fallbackResponse.setResponseHeader(responseHeader);
		fallbackResponse.setResponseBody(responseBody);
		return Mono.just(fallbackResponse);
	}

	private static Response getFallbackResponse(ResponseHeader responseHeader) {
		ResponseBody responseBody = new ResponseBody();
		responseBody.setResponseObj("");

		Response fallbackResponse = new Response();
		fallbackResponse.setResponseHeader(responseHeader);
		fallbackResponse.setResponseBody(responseBody);
		return fallbackResponse;
	}
}
