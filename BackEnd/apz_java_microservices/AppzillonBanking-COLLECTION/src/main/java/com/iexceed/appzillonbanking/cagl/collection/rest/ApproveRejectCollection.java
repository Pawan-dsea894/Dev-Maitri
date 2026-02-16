package com.iexceed.appzillonbanking.cagl.collection.rest;

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

import com.iexceed.appzillonbanking.cagl.collection.payload.ApprovePushbackApplnRequest;
import com.iexceed.appzillonbanking.cagl.collection.payload.ApprovePushbackApplnRequestWrapper;
import com.iexceed.appzillonbanking.cagl.collection.service.ApprovePushbackService;
import com.iexceed.appzillonbanking.core.payload.Header;
import com.iexceed.appzillonbanking.core.payload.Response;
import com.iexceed.appzillonbanking.core.payload.ResponseWrapper;
import com.iexceed.appzillonbanking.core.utils.CommonUtils;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import reactor.core.publisher.Mono;

@RestController
@Tag(description = "application/collection", name = "Pushback / Approve Collections")
@RequestMapping("application/collection")
public class ApproveRejectCollection {

	private static final Logger logger = LogManager.getLogger(ApproveRejectCollection.class);

	@Autowired
	private ApprovePushbackService approvePushbackService;

	@ApiResponses({ @ApiResponse(responseCode = "200", description = "AppzillonBanking API reachable"),
			@ApiResponse(responseCode = "408", description = "Service Timed Out"),
			@ApiResponse(responseCode = "500", description = "Internal Server Error"),
			@ApiResponse(responseCode = "404", description = "AppzillonBanking not reachable") })
	@Operation(summary = "Approve application", description = "Approve Application")
	@PostMapping(value = "/approve", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	public Mono<ResponseEntity<ResponseWrapper>> approveApplication(
			@RequestBody ApprovePushbackApplnRequestWrapper requestWrapper, @RequestHeader String appId,
			@RequestHeader String interfaceId, @RequestHeader String userId, @RequestHeader String masterTxnRefNo,
			@RequestHeader String deviceId) {

		logger.warn("Start : approveApplication with request :: {}", requestWrapper);
		Header header = CommonUtils.obtainHeader(appId, interfaceId, userId, masterTxnRefNo, deviceId);
		logger.debug("approveApplication Header value :: {} ", header);

		ApprovePushbackApplnRequest approvePushbackApplnRequest = requestWrapper.getApiRequest();
		Mono<Response> responseMono = approvePushbackService.approveApplication(approvePushbackApplnRequest, header);

		return responseMono.flatMap(response -> {
			ResponseWrapper responseWrapper = new ResponseWrapper();
			responseWrapper.setApiResponse(response);
			logger.debug("End : approveApplication response :: {}", response);
			return Mono.just(new ResponseEntity<>(responseWrapper, HttpStatus.OK));
		});

	}

	@ApiResponses({ @ApiResponse(responseCode = "200", description = "AppzillonBanking API reachable"),
			@ApiResponse(responseCode = "408", description = "Service Timed Out"),
			@ApiResponse(responseCode = "500", description = "Internal Server Error"),
			@ApiResponse(responseCode = "404", description = "AppzillonBanking not reachable") })
	@Operation(summary = "Pushback application", description = "Pushback Application")
	@PostMapping(value = "/pushback", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<ResponseWrapper> pushBackApplication(
			@RequestBody ApprovePushbackApplnRequestWrapper requestWrapper, @RequestHeader String appId,
			@RequestHeader String interfaceId, @RequestHeader String userId, @RequestHeader String masterTxnRefNo,
			@RequestHeader String deviceId) {

		ResponseWrapper responseWrapper = new ResponseWrapper();
		logger.warn("Start : pushbackApplication with request :: {}", requestWrapper);
		Header header = CommonUtils.obtainHeader(appId, interfaceId, userId, masterTxnRefNo, deviceId);
		logger.debug("pushbackApplication Header value :: {} ", header);

		ApprovePushbackApplnRequest approvePushbackApplnRequest = requestWrapper.getApiRequest();
		Response response = approvePushbackService.pushbackApplication(approvePushbackApplnRequest, header);
		responseWrapper.setApiResponse(response);
		logger.debug("End : pushbackApplication response :: {}", response);
		return new ResponseEntity<>(responseWrapper, HttpStatus.OK);
	}
}
