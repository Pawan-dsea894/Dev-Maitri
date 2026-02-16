package com.iexceed.appzillonbanking.kendra.service;

import java.util.Base64;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.iexceed.appzillonbanking.core.payload.Header;
import com.iexceed.appzillonbanking.core.payload.Response;
import com.iexceed.appzillonbanking.core.payload.ResponseBody;
import com.iexceed.appzillonbanking.core.payload.ResponseHeader;
import com.iexceed.appzillonbanking.core.payload.ResponseWrapper;
import com.iexceed.appzillonbanking.core.utils.CommonUtils;
import com.iexceed.appzillonbanking.interfaceAdapter.service.InterfaceAdapter;
import com.iexceed.appzillonbanking.interfaceAdapter.utils.AdapterUtil;
import com.iexceed.appzillonbanking.kendra.payload.KendraDetailsUpdateRequest;
import com.iexceed.appzillonbanking.kendra.payload.MeetingDateUpdateRequest;
import com.iexceed.appzillonbanking.kendra.payload.MeetingDayUpdateRequest;
import com.iexceed.appzillonbanking.kendra.payload.MeetingFrequencyUpdateRequest;
import com.iexceed.appzillonbanking.kendra.payload.MeetingTimeUpdateRequest;
import com.iexceed.appzillonbanking.kendra.payload.MultiKendraDetailsUpdateRequest;
import com.iexceed.appzillonbanking.kendra.payload.UpdateKendraAddressDetailsRequest;

import jakarta.transaction.Transactional;
import reactor.core.publisher.Mono;

@Service
public class KendraService {

	private static final Logger logger = LogManager.getLogger(KendraService.class);

	@Autowired
	private InterfaceAdapter interfaceAdapter;

	@Autowired
	private AdapterUtil adapterUtil;

	public static final String EXCEPTION_MSG = "Something went wrong, Please try again!!";
	public static final String EXCEPTION_OCCURED = "Exception occurred";

	public static final String YES = "yes";
	public static final String MEETING_DATE_UPADTE_INTERFACE_ID = "meetingDateUpdate";
	public static final String MEETING_DAY_UPADTE_INTERFACE_ID = "meetingDayUpdate";
	public static final String MEETING_FREQUENCY_UPADTE_INTERFACE_ID = "meetingFrequencyUpdate";
	public static final String MEETING_TIME_UPADTE_INTERFACE_ID = "meetingTimeUpdate";

	public static final String DENTRA_DETAILS_UPADTE_INTERFACE_ID = "kendraDetailsUpdate";
	public static final String KENDRA_MULTI_DETAILS_UPADTE_INTERFACE_ID = "MultikendraDetailsUpdate";
	public static final String KENDRA_ADDRESS_UPDATE="KendraAddressUpdate";

	public Mono<ResponseWrapper> meetingDateUpdate(MeetingDateUpdateRequest apiRequest, Header header) {

		try {

			header.setInterfaceId(MEETING_DATE_UPADTE_INTERFACE_ID);
			Mono<Object> dedupeResponse = interfaceAdapter.callExternalService(header, apiRequest.getRequestObj(),
					MEETING_DATE_UPADTE_INTERFACE_ID, true);
			Mono<ResponseWrapper> monoResWrapper = adapterUtil.generateRespWrapper(dedupeResponse,
					MEETING_DATE_UPADTE_INTERFACE_ID, header, true);
			return monoResWrapper;

		} catch (Exception e) {
			logger.error("exception at meetingDateUpdate: ", e);
			Response response = new Response();
			ResponseHeader respHeader = new ResponseHeader();
			ResponseBody respBody = new ResponseBody();
			respBody.setResponseObj(EXCEPTION_MSG);
			CommonUtils.generateHeaderForFailure(respHeader, EXCEPTION_OCCURED);
			response.setResponseBody(respBody);
			response.setResponseHeader(respHeader);
			ResponseWrapper resWrapper = new ResponseWrapper();
			resWrapper.setApiResponse(response);
			return Mono.just(resWrapper);
		}
	}

