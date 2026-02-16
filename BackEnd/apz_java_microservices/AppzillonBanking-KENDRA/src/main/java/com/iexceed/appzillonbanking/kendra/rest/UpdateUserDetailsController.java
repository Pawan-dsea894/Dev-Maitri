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
import com.iexceed.appzillonbanking.kendra.payload.UserDetailsUpdateRequest;
import com.iexceed.appzillonbanking.kendra.payload.UserDetailsUpdateRequestWrapper;
import com.iexceed.appzillonbanking.kendra.service.UpdateUserDetailsService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@Tag(description = "application/userManagement", name = "USERMANAGEMENT")
@RequestMapping("application/userManagement")
public class UpdateUserDetailsController {

	private static final Logger logger = LogManager.getLogger(UpdateUserDetailsController.class);

	@Autowired
	private UpdateUserDetailsService updateUserDetailsService;

	/**
	 * Rest API Service to Update User record in DB
	 *
	 */
	@ApiResponses({ @ApiResponse(responseCode = "200", description = "AppzillonBanking API reachable"),
			@ApiResponse(responseCode = "408", description = "Service Timed Out"),
			@ApiResponse(responseCode = "500", description = "Internal Server Error"),
			@ApiResponse(responseCode = "404", description = "AppzillonBanking not reachable") })
	@Operation(summary = "Update User Details", description = "API to Update User Record in DB")
	@PostMapping(value = "/updateUserDetails", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<ResponseWrapper> UpdateUserRecord(@RequestBody UserDetailsUpdateRequestWrapper requestWrapper,
			@RequestHeader(defaultValue = "APZRMB") String appId,
			@RequestHeader(defaultValue = "FetchUserDetails") String interfaceId,
			@RequestHeader(defaultValue = "000000000002") String userId,
			@RequestHeader(defaultValue = "12345678") String masterTxnRefNo,
			@RequestHeader(defaultValue = "abcd1234efgh5678") String deviceId) {

		ResponseWrapper responseWrapper = new ResponseWrapper();
		logger.debug("UpdateUserDetails request data :: {}", requestWrapper);
		Header header = CommonUtils.obtainHeader(appId, interfaceId, userId, masterTxnRefNo, deviceId);
		logger.debug("UpdateUserDetails Header value :: {}", header);
		UserDetailsUpdateRequest apiRequest = requestWrapper.getApiRequest();
		logger.debug("APIRequest :: {}", apiRequest);

		Response response = updateUserDetailsService.updateUserDetailRecord(apiRequest);
		responseWrapper.setApiResponse(response);
		logger.debug("End : UpdateUserDetails response :: {}", response);
		return new ResponseEntity<>(responseWrapper, HttpStatus.OK);
	}
}
