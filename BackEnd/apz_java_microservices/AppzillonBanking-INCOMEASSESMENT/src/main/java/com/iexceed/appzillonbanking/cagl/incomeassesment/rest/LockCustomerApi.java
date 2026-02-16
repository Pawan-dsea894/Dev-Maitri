package com.iexceed.appzillonbanking.cagl.incomeassesment.rest;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.iexceed.appzillonbanking.cagl.incomeassesment.payload.LockCustomerRequestWrapper;
import com.iexceed.appzillonbanking.cagl.incomeassesment.service.LockCustomerService;
import com.iexceed.appzillonbanking.core.payload.Header;
import com.iexceed.appzillonbanking.core.payload.Response;
import com.iexceed.appzillonbanking.core.payload.ResponseWrapper;
import com.iexceed.appzillonbanking.core.utils.CommonUtils;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@Tag(description = "application/incomeassessment", name = "INCOMEASSESMENT")
@RequestMapping("application/incomeassessment")

public class LockCustomerApi {
	private static final Logger logger = LogManager.getLogger(LockCustomerApi.class);

	@Autowired
	private LockCustomerService lockCustomerService;

	@ApiResponses({ @ApiResponse(responseCode = "200", description = "AppzillonBanking API reachable"),
			@ApiResponse(responseCode = "408", description = "Service Timed Out"),
			@ApiResponse(responseCode = "500", description = "Internal Server Error"),
			@ApiResponse(responseCode = "404", description = "AppzillonBanking not reachable") })

	@Operation(summary = "Create Application", description = "API to Create Lock Customer Application")
	@PostMapping(value = "/saveLockCustomer", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<ResponseWrapper> saveApplication(@RequestBody LockCustomerRequestWrapper requestWrapper,
			@RequestHeader String appId, @RequestHeader String interfaceId, @RequestHeader String userId,
			@RequestHeader String masterTxnRefNo, @RequestHeader String deviceId) {

		ResponseWrapper responseWrapper = new ResponseWrapper();
		logger.debug("createApplication request data :: {}", requestWrapper);
		Header header = CommonUtils.obtainHeader(appId, interfaceId, userId, masterTxnRefNo, deviceId);
		logger.debug("createApplication Header value :: {}", header);

		Response response = lockCustomerService.saveApplication(requestWrapper.getApiRequest());
		responseWrapper.setApiResponse(response);
		logger.debug("End : createApplication response :: {}", response);
		return new ResponseEntity<>(responseWrapper, HttpStatus.OK);
	}

	@ApiResponses({ @ApiResponse(responseCode = "200", description = "AppzillonBanking API reachable"),
			@ApiResponse(responseCode = "408", description = "Service Timed Out"),
			@ApiResponse(responseCode = "500", description = "Internal Server Error"),
			@ApiResponse(responseCode = "404", description = "AppzillonBanking not reachable") })
	@Operation(summary = "delete Application", description = "API to delete Lock Customer application")
	@DeleteMapping(value = "/deleteApplicationLockCustomer", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<ResponseWrapper> deleteApplication(@RequestBody String applicationId,
			@RequestHeader(defaultValue = "APZRMB") String appId,
			@RequestHeader(defaultValue = "DeleteApplication") String interfaceId,
			@RequestHeader(defaultValue = "000000000002") String userId,
			@RequestHeader(defaultValue = "12345678") String masterTxnRefNo,
			@RequestHeader(defaultValue = "abcd1234efgh5678") String deviceId) {

		ResponseWrapper responseWrapper = new ResponseWrapper();

		logger.debug("reqWrapper:{}",  applicationId);
		Response response = lockCustomerService.deleteApplicationLockCustomer(applicationId);
		responseWrapper.setApiResponse(response);
		logger.debug("End : delete Application response :: {}", response);
		return new ResponseEntity<>(responseWrapper, HttpStatus.OK);
	}

	@ApiResponses({ @ApiResponse(responseCode = "200", description = "AppzillonBanking API reachable"),
			@ApiResponse(responseCode = "408", description = "Service Timed Out"),
			@ApiResponse(responseCode = "500", description = "Internal Server Error"),
			@ApiResponse(responseCode = "404", description = "AppzillonBanking not reachable") })
	@Operation(summary = "Fetch list of Lock Customer", description = "API to fetch application master")
	@GetMapping(value = "/fetchApplicationLockCustomer/applicationId", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<ResponseWrapper> fetchApplicationList(@PathVariable String applicationId) {
		ResponseWrapper responseWrapper = new ResponseWrapper();
		Response response = lockCustomerService.fetchApplicationLockCustomer(applicationId);
		responseWrapper.setApiResponse(response);
		logger.warn("End : fetchApplicationList method response is:: {}", responseWrapper);
		return new ResponseEntity<>(responseWrapper, HttpStatus.OK);
	}

}
