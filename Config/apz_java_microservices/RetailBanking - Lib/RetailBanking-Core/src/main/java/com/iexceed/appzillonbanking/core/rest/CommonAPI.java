package com.iexceed.appzillonbanking.core.rest;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.iexceed.appzillonbanking.core.payload.LovMaintenanceRequestWrapper;
import com.iexceed.appzillonbanking.core.payload.Response;
import com.iexceed.appzillonbanking.core.payload.ResponseWrapper;
import com.iexceed.appzillonbanking.core.service.CommonService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/common")
@Tag(name = "core", description = "/common")

public class CommonAPI {

	private static final Logger logger = LogManager.getLogger(CommonAPI.class);

	@Autowired
	private CommonService commonService;

	@ApiResponses({ @ApiResponse(responseCode = "200", description = "AppzillonBanking API reachable"),
			@ApiResponse(responseCode = "408", description = "Service Timed Out"),
			@ApiResponse(responseCode = "500", description = "Internal Server Error"),
			@ApiResponse(responseCode = "404", description = "AppzillonBanking not reachable") })
	@Operation(summary = "Fetch lov value based on key", description = "API to Fetch lov value based on key")
	@GetMapping(value = "/v1/fetchlov/{lovName}/{language}", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<ResponseWrapper> fetchLovDetails(@PathVariable String lovName,

			@PathVariable String language) {

		logger.warn("start fetchLovDetails :: {}", lovName);
		ResponseWrapper lovDetailsResponseWrapper = new ResponseWrapper();

		logger.warn("Start : Fetch LovDetails with request :: {}", language);

		Response lovDetailsResponse = commonService.fetchLovDetails(lovName, language);

		lovDetailsResponseWrapper.setApiResponse(lovDetailsResponse);

		return new ResponseEntity<>(lovDetailsResponseWrapper, HttpStatus.OK);

	}

	@ApiResponses({ @ApiResponse(responseCode = "200", description = "AppzillonBanking API reachable"),
			@ApiResponse(responseCode = "408", description = "Service Timed Out"),
			@ApiResponse(responseCode = "500", description = "Internal Server Error"),
			@ApiResponse(responseCode = "404", description = "AppzillonBanking not reachable") })
	@Operation(summary = "Fetch lov name and language", description = "API to Fetch lov name and language based on appId")
	@GetMapping(value = "/v1/fetchlovname/{appId}", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<ResponseWrapper> fetchlovname(@PathVariable String appId) {

		logger.warn("start fetchlovname :: ");
		ResponseWrapper lovDetailsResponseWrapper = new ResponseWrapper();

		Response lovDetailsResponse = commonService.fetchLovName(appId);

		lovDetailsResponseWrapper.setApiResponse(lovDetailsResponse);

		return new ResponseEntity<>(lovDetailsResponseWrapper, HttpStatus.OK);

	}

	@ApiResponses({ @ApiResponse(responseCode = "200", description = "AppzillonBanking API reachable"),
			@ApiResponse(responseCode = "408", description = "Service Timed Out"),
			@ApiResponse(responseCode = "500", description = "Internal Server Error"),
			@ApiResponse(responseCode = "404", description = "AppzillonBanking not reachable") })
	@Operation(summary = "create lov", description = "API to Save/ update LOV")
	@PostMapping(value = "/v1/savelovdetails", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<ResponseWrapper> savelovdetails(@RequestBody LovMaintenanceRequestWrapper requestWrapper,
			@RequestHeader(defaultValue = "") String interfaceId, @RequestHeader(defaultValue = "admin") String userId,
			@RequestHeader(defaultValue = "admin") String appId,
			@RequestHeader(defaultValue = "") String masterTxnRefNo,
			@RequestHeader(defaultValue = "") String deviceId) {

		logger.warn("start savelovdetails :: {}", requestWrapper.getLovMaintenanceRequest());
		ResponseWrapper lovDetailsResponseWrapper = new ResponseWrapper();

		Response lovDetailsResponse = commonService.savelovdetails(requestWrapper.getLovMaintenanceRequest());

		lovDetailsResponseWrapper.setApiResponse(lovDetailsResponse);

		return new ResponseEntity<>(lovDetailsResponseWrapper, HttpStatus.OK);

	}

}
