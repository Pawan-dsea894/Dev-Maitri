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
import com.iexceed.appzillonbanking.kendra.payload.EditOrDeleteKendraAssignmentRequest;
import com.iexceed.appzillonbanking.kendra.payload.EditOrDeleteKendraAssignmentRequestWrapper;
import com.iexceed.appzillonbanking.kendra.payload.KendraAssignmentFetchRequest;
import com.iexceed.appzillonbanking.kendra.payload.KendraAssignmentFetchRequestWrapper;
import com.iexceed.appzillonbanking.kendra.payload.KendraAssignmentRequest;
import com.iexceed.appzillonbanking.kendra.payload.KendraAssignmentRequestWrapper;
import com.iexceed.appzillonbanking.kendra.service.KendraAssignmentService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import reactor.core.publisher.Mono;

@RestController
@Tag(description = "application/kendraAssign", name = "KENDRAASSIGNMENT")
@RequestMapping("application/kendraAssign")
public class KendraAssignmentController {

	private static final Logger logger = LogManager.getLogger(KendraAssignmentController.class);

	@Autowired
	private KendraAssignmentService kendraAssignmentService;
	
	/**
	 * Rest API Service to insert kendra assignment record in DB
	 * 
	 * @author akshay.shahane
	 * @since 21.06.2024
	 * @return Object which will give status of inserted record
	 */
	@ApiResponses({ @ApiResponse(responseCode = "200", description = "AppzillonBanking API reachable"),
			@ApiResponse(responseCode = "408", description = "Service Timed Out"),
			@ApiResponse(responseCode = "500", description = "Internal Server Error"),
			@ApiResponse(responseCode = "404", description = "AppzillonBanking not reachable") })
	@Operation(summary = "KendraAssignment Insetion", description = "API to Insert KendraAssignment Record in DB")
	@PostMapping(value = "/add", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	public Mono<ResponseEntity<ResponseWrapper>> insertKendraAssignmentRecord(@RequestBody KendraAssignmentRequestWrapper requestWrapper,
			@RequestHeader(defaultValue = "APZRMB") String appId,
			@RequestHeader(defaultValue = "KendraAssignmentInsert") String interfaceId,
			@RequestHeader(defaultValue = "000000000002") String userId,
			@RequestHeader(defaultValue = "12345678") String masterTxnRefNo,
			@RequestHeader(defaultValue = "abcd1234efgh5678") String deviceId) {
		logger.debug("KendraAssignmentInsertion request data :: {}", requestWrapper);
		Header header = CommonUtils.obtainHeader(appId, interfaceId, userId, masterTxnRefNo, deviceId);
		logger.debug("KendraAssignmentInsertion Header value :: {}", header);
		KendraAssignmentRequest apiRequest = requestWrapper.getApiRequest();
		logger.debug("APIRequest :: {}", apiRequest);

		Mono<ResponseWrapper> saveApplicationResponse = kendraAssignmentService.saveApplication(apiRequest, header);
		return saveApplicationResponse.flatMap(resWrapper -> {
			logger.warn("Final saveApplicationResponse Response Wrapper ==> {}", resWrapper);
			return Mono.just(new ResponseEntity<>(resWrapper, HttpStatus.OK));
		});
	}
	
	/**
	 * Rest API Service to fetch kendra assignment record from DB
	 * 
	 * @author akshay.shahane
	 * @since 24.06.2024
	 * @return Object which will give the kendra assignment record
	 */
	@ApiResponses({ @ApiResponse(responseCode = "200", description = "AppzillonBanking API reachable"),
			@ApiResponse(responseCode = "408", description = "Service Timed Out"),
			@ApiResponse(responseCode = "500", description = "Internal Server Error"),
			@ApiResponse(responseCode = "404", description = "AppzillonBanking not reachable") })
	@Operation(summary = "KendraAssignment Fetch", description = "API to Fetch KendraAssignment Record from DB")
	@PostMapping(value = "/fetch", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<ResponseWrapper> fetchKendraAssignmentRecord(@RequestBody KendraAssignmentFetchRequestWrapper requestWrapper,
			@RequestHeader(defaultValue = "APZRMB") String appId,
			@RequestHeader(defaultValue = "KendraAssignmentFetch") String interfaceId,
			@RequestHeader(defaultValue = "000000000002") String userId,
			@RequestHeader(defaultValue = "12345678") String masterTxnRefNo,
			@RequestHeader(defaultValue = "abcd1234efgh5678") String deviceId) {

		ResponseWrapper responseWrapper = new ResponseWrapper();
		logger.debug("KendraAssignmentFetch request data :: {}", requestWrapper);
		Header header = CommonUtils.obtainHeader(appId, interfaceId, userId, masterTxnRefNo, deviceId);
		logger.debug("KendraAssignmentFetch Header value :: {}", header);
		KendraAssignmentFetchRequest apiRequest = requestWrapper.getApiRequest();
		logger.debug("APIRequest :: {}", apiRequest);

		Response response = kendraAssignmentService.fetchKendraAssignmentRecord(apiRequest);
		responseWrapper.setApiResponse(response);
		logger.debug("End : KendraAssignmentFetch response :: {}", response);
		return new ResponseEntity<>(responseWrapper, HttpStatus.OK);
	}
	
	/**
	 * Rest API Service to Edit Or Delete KendraAssignment Record in DB
	 * 
	 * @author akshay.shahane
	 * @since 26.06.2024
	 * @return Object which will give the status of modified record
	 */
	@ApiResponses({ @ApiResponse(responseCode = "200", description = "AppzillonBanking API reachable"),
			@ApiResponse(responseCode = "408", description = "Service Timed Out"),
			@ApiResponse(responseCode = "500", description = "Internal Server Error"),
			@ApiResponse(responseCode = "404", description = "AppzillonBanking not reachable") })
	@Operation(summary = "Edit Or Delete KendraAssignment Record", description = "API to Edit Or Delete KendraAssignment Record in DB")
	@PostMapping(value = "/editOrDeleteKendraAssignmentRecord", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<ResponseWrapper> modifyKendraAssignmentRecord(@RequestBody EditOrDeleteKendraAssignmentRequestWrapper requestWrapper,
			@RequestHeader(defaultValue = "APZRMB") String appId,
			@RequestHeader(defaultValue = "EditOrDeleteKendraAssignmentRecord") String interfaceId,
			@RequestHeader(defaultValue = "000000000002") String userId,
			@RequestHeader(defaultValue = "12345678") String masterTxnRefNo,
			@RequestHeader(defaultValue = "abcd1234efgh5678") String deviceId) {

		ResponseWrapper responseWrapper = new ResponseWrapper();
		logger.debug("EditOrDeleteKendraAssignmentRecord request data :: {}", requestWrapper);
		Header header = CommonUtils.obtainHeader(appId, interfaceId, userId, masterTxnRefNo, deviceId);
		logger.debug("EditOrDeleteKendraAssignmentRecord Header value :: {}", header);
		EditOrDeleteKendraAssignmentRequest apiRequest = requestWrapper.getApiRequest();
		logger.debug("APIRequest :: {}", apiRequest);

		Response response = kendraAssignmentService.modifyKendraAssignemetRecordService(apiRequest);
		responseWrapper.setApiResponse(response);
		logger.debug("End : EditOrDeleteKendraAssignmentRecord response :: {}", response);
		return new ResponseEntity<>(responseWrapper, HttpStatus.OK);
	}
}
