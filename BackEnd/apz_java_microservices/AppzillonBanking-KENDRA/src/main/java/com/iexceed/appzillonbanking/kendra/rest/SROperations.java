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
import com.iexceed.appzillonbanking.core.payload.ResponseWrapper;
import com.iexceed.appzillonbanking.core.utils.CommonUtils;
import com.iexceed.appzillonbanking.interfaceAdapter.utils.AdapterUtil;
import com.iexceed.appzillonbanking.kendra.payload.FetchAllSRRecordsRequest;
import com.iexceed.appzillonbanking.kendra.payload.FetchAllSRRecordsRequestWrapper;
import com.iexceed.appzillonbanking.kendra.payload.SRApplicationFetchRequest;
import com.iexceed.appzillonbanking.kendra.payload.SRApplicationFetchRequestWrapper;
import com.iexceed.appzillonbanking.kendra.payload.SRApplicationRequest;
import com.iexceed.appzillonbanking.kendra.payload.SRApplicationRequestWrapper;
import com.iexceed.appzillonbanking.kendra.payload.SRUpdateApplicationRequest;
import com.iexceed.appzillonbanking.kendra.payload.SRUpdateApplicationRequestWrapper;
import com.iexceed.appzillonbanking.kendra.service.SRCreationService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import reactor.core.publisher.Mono;

@RestController
@Tag(description = "application/srOperations", name = "SROPERATIONS")
@RequestMapping("application/srOperations")
public class SROperations {

	private static final Logger logger = LogManager.getLogger(SROperations.class);

	@Autowired
	private SRCreationService srCreationService;
	
	@Autowired
	private AdapterUtil adapterUtil;