	public Mono<ResponseWrapper> meetingDayUpdate(MeetingDayUpdateRequest apiRequest, Header header) {

		try {

			header.setInterfaceId(MEETING_DAY_UPADTE_INTERFACE_ID);
			logger.warn("Printing Final req:{}",apiRequest);
			Mono<Object> dedupeResponse = interfaceAdapter.callExternalService(header, apiRequest.getRequestObj(),
					MEETING_DAY_UPADTE_INTERFACE_ID, true);
			Mono<ResponseWrapper> monoResWrapper = adapterUtil.generateRespWrapper(dedupeResponse,
					MEETING_DAY_UPADTE_INTERFACE_ID, header, true);
			return monoResWrapper;

		} catch (Exception e) {
			logger.error("exception at meetingDayUpdate: ", e);
			Response response = new Response();
			ResponseHeader respHeader = new ResponseHeader();
			ResponseBody respBody = new ResponseBody();
			respBody.setResponseObj(EXCEPTION_MSG);
			CommonUtils.generateHeaderForFailure(respHeader, EXCEPTION_OCCURED);
			response.setResponseBody(respBody);
			response.setResponseHeader(respHeader);
			ResponseWrapper resWrapper = new ResponseWrapper();
			resWrapper.setApiResponse(response);
			return Mono.just(resWrapper);
		}
	}

	public Mono<ResponseWrapper> meetingFrequencyUpdate(MeetingFrequencyUpdateRequest apiRequest, Header header) {

		try {

			header.setInterfaceId(MEETING_FREQUENCY_UPADTE_INTERFACE_ID);
			logger.warn("Printing final response :{}",apiRequest);
			Mono<Object> dedupeResponse = interfaceAdapter.callExternalService(header, apiRequest.getRequestObj(),
					MEETING_FREQUENCY_UPADTE_INTERFACE_ID, true);
	        dedupeResponse.doOnNext(response -> logger.debug("Received response from external service: {}", response))
            .subscribe();
			Mono<ResponseWrapper> monoResWrapper = adapterUtil.generateRespWrapper(dedupeResponse,
					MEETING_FREQUENCY_UPADTE_INTERFACE_ID, header, true);
			return monoResWrapper.flatMap(responseMono -> {
				 logger.debug("Received Response Wrapper: {}", responseMono);
				 logger.warn("Received Response Wrapper: {}", responseMono);
				return Mono.just(responseMono);
			});

		} catch (Exception e) {
			logger.error("exception at meetingFrequencyUpdate: ", e);
			Response response = new Response();
			ResponseHeader respHeader = new ResponseHeader();
			ResponseBody respBody = new ResponseBody();
			respBody.setResponseObj(EXCEPTION_MSG);
			CommonUtils.generateHeaderForFailure(respHeader, EXCEPTION_OCCURED);
			response.setResponseBody(respBody);
			response.setResponseHeader(respHeader);
			ResponseWrapper resWrapper = new ResponseWrapper();
			resWrapper.setApiResponse(response);
			return Mono.just(resWrapper);
		}
	}
	
	public Mono<ResponseWrapper> kendraDetailsUpdate(KendraDetailsUpdateRequest apiRequest, Header header) {

		try {

			header.setInterfaceId(DENTRA_DETAILS_UPADTE_INTERFACE_ID);
			Mono<Object> dedupeResponse = interfaceAdapter.callExternalService(header, apiRequest.getRequestObj(),
					DENTRA_DETAILS_UPADTE_INTERFACE_ID, true);
			Mono<ResponseWrapper> monoResWrapper = adapterUtil.generateRespWrapper(dedupeResponse,
					DENTRA_DETAILS_UPADTE_INTERFACE_ID, header, true);
			return monoResWrapper;

		} catch (Exception e) {
			logger.error("exception at kendraDetailsUpdate: ", e);
			Response response = new Response();
			ResponseHeader respHeader = new ResponseHeader();
			ResponseBody respBody = new ResponseBody();
			respBody.setResponseObj(EXCEPTION_MSG);
			CommonUtils.generateHeaderForFailure(respHeader, EXCEPTION_OCCURED);
			response.setResponseBody(respBody);
			response.setResponseHeader(respHeader);
			ResponseWrapper resWrapper = new ResponseWrapper();
			resWrapper.setApiResponse(response);
			return Mono.just(resWrapper);
		}
	}

	public static String generateBasicAuthHeader(String username, String password) {
		String credentials = username + ":" + password;
		String encodedCredentials = Base64.getEncoder().encodeToString(credentials.getBytes());
		return "Basic " + encodedCredentials;
	}

