package com.iexceed.appzillonbanking.cagl.collection.rest;

import com.iexceed.appzillonbanking.cagl.collection.payload.*;
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

import com.iexceed.appzillonbanking.cagl.collection.service.CollectionService;
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
@Tag(description = "application/collection", name = "Collections")
@RequestMapping("application/collection")
public class CollectionAPI {

	private static final Logger logger = LogManager.getLogger(CollectionAPI.class);

	@Autowired
	private CollectionService collectionService;

	@ApiResponses({ @ApiResponse(responseCode = "200", description = "AppzillonBanking API reachable"),
			@ApiResponse(responseCode = "408", description = "Service Timed Out"),
			@ApiResponse(responseCode = "500", description = "Internal Server Error"),
			@ApiResponse(responseCode = "404", description = "AppzillonBanking not reachable") })
	@Operation(summary = "Create application", description = "API for storing Collection Details")
	@PostMapping(value = "/create", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<ResponseWrapper> createApplication(@RequestBody CollectionRequestWrapper requestWrapper,
			@RequestHeader String appId, @RequestHeader String interfaceId, @RequestHeader String userId,
			@RequestHeader String masterTxnRefNo, @RequestHeader String deviceId) {

		ResponseWrapper responseWrapper = new ResponseWrapper();
		logger.warn("Start : createApplication with request :: {}", requestWrapper);
		Header header = CommonUtils.obtainHeader(appId, interfaceId, userId, masterTxnRefNo, deviceId);
		logger.debug("createApplication Header value :: {} ", header);

		CollectionRequest collectionRequest = requestWrapper.getApiRequest();
		Response response = collectionService.createApplication(collectionRequest, header);
		responseWrapper.setApiResponse(response);
		logger.debug("End : createApplication response :: {}", response);
		return new ResponseEntity<>(responseWrapper, HttpStatus.OK);
	}

	@ApiResponses({ @ApiResponse(responseCode = "200", description = "AppzillonBanking API reachable"),
			@ApiResponse(responseCode = "408", description = "Service Timed Out"),
			@ApiResponse(responseCode = "500", description = "Internal Server Error"),
			@ApiResponse(responseCode = "404", description = "AppzillonBanking not reachable") })
	@Operation(summary = "Fetch application", description = "API for fetching Collection Details")
	@PostMapping(value = "/fetch", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	public Mono<ResponseEntity<ResponseWrapper>> fetchApplication(
			@RequestBody FetchCollectionRequestWrapper requestWrapper, @RequestHeader String appId,
			@RequestHeader String interfaceId, @RequestHeader String userId, @RequestHeader String masterTxnRefNo,
			@RequestHeader String deviceId) {

		ResponseWrapper responseWrapper = new ResponseWrapper();
		logger.warn("Start : FetchApplication with request :: {}", requestWrapper);
		Header header = CommonUtils.obtainHeader(appId, interfaceId, userId, masterTxnRefNo, deviceId);
		logger.debug("fetchApplication Header value :: {} ", header);

		FetchCollectionRequest collectionRequest = requestWrapper.getApiRequest();
		Mono<Response> responseMono = collectionService.fetchApplication(collectionRequest, header);
		return responseMono.flatMap(val -> {
			responseWrapper.setApiResponse(val);
			logger.debug("End : fetchApplication response :: {}", val);
			return Mono.just(new ResponseEntity<>(responseWrapper, HttpStatus.OK));
		});
	}

	@ApiResponses({ @ApiResponse(responseCode = "200", description = "AppzillonBanking API reachable"),
			@ApiResponse(responseCode = "408", description = "Service Timed Out"),
			@ApiResponse(responseCode = "500", description = "Internal Server Error"),
			@ApiResponse(responseCode = "404", description = "AppzillonBanking not reachable") })
	@Operation(summary = "Get File Base64", description = "API for fetching base64 content of a File")
	@PostMapping(value = "/getFileData", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<ResponseWrapper> getFileData(@RequestBody FetchCollectionRequestWrapper requestWrapper,
			@RequestHeader String appId, @RequestHeader String interfaceId, @RequestHeader String userId,
			@RequestHeader String masterTxnRefNo, @RequestHeader String deviceId) {

		ResponseWrapper responseWrapper = new ResponseWrapper();
		logger.warn("Start : getFileData with request :: {}", requestWrapper);
		Header header = CommonUtils.obtainHeader(appId, interfaceId, userId, masterTxnRefNo, deviceId);
		logger.debug("getFileData Header value :: {} ", header);

		FetchCollectionRequest collectionRequest = requestWrapper.getApiRequest();
		Response response = collectionService.getFileData(collectionRequest, header);
		responseWrapper.setApiResponse(response);
		logger.debug("End : getFileData response :: {}", response);
		return new ResponseEntity<>(responseWrapper, HttpStatus.OK);
	}

	@ApiResponses({ @ApiResponse(responseCode = "200", description = "AppzillonBanking API reachable"),
			@ApiResponse(responseCode = "408", description = "Service Timed Out"),
			@ApiResponse(responseCode = "500", description = "Internal Server Error"),
			@ApiResponse(responseCode = "404", description = "AppzillonBanking not reachable") })
	@Operation(summary = "Fetch Repeat Collection", description = "API for fetching Repeat Collection Data")
	@PostMapping(value = "/fetch/repeatCol", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	public Mono<ResponseEntity<ResponseWrapper>> fetchRepeatCollection(
			@RequestBody FetchRptAndNonMeetingColReqWrapper requestWrapper, @RequestHeader String appId,
			@RequestHeader String interfaceId, @RequestHeader String userId, @RequestHeader String masterTxnRefNo,
			@RequestHeader String deviceId) {

		ResponseWrapper responseWrapper = new ResponseWrapper();
		logger.warn("Start : fetchRepeatCollection with request :: {}", requestWrapper);
		Header header = CommonUtils.obtainHeader(appId, interfaceId, userId, masterTxnRefNo, deviceId);
		logger.debug("fetchRepeatCollection Header value :: {} ", header);

		FetchRptAndNonMeetingColReq colReq = requestWrapper.getApiRequest();
		Mono<Response> responseMono = collectionService.fetchRepeatCollection(colReq, header);
		return responseMono.flatMap(response -> {
			responseWrapper.setApiResponse(response);
			logger.warn("End : fetchRepeatCollection response :: {}", response);
			return Mono.just(new ResponseEntity<>(responseWrapper, HttpStatus.OK));
		});
	}

	@ApiResponses({ @ApiResponse(responseCode = "200", description = "AppzillonBanking API reachable"),
			@ApiResponse(responseCode = "408", description = "Service Timed Out"),
			@ApiResponse(responseCode = "500", description = "Internal Server Error"),
			@ApiResponse(responseCode = "404", description = "AppzillonBanking not reachable") })
	@Operation(summary = "Fetch Non Meeting Day Collection", description = "API for fetching Non Meeting Day Collection Data")
	@PostMapping(value = "/fetch/nonMeetingCol", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	public Mono<ResponseEntity<ResponseWrapper>> fetchNonMeetingCollection(
			@RequestBody FetchRptAndNonMeetingColReqWrapper requestWrapper, @RequestHeader String appId,
			@RequestHeader String interfaceId, @RequestHeader String userId, @RequestHeader String masterTxnRefNo,
			@RequestHeader String deviceId) {

		ResponseWrapper responseWrapper = new ResponseWrapper();
		logger.warn("Start : fetchNonMeetingCollection with request :: {}", requestWrapper);
		Header header = CommonUtils.obtainHeader(appId, interfaceId, userId, masterTxnRefNo, deviceId);
		logger.debug("fetchNonMeetingCollection Header value :: {} ", header);

		FetchRptAndNonMeetingColReq colReq = requestWrapper.getApiRequest();
		Mono<Response> responseMono = collectionService.fetchNonMeetingCollection(colReq, header);
		return responseMono.flatMap(response -> {
			responseWrapper.setApiResponse(response);
			logger.warn("End : fetchNonMeetingCollection response :: {}", response);
			return Mono.just(new ResponseEntity<>(responseWrapper, HttpStatus.OK));
		});
	}

	@ApiResponses({ @ApiResponse(responseCode = "200", description = "AppzillonBanking API reachable"),
			@ApiResponse(responseCode = "408", description = "Service Timed Out"),
			@ApiResponse(responseCode = "500", description = "Internal Server Error"),
			@ApiResponse(responseCode = "404", description = "AppzillonBanking not reachable") })
	@Operation(summary = "Verify Application", description = "API to check if the Application exists based on Kendra Id and Branch Id")
	@PostMapping(value = "/verifyApplication", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<ResponseWrapper> verifyApplication(@RequestBody VerifyApplnReqWrapper requestWrapper,
			@RequestHeader String appId, @RequestHeader String interfaceId, @RequestHeader String userId,
			@RequestHeader String masterTxnRefNo, @RequestHeader String deviceId) {

		logger.warn("Start : verifyApplication with request :: {}", requestWrapper);
		Header header = CommonUtils.obtainHeader(appId, interfaceId, userId, masterTxnRefNo, deviceId);
		logger.debug("verifyApplication Header value :: {} ", header);

		VerifyApplnRequest verifyApplnRequest = requestWrapper.getApiRequest();
		Response response = collectionService.verifyApplication(verifyApplnRequest);
		logger.warn("End : verifyApplication response :: {}", response);
		ResponseWrapper responseWrapper = ResponseWrapper.builder().apiResponse(response).build();
		return new ResponseEntity<>(responseWrapper, HttpStatus.OK);
	}

	@ApiResponses({ @ApiResponse(responseCode = "200", description = "AppzillonBanking API reachable"),
			@ApiResponse(responseCode = "408", description = "Service Timed Out"),
			@ApiResponse(responseCode = "500", description = "Internal Server Error"),
			@ApiResponse(responseCode = "404", description = "AppzillonBanking not reachable") })
	@Operation(summary = "Fetch EmergencyLoan Details", description = "API to fetch Emergency loan details based on Kendra Id, Meeting Date and Product Code ")
	@PostMapping(value = "/fetch/emergencyLoanDtls", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<ResponseWrapper> fetchEmergencyLoanDtls(
			@RequestBody FetchEmergencyLoanRequestWrapper requestWrapper, @RequestHeader String appId,
			@RequestHeader String interfaceId, @RequestHeader String userId, @RequestHeader String masterTxnRefNo,
			@RequestHeader String deviceId) {

		ResponseWrapper responseWrapper = new ResponseWrapper();
		logger.warn("Start : fetchEmergencyLoan with request :: {}", requestWrapper);
		Header header = CommonUtils.obtainHeader(appId, interfaceId, userId, masterTxnRefNo, deviceId);
		logger.debug("fetchEmergencyLoan Header value :: {}", header);
		FetchEmergencyLoanRequest loanRequest = requestWrapper.getApiRequest();
		Response response = collectionService.fetchEmergencyLoanDetails(loanRequest, header);
		responseWrapper.setApiResponse(response);
		logger.warn("End : fetchEmergencyLoan response :: {}", response);
		return new ResponseEntity<>(responseWrapper, HttpStatus.OK);
	}
	
	@ApiResponses({ @ApiResponse(responseCode = "200", description = "AppzillonBanking API reachable"),
			@ApiResponse(responseCode = "408", description = "Service Timed Out"),
			@ApiResponse(responseCode = "500", description = "Internal Server Error"),
			@ApiResponse(responseCode = "404", description = "AppzillonBanking not reachable") })
	@Operation(summary = "Fetch EmergencyLoan Details", description = "API to fetch Emergency loan details based on Kendra Id, Meeting Date and Product Code ")
	@PostMapping(value = "/applicationDedupe", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<ResponseWrapper> applicationDedupe(
			@RequestBody ApplicationDedupeRequestWrapper requestWrapper, @RequestHeader String appId,
			@RequestHeader String interfaceId, @RequestHeader String userId, @RequestHeader String masterTxnRefNo,
			@RequestHeader String deviceId) {

		ResponseWrapper responseWrapper = new ResponseWrapper();
		logger.warn("Start : applicationDedupe with request :: {}", requestWrapper);
		Header header = CommonUtils.obtainHeader(appId, interfaceId, userId, masterTxnRefNo, deviceId);
		logger.debug("applicationDedupe Header value :: {}", header);
		ApplicationDedupeRequest dedupeRequest = requestWrapper.getApiRequest();
		Response response = collectionService.applicationDedupeService(dedupeRequest);
		responseWrapper.setApiResponse(response);
		logger.warn("End : applicationDedupe response :: {}", response);
		return new ResponseEntity<>(responseWrapper, HttpStatus.OK);
	}
}