	/**
	 * Rest API Service to raise SR in DB
	 * 
	 * @author akshay.shahane
	 * @since 19.06.2024
	 * @return Object which will give status of transaction
	 */
	@ApiResponses({ @ApiResponse(responseCode = "200", description = "AppzillonBanking API reachable"),
			@ApiResponse(responseCode = "408", description = "Service Timed Out"),
			@ApiResponse(responseCode = "500", description = "Internal Server Error"),
			@ApiResponse(responseCode = "404", description = "AppzillonBanking not reachable") })
	@Operation(summary = "SR Insetion", description = "API to Insert SRCreation Record in DB")
	@PostMapping(value = "/insert", consumes =MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<ResponseWrapper> insertSRRecord(@RequestBody SRApplicationRequestWrapper requestWrapper,
			@RequestHeader(defaultValue = "APZRMB") String appId,
			@RequestHeader(defaultValue = "SRInsert") String interfaceId,
			@RequestHeader(defaultValue = "000000000002") String userId,
			@RequestHeader(defaultValue = "12345678") String masterTxnRefNo,
			@RequestHeader(defaultValue = "abcd1234efgh5678") String deviceId) {

		ResponseWrapper responseWrapper = new ResponseWrapper();
		logger.debug("SRCreation request data :: {}", requestWrapper);
		Header header = CommonUtils.obtainHeader(appId, interfaceId, userId, masterTxnRefNo, deviceId);
		logger.debug("SRCreation Header value :: {}", header);
		SRApplicationRequest apiRequest = requestWrapper.getApiRequest();
		logger.debug("APIRequest :: {}", apiRequest);

		Response response = srCreationService.saveApplication(apiRequest,header);
		responseWrapper.setApiResponse(response);
		logger.debug("End : SRCreation response :: {}", response);
		return new ResponseEntity<>(responseWrapper, HttpStatus.OK);
	}

	/**
	 * Rest API Service to Update SR in DB
	 * 
	 * @author akshay.shahane
	 * @since 20.06.2024
	 * @return Object which will give status of updated SR
	 */
	@ApiResponses({ @ApiResponse(responseCode = "200", description = "AppzillonBanking API reachable"),
			@ApiResponse(responseCode = "408", description = "Service Timed Out"),
			@ApiResponse(responseCode = "500", description = "Internal Server Error"),
			@ApiResponse(responseCode = "404", description = "AppzillonBanking not reachable") })
	@Operation(summary = "SR Updation", description = "API to Update SRCreation Record in DB")
	@PostMapping(value = "/update", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	public Mono<ResponseEntity<ResponseWrapper>> updateSRRecord(@RequestBody SRUpdateApplicationRequestWrapper requestWrapper, 
			@RequestHeader(defaultValue = "APZRMB") String appId,
			@RequestHeader(defaultValue = "SRUpdate") String interfaceId,
			@RequestHeader(defaultValue = "000000000002") String userId,
			@RequestHeader(defaultValue = "12345678") String masterTxnRefNo,
			@RequestHeader(defaultValue = "abcd1234efgh5678") String deviceId) {
		logger.debug("updateSRRecord request data :: {}", requestWrapper);
		Header header = CommonUtils.obtainHeader(appId, interfaceId, userId, masterTxnRefNo, deviceId);
		logger.debug("updateSRRecord Header value :: {}", header);
		SRUpdateApplicationRequest apiRequest = requestWrapper.getApiRequest();
		logger.debug("APIRequest :: {}", apiRequest);

		Mono<ResponseWrapper> updateSRRecord = srCreationService.updateSRRecord(apiRequest,header);
		return updateSRRecord.flatMap(resWrapper -> {
			logger.warn("Final updateSRRecord Response Wrapper ==> {}", resWrapper);
			return Mono.just(new ResponseEntity<>(resWrapper, HttpStatus.OK));
		});
	}
	
	/**
	 * Rest API Service to Fetch SRCreation Record from DB
	 * 
	 * @author akshay.shahane
	 * @since 20.06.2024
	 * @return Object which will give SRCreation Record
	 */
	@ApiResponses({ @ApiResponse(responseCode = "200", description = "AppzillonBanking API reachable"),
			@ApiResponse(responseCode = "408", description = "Service Timed Out"),
			@ApiResponse(responseCode = "500", description = "Internal Server Error"),
			@ApiResponse(responseCode = "404", description = "AppzillonBanking not reachable") })
	@Operation(summary = "SR Fetch", description = "API to Fetch SRCreation Record from DB")
	@PostMapping(value = "/fetch", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<ResponseWrapper> fetchSRRecord(@RequestBody SRApplicationFetchRequestWrapper requestWrapper, 
			@RequestHeader(defaultValue = "APZRMB") String appId,
			@RequestHeader(defaultValue = "SRFetch") String interfaceId,
			@RequestHeader(defaultValue = "000000000002") String userId,
			@RequestHeader(defaultValue = "12345678") String masterTxnRefNo,
			@RequestHeader(defaultValue = "abcd1234efgh5678") String deviceId) {

		ResponseWrapper responseWrapper = new ResponseWrapper();
		logger.debug("Fetch SR request data :: {}", requestWrapper);
		Header header = CommonUtils.obtainHeader(appId, interfaceId, userId, masterTxnRefNo, deviceId);
		logger.debug("Fetch SR Header value :: {}", header);
		SRApplicationFetchRequest apiRequest = requestWrapper.getApiRequest();
		logger.debug("APIRequest :: {}", apiRequest);

		Response response = srCreationService.fetchSRApplication(apiRequest,header);
		responseWrapper.setApiResponse(response);
		logger.debug("End : SRCreation Fetch response :: {}", response);
		return new ResponseEntity<>(responseWrapper, HttpStatus.OK);
	}
	
	@ApiResponses({ @ApiResponse(responseCode = "200", description = "AppzillonBanking API reachable"),
		@ApiResponse(responseCode = "408", description = "Service Timed Out"),
		@ApiResponse(responseCode = "500", description = "Internal Server Error"),
		@ApiResponse(responseCode = "404", description = "AppzillonBanking not reachable") })
@Operation(summary = "SR FetchAll", description = "API to FetchAll SRCreation Record from DB")
@PostMapping(value = "/fetchAll", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
public ResponseEntity<ResponseWrapper> fetchAllSRRecord(@RequestBody FetchAllSRRecordsRequestWrapper requestWrapper, 
		@RequestHeader(defaultValue = "APZRMB") String appId,
		@RequestHeader(defaultValue = "SRFetch") String interfaceId,
		@RequestHeader(defaultValue = "000000000002") String userId,
		@RequestHeader(defaultValue = "12345678") String masterTxnRefNo,
		@RequestHeader(defaultValue = "abcd1234efgh5678") String deviceId) {

	ResponseWrapper responseWrapper = new ResponseWrapper();
	logger.debug("FetchAll SR request data :: {}", requestWrapper);
	Header header = CommonUtils.obtainHeader(appId, interfaceId, userId, masterTxnRefNo, deviceId);
	logger.debug("FetchAll SR Header value :: {}", header);
	FetchAllSRRecordsRequest apiRequest = requestWrapper.getApiRequest();
	logger.debug("APIRequest :: {}", apiRequest);

	Response response = srCreationService.fetchAllSRApplication(apiRequest,header);
	responseWrapper.setApiResponse(response);
	logger.debug("End : SRCreation FetchAll response :: {}", response);
	return new ResponseEntity<>(responseWrapper, HttpStatus.OK);
}
	
}
