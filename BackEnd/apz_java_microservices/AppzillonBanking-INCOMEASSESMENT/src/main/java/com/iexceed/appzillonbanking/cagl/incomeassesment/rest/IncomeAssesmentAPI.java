package com.iexceed.appzillonbanking.cagl.incomeassesment.rest;


import com.iexceed.appzillonbanking.cagl.incomeassesment.payload.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.iexceed.appzillonbanking.cagl.incomeassesment.service.IncomeAssesmentService;
import com.iexceed.appzillonbanking.core.payload.Header;
import com.iexceed.appzillonbanking.core.payload.Response;
import com.iexceed.appzillonbanking.core.payload.ResponseWrapper;
import com.iexceed.appzillonbanking.core.utils.CommonUtils;
import com.iexceed.appzillonbanking.interfaceAdapter.utils.AdapterUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import reactor.core.publisher.Mono;

@RestController
@Tag(description = "application/incomeassessment", name = "INCOMEASSESMENT")
@RequestMapping("application/incomeassessment")
public class IncomeAssesmentAPI {

	private static final Logger logger = LogManager.getLogger(IncomeAssesmentAPI.class);

	@Autowired
	private IncomeAssesmentService incomeAssesmentService;

	@Autowired
	private AdapterUtil adapterUtil;

	@ApiResponses({ @ApiResponse(responseCode = "200", description = "AppzillonBanking API reachable"),
			@ApiResponse(responseCode = "408", description = "Service Timed Out"),
			@ApiResponse(responseCode = "500", description = "Internal Server Error"),
			@ApiResponse(responseCode = "404", description = "AppzillonBanking not reachable") })

