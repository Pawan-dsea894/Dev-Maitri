package com.iexceed.appzillonbanking.kendra.rest;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.iexceed.appzillonbanking.core.payload.Header;
import com.iexceed.appzillonbanking.core.payload.Response;
import com.iexceed.appzillonbanking.core.payload.ResponseBody;
import com.iexceed.appzillonbanking.core.payload.ResponseHeader;
import com.iexceed.appzillonbanking.core.payload.ResponseWrapper;
import com.iexceed.appzillonbanking.core.utils.CommonUtils;
import com.iexceed.appzillonbanking.kendra.payload.KendraDetailsUpdateRequestWrapper;
import com.iexceed.appzillonbanking.kendra.payload.MeetingDateUpdateRequestWrapper;
import com.iexceed.appzillonbanking.kendra.payload.MeetingDayUpdateRequestWrapper;
import com.iexceed.appzillonbanking.kendra.payload.MeetingFrequencyUpdateRequestWrapper;
import com.iexceed.appzillonbanking.kendra.payload.MeetingTimeUpdateRequestWrapper;
import com.iexceed.appzillonbanking.kendra.payload.MultiKendraDetailsUpdateRequestWrapper;
import com.iexceed.appzillonbanking.kendra.payload.UpdateKendraAddressDetailsRequestWrapper;
import com.iexceed.appzillonbanking.kendra.service.KendraService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import reactor.core.publisher.Mono;

@RestController
@Tag(description = "application/kendra", name = "KENDRA")
@RequestMapping("application/kendra/")
public class KendraServiceController {

	private static final Logger logger = LogManager.getLogger(KendraServiceController.class);

	@Autowired
	private KendraService kendraService;

