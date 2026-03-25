package com.iexceed.appzillonbanking.cagl.collection.rest;

import com.iexceed.appzillonbanking.cagl.collection.constants.CommonConstants;
import com.iexceed.appzillonbanking.cagl.collection.payload.*;
import com.iexceed.appzillonbanking.core.payload.*;
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

import com.iexceed.appzillonbanking.cagl.collection.service.DeathIntimationService;
import com.iexceed.appzillonbanking.core.utils.CommonUtils;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import reactor.core.publisher.Mono;

@RestController
@Tag(description = "application/deathintimation", name = "Death Intimation")
@RequestMapping("application/deathintimation")
public class DeathIntimationAPI {

	private static final Logger logger = LogManager.getLogger(DeathIntimationAPI.class);

	@Autowired
	private DeathIntimationService deathIntimationService;

	@ApiResponses({ @ApiResponse(responseCode = "200", description = "AppzillonBanking API reachable"),
			@ApiResponse(responseCode = "408", description = "Service Timed Out"),
			@ApiResponse(responseCode = "500", description = "Internal Server Error"),
			@ApiResponse(responseCode = "404", description = "AppzillonBanking not reachable") })
	@Operation(summary = "Create Death Intimation", description = "API for storing Death Intimation Details")
	@PostMapping(value = "/create", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<ResponseWrapper> createDeathIntimation(
			@RequestBody DeathIntimationRequestWrapper requestWrapper, @RequestHeader String appId,
			@RequestHeader String interfaceId, @RequestHeader String userId, @RequestHeader String masterTxnRefNo,
			@RequestHeader String deviceId) {

		ResponseWrapper responseWrapper = new ResponseWrapper();
		logger.warn("Start : createDeathIntimation with request :: {}", requestWrapper);
		Header header = CommonUtils.obtainHeader(appId, interfaceId, userId, masterTxnRefNo, deviceId);
		logger.debug("createDeathIntimation Header value :: {} ", header);

		DeathIntimationRequest deathIntimationRequest = requestWrapper.getApiRequest();
		Response response = deathIntimationService.createDeathIntimation(deathIntimationRequest);
		responseWrapper.setApiResponse(response);
		logger.debug("End : createDeathIntimation response :: {}", response);
		return new ResponseEntity<>(responseWrapper, HttpStatus.OK);
	}
//
//	@ApiResponses({ @ApiResponse(responseCode = "200", description = "AppzillonBanking API reachable"),
//			@ApiResponse(responseCode = "408", description = "Service Timed Out"),
//			@ApiResponse(responseCode = "500", description = "Internal Server Error"),
//			@ApiResponse(responseCode = "404", description = "AppzillonBanking not reachable") })
//	@Operation(summary = "Fetch Death Intimation", description = "API for fetching Death Intimation Details")
//	@PostMapping(value = "/fetch", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
//	public ResponseEntity<ResponseWrapper> fetchDeathIntimation(
//			@RequestBody FetchDeathIntimationRequestWrapper requestWrapper, @RequestHeader String appId,
//			@RequestHeader String interfaceId, @RequestHeader String userId, @RequestHeader String masterTxnRefNo,
//			@RequestHeader String deviceId) {
//
//		ResponseWrapper responseWrapper = new ResponseWrapper();
//		logger.warn("Start : fetchDeathIntimation with request :: {}", requestWrapper);
//		Header header = CommonUtils.obtainHeader(appId, interfaceId, userId, masterTxnRefNo, deviceId);
//		logger.debug("fetchDeathIntimation Header value :: {} ", header);
//
//		FetchDeathIntimationRequest deathIntimationRequest = requestWrapper.getApiRequest();
//		Response response = deathIntimationService.fetchDeathIntimation(deathIntimationRequest);
//		responseWrapper.setApiResponse(response);
//		logger.debug("End : fetchDeathIntimation response :: {}", response);
//		return new ResponseEntity<>(responseWrapper, HttpStatus.OK);
//	}

	@ApiResponses({ @ApiResponse(responseCode = "200", description = "AppzillonBanking API reachable"),
			@ApiResponse(responseCode = "408", description = "Service Timed Out"),
			@ApiResponse(responseCode = "500", description = "Internal Server Error"),
			@ApiResponse(responseCode = "404", description = "AppzillonBanking not reachable") })
	@Operation(summary = "Fetch Death Intimation", description = "API for fetching Death Intimation Details")
	@PostMapping(value = "/fetch", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	public Mono<ResponseEntity<ResponseWrapper>> fetchDeathIntimation(@RequestBody FetchDeathIntimationRequestWrapper requestWrapper,
			@RequestHeader String appId, @RequestHeader String interfaceId,
			@RequestHeader String userId, @RequestHeader String masterTxnRefNo, @RequestHeader String deviceId) {

		logger.warn("Start : fetchDeathIntimation with request :: {}", requestWrapper);
		Header header = CommonUtils.obtainHeader(appId, interfaceId, userId, masterTxnRefNo, deviceId);
		logger.debug("Header value :: {}", header);
		return deathIntimationService.fetchDeathIntimation(requestWrapper, header)
				.map(response -> {
					ResponseWrapper wrapper = new ResponseWrapper();
					wrapper.setApiResponse(response);
					logger.debug("End : fetchDeathIntimation response :: {}", response);
					return new ResponseEntity<>(wrapper, HttpStatus.OK);
				})
				.onErrorResume(ex -> {
					logger.error("Error in fetching deathIntimation applications", ex);
					ResponseWrapper wrapper = new ResponseWrapper();
					Response errorResponse = new Response();
					ResponseHeader respHeader = new ResponseHeader();
					ResponseBody respBody = new ResponseBody();
					respBody.setResponseObj("");
					CommonUtils.generateHeaderForFailure(respHeader, CommonConstants.EXCEPTION_MSG);
					errorResponse.setResponseHeader(respHeader);
					errorResponse.setResponseBody(respBody);
					wrapper.setApiResponse(errorResponse);
					return Mono.just(new ResponseEntity<>(wrapper, HttpStatus.OK));
				});
	}
	@ApiResponses({ @ApiResponse(responseCode = "200", description = "AppzillonBanking API reachable"),
			@ApiResponse(responseCode = "408", description = "Service Timed Out"),
			@ApiResponse(responseCode = "500", description = "Internal Server Error"),
			@ApiResponse(responseCode = "404", description = "AppzillonBanking not reachable") })
	@Operation(summary = "Approve/Pushback Death Intimation", description = "API for Approval/Pushback of Death Intimation")
	@PostMapping(value = "/approvepushback", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<ResponseWrapper> approvePushbackDeathIntimation(
			@RequestBody DeathIntimationApprovePushbackRequestWrapper requestWrapper, @RequestHeader String appId,
			@RequestHeader String interfaceId, @RequestHeader String userId, @RequestHeader String masterTxnRefNo,
			@RequestHeader String deviceId) {

		logger.warn("Start : approvePushbackDeathIntimation with request :: {}", requestWrapper);
		Header header = CommonUtils.obtainHeader(appId, interfaceId, userId, masterTxnRefNo, deviceId);
		logger.debug("approveApplication Header value :: {} ", header);

		DeathIntimationApprovePushbackRequest approvePushbackRequest = requestWrapper.getApiRequest();
		Response response = deathIntimationService.approvePushbackDeathIntimation(approvePushbackRequest);

		ResponseWrapper responseWrapper = new ResponseWrapper();
		responseWrapper.setApiResponse(response);
		logger.debug("End : approveApplication response :: {}", response);
		return new ResponseEntity<>(responseWrapper, HttpStatus.OK);

	}

	@ApiResponses({ @ApiResponse(responseCode = "200", description = "AppzillonBanking API reachable"),
			@ApiResponse(responseCode = "408", description = "Service Timed Out"),
			@ApiResponse(responseCode = "500", description = "Internal Server Error"),
			@ApiResponse(responseCode = "404", description = "AppzillonBanking not reachable") })
	@Operation(summary = "Insurance details Death Intimation", description = "API for fetching insurance detaisl")
	@PostMapping(value = "/claim/initiate", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	public Mono<ResponseEntity<ResponseWrapper>> initiateClaim(
			@RequestBody ClaimInitiationRequestWrapper requestWrapper,
			@RequestHeader String appId,
			@RequestHeader String interfaceId,
			@RequestHeader String userId,
			@RequestHeader String masterTxnRefNo,
			@RequestHeader String deviceId) {

		Header header = CommonUtils.obtainHeader(appId, interfaceId, userId, masterTxnRefNo, deviceId);
		ClaimInitiationRequest claimInitiationRequest = requestWrapper.getApiRequest();

		return deathIntimationService.initiateClaim(claimInitiationRequest, header)
				.map(response -> {
					ResponseWrapper wrapper = new ResponseWrapper();
					wrapper.setApiResponse(response);
					return ResponseEntity.ok(wrapper);
				});
	}
}
