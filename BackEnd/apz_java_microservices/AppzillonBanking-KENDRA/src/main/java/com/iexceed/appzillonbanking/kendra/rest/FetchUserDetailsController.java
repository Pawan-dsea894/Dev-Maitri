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
import com.iexceed.appzillonbanking.kendra.domain.cus.TbAsmiUser;
import com.iexceed.appzillonbanking.kendra.payload.AsmiUserDetailsResponse;
import com.iexceed.appzillonbanking.kendra.payload.FetchBranchIdDetails;
import com.iexceed.appzillonbanking.kendra.payload.FetchUserData;
import com.iexceed.appzillonbanking.kendra.payload.UserDetailsDataRequest;
import com.iexceed.appzillonbanking.kendra.payload.UserDetailsDataRequestWrapper;
import com.iexceed.appzillonbanking.kendra.payload.UserDetailsFetchRequest;
import com.iexceed.appzillonbanking.kendra.payload.UserDetailsFetchRequestWrapper;
import com.iexceed.appzillonbanking.kendra.payload.UserDetailsRequest;
import com.iexceed.appzillonbanking.kendra.payload.UserDetailsRequestWrapper;
import com.iexceed.appzillonbanking.kendra.service.FetchUserDetailsService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@Tag(description = "application/userManagement", name = "USERMANAGEMENT")
@RequestMapping("application/userManagement")
public class FetchUserDetailsController {

	private static final Logger logger = LogManager.getLogger(FetchUserDetailsController.class);

	@Autowired
	private FetchUserDetailsService fetchUserDetailsService;