	@ApiResponses({ @ApiResponse(responseCode = "200", description = "AppzillonBanking API reachable"),
			@ApiResponse(responseCode = "408", description = "Service Timed Out"),
			@ApiResponse(responseCode = "500", description = "Internal Server Error"),
			@ApiResponse(responseCode = "404", description = "AppzillonBanking not reachable") })
	@Operation(summary = "Meeting Date Update", description = "API to Update Next Meeting Date ")
	@PostMapping(value = "meetingDateUpdate", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	public Mono<ResponseEntity<ResponseWrapper>> meetingDateUpdate(
			@RequestBody MeetingDateUpdateRequestWrapper requestWrapper, @RequestHeader String appId,
			@RequestHeader String interfaceId, @RequestHeader String userId, @RequestHeader String masterTxnRefNo,
			@RequestHeader String deviceId) {

		logger.debug("meetingDateUpdate request data :: {}", requestWrapper);
		Header header = CommonUtils.obtainHeader(appId, interfaceId, userId, masterTxnRefNo, deviceId);
		logger.debug("meetingDateUpdate Header value :: {}", header);
		try {
			Mono<ResponseWrapper> monoResponseWrapper = kendraService.meetingDateUpdate(requestWrapper.getApiRequest(),
					header);
			return monoResponseWrapper.flatMap(resWrapper -> {
				logger.warn("meetingDateUpdate Response Wrapper ==> {}", resWrapper);
				return Mono.just(new ResponseEntity<>(resWrapper, HttpStatus.OK));
			});
		} catch (Exception e) {
			ResponseWrapper errorResponseWrapper = new ResponseWrapper();
			Response errorResponse = new Response();
			ResponseHeader errorHeader = new ResponseHeader();
			ResponseBody errorBody = new ResponseBody();
			CommonUtils.generateHeaderForFailure(errorHeader, e.getMessage());
			errorBody.setResponseObj("");
			errorResponse.setResponseBody(errorBody);
			errorResponse.setResponseHeader(errorHeader);
			errorResponseWrapper.setApiResponse(errorResponse);
			return Mono.just(new ResponseEntity<>(errorResponseWrapper, HttpStatus.OK));
		}
	}

	@ApiResponses({ @ApiResponse(responseCode = "200", description = "AppzillonBanking API reachable"),
			@ApiResponse(responseCode = "408", description = "Service Timed Out"),
			@ApiResponse(responseCode = "500", description = "Internal Server Error"),
			@ApiResponse(responseCode = "404", description = "AppzillonBanking not reachable") })
	@Operation(summary = "Meeting Day Update", description = "API to Update Next Meeting Day ")
	@PostMapping(value = "meetingDayUpdate", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	public Mono<ResponseEntity<ResponseWrapper>> meetingDayUpdate(
			@RequestBody MeetingDayUpdateRequestWrapper requestWrapper, @RequestHeader String appId,
			@RequestHeader String interfaceId, @RequestHeader String userId, @RequestHeader String masterTxnRefNo,
			@RequestHeader String deviceId) {

		logger.debug("meetingDayUpdate request data :: {}", requestWrapper);
		Header header = CommonUtils.obtainHeader(appId, interfaceId, userId, masterTxnRefNo, deviceId);
		logger.debug("meetingDayUpdate Header value :: {}", header);
		try {
			Mono<ResponseWrapper> monoResponseWrapper = kendraService.meetingDayUpdate(requestWrapper.getApiRequest(),
					header);
			return monoResponseWrapper.flatMap(resWrapper -> {
				logger.warn("meetingDayUpdate Response Wrapper ==> {}", resWrapper);
				return Mono.just(new ResponseEntity<>(resWrapper, HttpStatus.OK));
			});
		} catch (Exception e) {
			ResponseWrapper errorResponseWrapper = new ResponseWrapper();
			Response errorResponse = new Response();
			ResponseHeader errorHeader = new ResponseHeader();
			ResponseBody errorBody = new ResponseBody();
			CommonUtils.generateHeaderForFailure(errorHeader, e.getMessage());
			errorBody.setResponseObj("");
			errorResponse.setResponseBody(errorBody);
			errorResponse.setResponseHeader(errorHeader);
			errorResponseWrapper.setApiResponse(errorResponse);
			return Mono.just(new ResponseEntity<>(errorResponseWrapper, HttpStatus.OK));
		}
	}

	@ApiResponses({ @ApiResponse(responseCode = "200", description = "AppzillonBanking API reachable"),
			@ApiResponse(responseCode = "408", description = "Service Timed Out"),
			@ApiResponse(responseCode = "500", description = "Internal Server Error"),
			@ApiResponse(responseCode = "404", description = "AppzillonBanking not reachable") })
	@Operation(summary = "Meeting Day Update", description = "API to Update Next Meeting Day ")
	@PostMapping(value = "meetingFrequencyUpdate", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	public Mono<ResponseEntity<ResponseWrapper>> meetingFrequencyUpdate(
			@RequestBody MeetingFrequencyUpdateRequestWrapper requestWrapper, @RequestHeader String appId,
			@RequestHeader String interfaceId, @RequestHeader String userId, @RequestHeader String masterTxnRefNo,
			@RequestHeader String deviceId) {

		logger.debug("meetingFrequencyUpdate request data :: {}", requestWrapper);
		Header header = CommonUtils.obtainHeader(appId, interfaceId, userId, masterTxnRefNo, deviceId);
		logger.debug("meetingFrequencyUpdate Header value :: {}", header);
		try {
			Mono<ResponseWrapper> monoResponseWrapper = kendraService.meetingFrequencyUpdate(requestWrapper.getApiRequest(),
					header);
			return monoResponseWrapper.flatMap(resWrapper -> {
				logger.warn("meetingFrequencyUpdate Response Wrapper ==> {}", resWrapper);
				return Mono.just(new ResponseEntity<>(resWrapper, HttpStatus.OK));
			});
		} catch (Exception e) {
			ResponseWrapper errorResponseWrapper = new ResponseWrapper();
			Response errorResponse = new Response();
			ResponseHeader errorHeader = new ResponseHeader();
			ResponseBody errorBody = new ResponseBody();
			CommonUtils.generateHeaderForFailure(errorHeader, e.getMessage());
			errorBody.setResponseObj("");
			errorResponse.setResponseBody(errorBody);
			errorResponse.setResponseHeader(errorHeader);
			errorResponseWrapper.setApiResponse(errorResponse);
			return Mono.just(new ResponseEntity<>(errorResponseWrapper, HttpStatus.OK));
		}
	}
	
	@ApiResponses({ @ApiResponse(responseCode = "200", description = "AppzillonBanking API reachable"),
		@ApiResponse(responseCode = "408", description = "Service Timed Out"),
		@ApiResponse(responseCode = "500", description = "Internal Server Error"),
		@ApiResponse(responseCode = "404", description = "AppzillonBanking not reachable") })
@Operation(summary = "Meeting Time Update", description = "API to Update Next Meeting Day ")
@PostMapping(value = "meetingTimeUpdate", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
public Mono<ResponseEntity<ResponseWrapper>> meetingTimeUpdate(
		@RequestBody MeetingTimeUpdateRequestWrapper requestWrapper, @RequestHeader String appId,
		@RequestHeader String interfaceId, @RequestHeader String userId, @RequestHeader String masterTxnRefNo,
		@RequestHeader String deviceId) {

	logger.debug("meetingTimeUpdate request data :: {}", requestWrapper);
	Header header = CommonUtils.obtainHeader(appId, interfaceId, userId, masterTxnRefNo, deviceId);
	logger.debug("meetingTimeUpdate Header value :: {}", header);
	try {
		Mono<ResponseWrapper> monoResponseWrapper = kendraService.meetingTimeUpdate(requestWrapper.getApiRequest(),
				header);
		return monoResponseWrapper.flatMap(resWrapper -> {
			logger.warn("meetingTimeUpdate Response Wrapper ==> {}", resWrapper);
			return Mono.just(new ResponseEntity<>(resWrapper, HttpStatus.OK));
		});
	} catch (Exception e) {
		ResponseWrapper errorResponseWrapper = new ResponseWrapper();
		Response errorResponse = new Response();
		ResponseHeader errorHeader = new ResponseHeader();
		ResponseBody errorBody = new ResponseBody();
		CommonUtils.generateHeaderForFailure(errorHeader, e.getMessage());
		errorBody.setResponseObj("");
		errorResponse.setResponseBody(errorBody);
		errorResponse.setResponseHeader(errorHeader);
		errorResponseWrapper.setApiResponse(errorResponse);
		return Mono.just(new ResponseEntity<>(errorResponseWrapper, HttpStatus.OK));
	}
}
	
	@ApiResponses({ @ApiResponse(responseCode = "200", description = "AppzillonBanking API reachable"),
		@ApiResponse(responseCode = "408", description = "Service Timed Out"),
		@ApiResponse(responseCode = "500", description = "Internal Server Error"),
		@ApiResponse(responseCode = "404", description = "AppzillonBanking not reachable") })
@Operation(summary = "Kendra Details Update", description = "API to Update Kendra Details ")
@PostMapping(value = "kendraDetailsUpdate", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
public Mono<ResponseEntity<ResponseWrapper>> kendraDetailsUpdate(
		@RequestBody KendraDetailsUpdateRequestWrapper requestWrapper, @RequestHeader String appId,
		@RequestHeader String interfaceId, @RequestHeader String userId, @RequestHeader String masterTxnRefNo,
		@RequestHeader String deviceId) {

	logger.debug("kendraDetailsUpdate request data :: {}", requestWrapper);
	Header header = CommonUtils.obtainHeader(appId, interfaceId, userId, masterTxnRefNo, deviceId);
	logger.debug("kendraDetailsUpdate Header value :: {}", header);
	try {
		Mono<ResponseWrapper> monoResponseWrapper = kendraService.kendraDetailsUpdate(requestWrapper.getApiRequest(),
				header);
		return monoResponseWrapper.flatMap(resWrapper -> {
			logger.warn("kendraDetailsUpdate Response Wrapper ==> {}", resWrapper);
			return Mono.just(new ResponseEntity<>(resWrapper, HttpStatus.OK));
		});
	} catch (Exception e) {
		ResponseWrapper errorResponseWrapper = new ResponseWrapper();
		Response errorResponse = new Response();
		ResponseHeader errorHeader = new ResponseHeader();
		ResponseBody errorBody = new ResponseBody();
		CommonUtils.generateHeaderForFailure(errorHeader, e.getMessage());
		errorBody.setResponseObj("");
		errorResponse.setResponseBody(errorBody);
		errorResponse.setResponseHeader(errorHeader);
		errorResponseWrapper.setApiResponse(errorResponse);
		return Mono.just(new ResponseEntity<>(errorResponseWrapper, HttpStatus.OK));
	}
}
	
	@ApiResponses({ @ApiResponse(responseCode = "200", description = "AppzillonBanking API reachable"),
		@ApiResponse(responseCode = "408", description = "Service Timed Out"),
		@ApiResponse(responseCode = "500", description = "Internal Server Error"),
		@ApiResponse(responseCode = "404", description = "AppzillonBanking not reachable") })
@Operation(summary = "Kendra Details Update", description = "API to Update Kendra Details ")
@PostMapping(value = "multiKendraDetailsUpdate", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
public Mono<ResponseEntity<ResponseWrapper>> multiKendraDetailsUpdate(
		@RequestBody MultiKendraDetailsUpdateRequestWrapper requestWrapper, @RequestHeader String appId,
		@RequestHeader String interfaceId, @RequestHeader String userId, @RequestHeader String masterTxnRefNo,
		@RequestHeader String deviceId) {

	logger.debug("multiKendraDetailsUpdate request data :: {}", requestWrapper);
	Header header = CommonUtils.obtainHeader(appId, interfaceId, userId, masterTxnRefNo, deviceId);
	logger.debug("multiKendraDetailsUpdate Header value :: {}", header);
	try {
		Mono<ResponseWrapper> monoResponseWrapper = kendraService.multikendraDetailsUpdate(requestWrapper.getApiRequest(),
				header);
		return monoResponseWrapper.flatMap(resWrapper -> {
			logger.warn("multiKendraDetailsUpdate Response Wrapper ==> {}", resWrapper);
			return Mono.just(new ResponseEntity<>(resWrapper, HttpStatus.OK));
		});
	} catch (Exception e) {
		ResponseWrapper errorResponseWrapper = new ResponseWrapper();
		Response errorResponse = new Response();
		ResponseHeader errorHeader = new ResponseHeader();
		ResponseBody errorBody = new ResponseBody();
		CommonUtils.generateHeaderForFailure(errorHeader, e.getMessage());
		errorBody.setResponseObj("");
		errorResponse.setResponseBody(errorBody);
		errorResponse.setResponseHeader(errorHeader);
		errorResponseWrapper.setApiResponse(errorResponse);
		return Mono.just(new ResponseEntity<>(errorResponseWrapper, HttpStatus.OK));
	}
}
	
	
	
	
	@ApiResponses({ @ApiResponse(responseCode = "200", description = "AppzillonBanking API reachable"),
		@ApiResponse(responseCode = "408", description = "Service Timed Out"),
		@ApiResponse(responseCode = "500", description = "Internal Server Error"),
		@ApiResponse(responseCode = "404", description = "AppzillonBanking not reachable") })
@Operation(summary = "Kendra Address Update", description = "API to Update Kendra Details ")
@PostMapping(value = "kendraAddressUpdate", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
public Mono<ResponseEntity<ResponseWrapper>> UpdateKendraDetails(
		@RequestBody UpdateKendraAddressDetailsRequestWrapper requestWrapper, @RequestHeader String appId,
		@RequestHeader String interfaceId, @RequestHeader String userId, @RequestHeader String masterTxnRefNo,
		@RequestHeader String deviceId) {

	logger.debug("UpdateKendraAddressDetails request data :: {}", requestWrapper);
	Header header = CommonUtils.obtainHeader(appId, interfaceId, userId, masterTxnRefNo, deviceId);
	logger.debug("UpdateKendraAddressDetails Header value :: {}", header);
	try {
		Mono<ResponseWrapper> monoResponseWrapper = kendraService.updateKendraAddressDetails(requestWrapper.getApiRequest(),
				header);
		return monoResponseWrapper.flatMap(resWrapper -> {
			logger.warn("UpdateKendraAddressDetails Response Wrapper ==> {}", resWrapper);
			return Mono.just(new ResponseEntity<>(resWrapper, HttpStatus.OK));
		});
	} catch (Exception e) {
		ResponseWrapper errorResponseWrapper = new ResponseWrapper();
		Response errorResponse = new Response();
		ResponseHeader errorHeader = new ResponseHeader();
		ResponseBody errorBody = new ResponseBody();
		CommonUtils.generateHeaderForFailure(errorHeader, e.getMessage());
		errorBody.setResponseObj("");
		errorResponse.setResponseBody(errorBody);
		errorResponse.setResponseHeader(errorHeader);
		errorResponseWrapper.setApiResponse(errorResponse);
		return Mono.just(new ResponseEntity<>(errorResponseWrapper, HttpStatus.OK));
	}
}

}