	@Operation(summary = "Create Application", description = "API to Create Application")
	@PostMapping(value = "/save", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<ResponseWrapper> saveApplication(@RequestBody CreateAppRequestWrapper requestWrapper,
			@RequestHeader String appId, @RequestHeader String interfaceId, @RequestHeader String userId,
			@RequestHeader String masterTxnRefNo, @RequestHeader String deviceId) {

		ResponseWrapper responseWrapper = new ResponseWrapper();
		logger.debug("createApplication request data :: {}", requestWrapper);
		Header header = CommonUtils.obtainHeader(appId, interfaceId, userId, masterTxnRefNo, deviceId);
		logger.debug("createApplication Header value :: {}", header);

		Response response = incomeAssesmentService.saveApplication(requestWrapper.getApiRequest());
		responseWrapper.setApiResponse(response);
		logger.debug("End : createApplication response :: {}", response);
		return new ResponseEntity<>(responseWrapper, HttpStatus.OK);
	}

	@ApiResponses({ @ApiResponse(responseCode = "200", description = "AppzillonBanking API reachable"),
			@ApiResponse(responseCode = "408", description = "Service Timed Out"),
			@ApiResponse(responseCode = "500", description = "Internal Server Error"),
			@ApiResponse(responseCode = "404", description = "AppzillonBanking not reachable") })
	@Operation(summary = "update Application status", description = "API to update Application status")
	@PostMapping(value = "/update", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<ResponseWrapper> updateStatus(
			@RequestBody CreateAppRequestWrapper requestWrapper, @RequestHeader String appId,
			@RequestHeader(defaultValue = "updateIncomeAssessment") String interfaceId, @RequestHeader String userId,
			@RequestHeader String masterTxnRefNo, @RequestHeader String deviceId) {

		ResponseWrapper responseWrapper = new ResponseWrapper();
		logger.debug("updateStatus request data :: {}", requestWrapper);
		Header header = CommonUtils.obtainHeader(appId, interfaceId, userId, masterTxnRefNo, deviceId);
		logger.debug("updateStatus Header value :: {}", header);
		Response response = incomeAssesmentService.updateStatus(requestWrapper.getApiRequest(), header);
		responseWrapper.setApiResponse(response);
		logger.debug("End : updateStatus response :: {}", response);
		return new ResponseEntity<>(responseWrapper, HttpStatus.OK);
	}

	@ApiResponses({ @ApiResponse(responseCode = "200", description = "AppzillonBanking API reachable"),
			@ApiResponse(responseCode = "408", description = "Service Timed Out"),
			@ApiResponse(responseCode = "500", description = "Internal Server Error"),
			@ApiResponse(responseCode = "404", description = "AppzillonBanking not reachable") })
	@Operation(summary = "approve Application request", description = "API to approve the request")
	@PostMapping(value = "/approve", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	public Mono<ResponseEntity<ResponseWrapper>> approve(@RequestBody ApproveRequestWrapper requestWrapper,
			@RequestHeader String appId, @RequestHeader(defaultValue = "updateIncomeAssessment") String interfaceId,
			@RequestHeader String userId, @RequestHeader String masterTxnRefNo, @RequestHeader String deviceId) {

		logger.debug("reqWrapper:{}", requestWrapper);
		ApproveRequest req = requestWrapper.getApiRequest();
		Header header = CommonUtils.obtainHeader(appId, interfaceId, userId, masterTxnRefNo, deviceId);
		Mono<Object> response = incomeAssesmentService.approve(req, header);
		return adapterUtil.generateResponseWrapper(response, req.getInterfaceName(), header, true);
	}
	
	@ApiResponses({ @ApiResponse(responseCode = "200", description = "AppzillonBanking API reachable"),
			@ApiResponse(responseCode = "408", description = "Service Timed Out"),
			@ApiResponse(responseCode = "500", description = "Internal Server Error"),
			@ApiResponse(responseCode = "404", description = "AppzillonBanking not reachable") })
	@Operation(summary = "reject Application request", description = "API to reject the request")
	@PostMapping(value = "/reject", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	public Mono<ResponseEntity<ResponseWrapper>> reject(@RequestBody ApproveRequestWrapper requestWrapper,
			@RequestHeader String appId, @RequestHeader(defaultValue = "updateIncomeAssessment") String interfaceId,
			@RequestHeader String userId, @RequestHeader String masterTxnRefNo, @RequestHeader String deviceId) {

		logger.debug("reqWrapper:{}", requestWrapper);
		ApproveRequest req = requestWrapper.getApiRequest();
		Header header = CommonUtils.obtainHeader(appId, interfaceId, userId, masterTxnRefNo, deviceId);
		Mono<Object> response = incomeAssesmentService.reject(req, header);
		return adapterUtil.generateResponseWrapper(response, req.getInterfaceName(), header, true);
	}

	@ApiResponses({ @ApiResponse(responseCode = "200", description = "AppzillonBanking API reachable"),
			@ApiResponse(responseCode = "408", description = "Service Timed Out"),
			@ApiResponse(responseCode = "500", description = "Internal Server Error"),
			@ApiResponse(responseCode = "404", description = "AppzillonBanking not reachable") })
	@Operation(summary = "fetch Application", description = "API to fetch application")
	@PostMapping(value = "/fetchApplication", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<ResponseWrapper> fetchApplication(@RequestBody FetchAppRequestWrapper requestWrapper,
			@RequestHeader String appId, @RequestHeader String interfaceId, @RequestHeader String userId,
			@RequestHeader String masterTxnRefNo, @RequestHeader String deviceId) {

		ResponseWrapper responseWrapper = new ResponseWrapper();

		logger.debug("reqWrapper:{}", requestWrapper);
		Response response = incomeAssesmentService.fetchApplication(requestWrapper.getApiRequest());
		responseWrapper.setApiResponse(response);
		logger.debug("End : createApplication response :: {}", response);
		return new ResponseEntity<>(responseWrapper, HttpStatus.OK);
	}
	
	@ApiResponses({ @ApiResponse(responseCode = "200", description = "AppzillonBanking API reachable"),
		@ApiResponse(responseCode = "408", description = "Service Timed Out"),
		@ApiResponse(responseCode = "500", description = "Internal Server Error"),
		@ApiResponse(responseCode = "404", description = "AppzillonBanking not reachable") })
@Operation(summary = "fetch Application", description = "API to fetch application")
@PostMapping(value = "/fetchApplication1", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
public ResponseEntity<ResponseWrapper> fetchApplication1(@RequestBody FetchAppRequestWrapper requestWrapper,
		@RequestHeader String appId, @RequestHeader String interfaceId, @RequestHeader String userId,
		@RequestHeader String masterTxnRefNo, @RequestHeader String deviceId) {

	ResponseWrapper responseWrapper = new ResponseWrapper();

	logger.debug("reqWrapper:{}", requestWrapper);
	Response response = incomeAssesmentService.fetchApplication1(requestWrapper.getApiRequest());
	responseWrapper.setApiResponse(response);
	logger.debug("End : createApplication response :: {}", response);
	return new ResponseEntity<>(responseWrapper, HttpStatus.OK);
}

	@ApiResponses({ @ApiResponse(responseCode = "200", description = "AppzillonBanking API reachable"),
		@ApiResponse(responseCode = "408", description = "Service Timed Out"),
		@ApiResponse(responseCode = "500", description = "Internal Server Error"),
		@ApiResponse(responseCode = "404", description = "AppzillonBanking not reachable") })
@Operation(summary = "fetchincomeDetails", description = "API to fetch application")
@PostMapping(value = "/fetchincomeDetails", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
public ResponseEntity<ResponseWrapper> fetchincomeDetails(@RequestBody String requestString,
		@RequestHeader String appId, @RequestHeader String interfaceId, @RequestHeader String userId,
		@RequestHeader String masterTxnRefNo, @RequestHeader String deviceId) {

	ResponseWrapper responseWrapper = new ResponseWrapper();

	logger.debug("reqWrapper:{}", requestString);
	Response response = incomeAssesmentService.fetchincomeDetails(new JSONObject(requestString));
	responseWrapper.setApiResponse(response);
	logger.debug("End : createApplication response :: {}", response);
	return new ResponseEntity<>(responseWrapper, HttpStatus.OK);
}
	
	
	@ApiResponses({ @ApiResponse(responseCode = "200", description = "AppzillonBanking API reachable"),
		@ApiResponse(responseCode = "408", description = "Service Timed Out"),
		@ApiResponse(responseCode = "500", description = "Internal Server Error"),
		@ApiResponse(responseCode = "404", description = "AppzillonBanking not reachable") })
@Operation(summary = "delete Application", description = "API to delete application")
@PostMapping(value = "/deleteApplication", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
public ResponseEntity<ResponseWrapper> deleteApplication(@RequestBody DeleteApplicatioRequestWrapper requestWrapper,
		@RequestHeader(defaultValue = "APZRMB") String appId,
		@RequestHeader(defaultValue = "DeleteApplication") String interfaceId,
		@RequestHeader(defaultValue = "000000000002") String userId,
		@RequestHeader(defaultValue = "12345678") String masterTxnRefNo,
		@RequestHeader(defaultValue = "abcd1234efgh5678") String deviceId) {

	ResponseWrapper responseWrapper = new ResponseWrapper();

	logger.debug("reqWrapper:{}", requestWrapper);
	Response response = incomeAssesmentService.deleteApplication(requestWrapper.getApiRequest());
	responseWrapper.setApiResponse(response);
	logger.debug("End : delete Application response :: {}", response);
	return new ResponseEntity<>(responseWrapper, HttpStatus.OK);
}
	
	/**
	 * Rest API Service to Update Earning Member
	 * 
	 * @author akshay.shahane
	 * @since 03.10.2024
	 * @return Object which will give message either Earning member is updated or not
	 */
	@ApiResponses({ @ApiResponse(responseCode = "200", description = "AppzillonBanking API reachable"),
			@ApiResponse(responseCode = "408", description = "Service Timed Out"),
			@ApiResponse(responseCode = "500", description = "Internal Server Error"),
			@ApiResponse(responseCode = "404", description = "AppzillonBanking not reachable") })
	@Operation(summary = "update EarningMember", description = "API to update Application status")
	@PostMapping(value = "/updateEarningMember", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<ResponseWrapper> updateEarningMember(@RequestBody UpdateEarningRequestWrapper requestWrapper,
			@RequestHeader(defaultValue = "APZRMB") String appId,
			@RequestHeader(defaultValue = "DeleteApplication") String interfaceId,
			@RequestHeader(defaultValue = "000000000002") String userId,
			@RequestHeader(defaultValue = "12345678") String masterTxnRefNo,
			@RequestHeader(defaultValue = "abcd1234efgh5678") String deviceId) {

		ResponseWrapper responseWrapper = new ResponseWrapper();
		logger.debug("updateEarningMember request data :: {}", requestWrapper);
		Header header = CommonUtils.obtainHeader(appId, interfaceId, userId, masterTxnRefNo, deviceId);
		logger.debug("updateEarningMember Header value :: {}", header);
		Response response = incomeAssesmentService.callupdateEarningMemberService(requestWrapper.getApiRequest(), header);
		responseWrapper.setApiResponse(response);
		logger.debug("End : updateEarningMember response :: {}", response);
		return new ResponseEntity<>(responseWrapper, HttpStatus.OK);
	}
	
	
	
	@ApiResponses({ @ApiResponse(responseCode = "200", description = "AppzillonBanking API reachable"),
			@ApiResponse(responseCode = "408", description = "Service Timed Out"),
			@ApiResponse(responseCode = "500", description = "Internal Server Error"),
			@ApiResponse(responseCode = "404", description = "AppzillonBanking not reachable") })
	@Operation(summary = "Fetch Data for CRT records from DB", description = "API to Fetch Data in DB ")
	@PostMapping(value = "/fetchCRTApplication", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<ResponseWrapper> fetchRecordsForCRT(
			@RequestBody FetchDataRequestWrapper fetchDataRequestWrapper,
			@RequestHeader(defaultValue = "APZRMB") String appId,
			@RequestHeader(defaultValue = "FetchData") String interfaceId,
			@RequestHeader(defaultValue = "000000000002") String userId,
			@RequestHeader(defaultValue = "12345678") String masterTxnRefNo,
			@RequestHeader(defaultValue = "abcd1234efgh5678") String deviceId) {
		ResponseWrapper responseWrapper = new ResponseWrapper();
		Response response = new Response();
		logger.debug("fetchData request data :: {}", fetchDataRequestWrapper);
		Header header = CommonUtils.obtainHeader(appId, interfaceId, userId, masterTxnRefNo, deviceId);
		logger.debug("fetchData Header value :: {}", header);
		try {
			response = incomeAssesmentService.fetchDataForCRTService(fetchDataRequestWrapper.getApiRequest());
			logger.debug("Final response :: {}", response);
		} catch (Exception e) {
			logger.error("Error Occured in fetchData:{}", e);
		}
		responseWrapper.setApiResponse(response);
		logger.debug("End : fetchData response :: {}", response);
		return new ResponseEntity<>(responseWrapper, HttpStatus.OK);
	}

	@ApiResponses({ @ApiResponse(responseCode = "200", description = "AppzillonBanking API reachable"),
			@ApiResponse(responseCode = "408", description = "Service Timed Out"),
			@ApiResponse(responseCode = "500", description = "Internal Server Error"),
			@ApiResponse(responseCode = "404", description = "AppzillonBanking not reachable") })
	@Operation(summary = "CreateAndUpdate Questions", description = "API to Add Questionary")
	@PostMapping(value = "/addquestionary", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<ResponseWrapper> addQuestionary(@RequestBody CreateIncomeRequestWrapper requestWrapper) {
		ResponseWrapper responseWrapper = new ResponseWrapper();
		Response response = new Response();
		logger.debug("addQuestionary request data :: {}", requestWrapper);
		try {
			response = incomeAssesmentService.saveQuestionary(requestWrapper.getApiRequest());
		} catch (Exception e) {
			logger.error("Error Occured in addQuestionary:{}", e);
		}
		responseWrapper.setApiResponse(response);
		logger.debug("End : addQuestionary response :: {}", response);
		return new ResponseEntity<>(responseWrapper, HttpStatus.OK);
	}

}