	public Mono<ResponseWrapper> meetingTimeUpdate(MeetingTimeUpdateRequest apiRequest, Header header) {
		try {
			header.setInterfaceId(MEETING_TIME_UPADTE_INTERFACE_ID);
			logger.warn("Printing final response :{}",apiRequest);
			Mono<Object> dedupeResponse = interfaceAdapter.callExternalService(header, apiRequest.getRequestObj(),
					MEETING_TIME_UPADTE_INTERFACE_ID, true);
			Mono<ResponseWrapper> monoResWrapper = adapterUtil.generateRespWrapper(dedupeResponse,
					MEETING_TIME_UPADTE_INTERFACE_ID, header, true);
			return monoResWrapper.flatMap(responseMono -> {
				 logger.debug("Received Response Wrapper: {}", responseMono);
				return Mono.just(responseMono);
			});

		} catch (Exception e) {
			logger.error("exception at meetingTimeUpdate: ", e);
			Response response = new Response();
			ResponseHeader respHeader = new ResponseHeader();
			ResponseBody respBody = new ResponseBody();
			respBody.setResponseObj(EXCEPTION_MSG);
			CommonUtils.generateHeaderForFailure(respHeader, EXCEPTION_OCCURED);
			response.setResponseBody(respBody);
			response.setResponseHeader(respHeader);
			ResponseWrapper resWrapper = new ResponseWrapper();
			resWrapper.setApiResponse(response);
			return Mono.just(resWrapper);
		}
	}

	public Mono<ResponseWrapper> multikendraDetailsUpdate(MultiKendraDetailsUpdateRequest apiRequest, Header header) {
		try {
			header.setInterfaceId(KENDRA_MULTI_DETAILS_UPADTE_INTERFACE_ID);
			Mono<Object> dedupeResponse = interfaceAdapter.callExternalService(header, apiRequest.getRequestObj(),
					KENDRA_MULTI_DETAILS_UPADTE_INTERFACE_ID, true);
			Mono<ResponseWrapper> monoResWrapper = adapterUtil.generateRespWrapper(dedupeResponse,
					KENDRA_MULTI_DETAILS_UPADTE_INTERFACE_ID, header, true);
			return monoResWrapper.flatMap(responseMono -> {
				 logger.debug("Received Response Wrapper from multikendraDetailsUpdate: {}", responseMono);
				return Mono.just(responseMono);
			});

		} catch (Exception e) {
			logger.error("exception at multikendraDetailsUpdate: ", e);
			Response response = new Response();
			ResponseHeader respHeader = new ResponseHeader();
			ResponseBody respBody = new ResponseBody();
			respBody.setResponseObj(EXCEPTION_MSG);
			CommonUtils.generateHeaderForFailure(respHeader, EXCEPTION_OCCURED);
			response.setResponseBody(respBody);
			response.setResponseHeader(respHeader);
			ResponseWrapper resWrapper = new ResponseWrapper();
			resWrapper.setApiResponse(response);
			return Mono.just(resWrapper);
		}
	}
	
	
	@Transactional
	public Mono<ResponseWrapper> updateKendraAddressDetails(UpdateKendraAddressDetailsRequest apiRequest, Header header) {
		try {
			apiRequest.getRequestObj().setBranchId(apiRequest.getRequestObj().getBranch());
			header.setInterfaceId(KENDRA_ADDRESS_UPDATE);
			Mono<Object> dedupeResponse = interfaceAdapter.callExternalService(header, apiRequest,
					KENDRA_ADDRESS_UPDATE, true);
			Mono<ResponseWrapper> monoResWrapper = adapterUtil.generateRespWrapper(dedupeResponse,
					KENDRA_ADDRESS_UPDATE, header, true);
			return monoResWrapper.flatMap(responseMono -> {
				 logger.debug("Received Response Wrapper from updateKendraAddressDetails: {}", responseMono);
				return Mono.just(responseMono);
			});

		} catch (Exception e) {
			logger.error("exception at UpdateKendraDetails: ", e);
			Response response = new Response();
			ResponseHeader respHeader = new ResponseHeader();
			ResponseBody respBody = new ResponseBody();
			respBody.setResponseObj(EXCEPTION_MSG);
			CommonUtils.generateHeaderForFailure(respHeader, EXCEPTION_OCCURED);
			response.setResponseBody(respBody);
			response.setResponseHeader(respHeader);
			ResponseWrapper resWrapper = new ResponseWrapper();
			resWrapper.setApiResponse(response);
			return Mono.just(resWrapper);
		}
	}
}