	/**
	 * Rest API Service to fetch User record in DB
	 *
	 */
	@ApiResponses({ @ApiResponse(responseCode = "200", description = "AppzillonBanking API reachable"),
			@ApiResponse(responseCode = "408", description = "Service Timed Out"),
			@ApiResponse(responseCode = "500", description = "Internal Server Error"),
			@ApiResponse(responseCode = "404", description = "AppzillonBanking not reachable") })
	@Operation(summary = "Fetch User Details", description = "API to Fetch User Record in DB")
	@PostMapping(value = "/fetchUserDetails", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<ResponseWrapper> insertKendraAssignmentRecord(
			@RequestBody UserDetailsFetchRequestWrapper requestWrapper,
			@RequestHeader(defaultValue = "APZRMB") String appId,
			@RequestHeader(defaultValue = "FetchUserDetails") String interfaceId,
			@RequestHeader(defaultValue = "000000000002") String userId,
			@RequestHeader(defaultValue = "12345678") String masterTxnRefNo,
			@RequestHeader(defaultValue = "abcd1234efgh5678") String deviceId) {

		ResponseWrapper responseWrapper = new ResponseWrapper();
		logger.debug("FetchUserDetails request data :: {}", requestWrapper);
		Header header = CommonUtils.obtainHeader(appId, interfaceId, userId, masterTxnRefNo, deviceId);
		logger.debug("FetchUserDetails Header value :: {}", header);
		UserDetailsFetchRequest apiRequest = requestWrapper.getApiRequest();
		logger.debug("APIRequest :: {}", apiRequest);

		Response response = fetchUserDetailsService.fetchUserDetailRecord(apiRequest);
		responseWrapper.setApiResponse(response);
		logger.debug("End : FetchUserDetails response :: {}", response);
		return new ResponseEntity<>(responseWrapper, HttpStatus.OK);
	}
	
	
	/**
	 * Rest API Service to fetch User Role record in DB
	 *
	 */
	@ApiResponses({ @ApiResponse(responseCode = "200", description = "AppzillonBanking API reachable"),
			@ApiResponse(responseCode = "408", description = "Service Timed Out"),
			@ApiResponse(responseCode = "500", description = "Internal Server Error"),
			@ApiResponse(responseCode = "404", description = "AppzillonBanking not reachable") })
	@Operation(summary = "Fetch User Role", description = "API to Fetch User Role Record in DB")
	@PostMapping(value = "/fetchUserRole", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<ResponseWrapper> FetchUserRoleRecord(
			@RequestBody UserDetailsFetchRequestWrapper requestWrapper,
			@RequestHeader(defaultValue = "APZRMB") String appId,
			@RequestHeader(defaultValue = "FetchUserRoleDetails") String interfaceId,
			@RequestHeader(defaultValue = "000000000002") String userId,
			@RequestHeader(defaultValue = "12345678") String masterTxnRefNo,
			@RequestHeader(defaultValue = "abcd1234efgh5678") String deviceId) {

		ResponseWrapper responseWrapper = new ResponseWrapper();
		logger.debug("FetchUserRoleDetails request data :: {}", requestWrapper);
		Header header = CommonUtils.obtainHeader(appId, interfaceId, userId, masterTxnRefNo, deviceId);
		logger.debug("FetchUserRoleDetails Header value :: {}", header);
		UserDetailsFetchRequest apiRequest = requestWrapper.getApiRequest();
		logger.debug("APIRequest :: {}", apiRequest);

		Response response = fetchUserDetailsService.fetchUserRoleDetailRecords(apiRequest);
		responseWrapper.setApiResponse(response);
		logger.debug("End : FetchUserRoleDetails response :: {}", response);
		return new ResponseEntity<>(responseWrapper, HttpStatus.OK);
	}
	
	@ApiResponses({ @ApiResponse(responseCode = "200", description = "AppzillonBanking API reachable"),
			@ApiResponse(responseCode = "408", description = "Service Timed Out"),
			@ApiResponse(responseCode = "500", description = "Internal Server Error"),
			@ApiResponse(responseCode = "404", description = "AppzillonBanking not reachable") })
	@Operation(summary = "Fetch Branch Id", description = "API to Fetch Branch Id Record in DB")
	@PostMapping(value = "/fetchBranchId", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	public String FetchBranchId(@RequestBody FetchBranchIdDetails fetchBranchIdDetails) {
		return fetchUserDetailsService.fetchBranchId(fetchBranchIdDetails);
	}
	
	
	@ApiResponses({ @ApiResponse(responseCode = "200", description = "AppzillonBanking API reachable"),
			@ApiResponse(responseCode = "408", description = "Service Timed Out"),
			@ApiResponse(responseCode = "500", description = "Internal Server Error"),
			@ApiResponse(responseCode = "404", description = "AppzillonBanking not reachable") })
	@Operation(summary = "fetch User List", description = "API to Fetch  Details Record in DB")
	@PostMapping(value = "/fetchUserList", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<ResponseWrapper> fetchUserDetailsOnBranchId(
			@RequestBody UserDetailsRequestWrapper requestWrapper, @RequestHeader(defaultValue = "APZRMB") String appId,
			@RequestHeader(defaultValue = "FetchDetails") String interfaceId,
			@RequestHeader(defaultValue = "000000000002") String userId,
			@RequestHeader(defaultValue = "12345678") String masterTxnRefNo,
			@RequestHeader(defaultValue = "abcd1234efgh5678") String deviceId) {
		ResponseWrapper responseWrapper = new ResponseWrapper();
		logger.debug("FetchDetails request data :: {}", requestWrapper);
		Header header = CommonUtils.obtainHeader(appId, interfaceId, userId, masterTxnRefNo, deviceId);
		logger.debug("FetchDetails Header value :: {}", header);
		UserDetailsRequest apiRequest = requestWrapper.getApiRequest();
		logger.debug("APIRequest :: {}", apiRequest);
		Response response = fetchUserDetailsService.fetchUserDetailsOnBranchId(apiRequest);
		responseWrapper.setApiResponse(response);
		logger.debug("End : FetchDetails response :: {}", response);
		return new ResponseEntity<>(responseWrapper, HttpStatus.OK);
	}
	
	@PostMapping(value = "/fetchUserData", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<AsmiUserDetailsResponse> fetchUserDetails(@RequestBody FetchUserData fetchUserData) {
		logger.debug("Received userIdfromCDH: {}", fetchUserData);
		AsmiUserDetailsResponse userDetails = fetchUserDetailsService.fetchUserDetailsData1(fetchUserData);
		logger.debug("Received userDetails: {}", userDetails);
		if (userDetails != null) {
			return ResponseEntity.ok(userDetails);
		} else {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
		}
	}

}
