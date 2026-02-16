package com.iexceed.appzillonbanking.cagl.rest;
import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.tools.ant.taskdefs.condition.Http;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.iexceed.appzillonbanking.cagl.loan.payload.ApplyAuditTrailRequestWrapper;
import com.iexceed.appzillonbanking.cagl.loan.payload.ApplyLoanRequestWrapper;
import com.iexceed.appzillonbanking.cagl.loan.payload.AuditTrailApplicationIdRequestWrapper;
import com.iexceed.appzillonbanking.cagl.loan.payload.CbCheckRequestWrapper;
import com.iexceed.appzillonbanking.cagl.loan.payload.CbRequest;
import com.iexceed.appzillonbanking.cagl.loan.payload.FetchAppRequest;
import com.iexceed.appzillonbanking.cagl.loan.payload.FetchAppRequestWrapper;
import com.iexceed.appzillonbanking.cagl.loan.payload.FetchCBReportRequest;
import com.iexceed.appzillonbanking.cagl.loan.payload.FetchCBReportRequestWrapper;
import com.iexceed.appzillonbanking.cagl.loan.payload.FetchEmiProductListWrapper;
import com.iexceed.appzillonbanking.cagl.loan.payload.FetchLoanRequestWrapper;
import com.iexceed.appzillonbanking.cagl.loan.payload.FetchLoanScheduleRequest;
import com.iexceed.appzillonbanking.cagl.loan.payload.FetchLoanScheduleRequestWrapper;
import com.iexceed.appzillonbanking.cagl.loan.payload.FetchLoanTargetWrapper;
import com.iexceed.appzillonbanking.cagl.loan.payload.FetchRoleRequestWrapper;
import com.iexceed.appzillonbanking.cagl.loan.payload.FetchStateRequestWrapper;
import com.iexceed.appzillonbanking.cagl.loan.payload.GeneratePassbookWrapper;
import com.iexceed.appzillonbanking.cagl.loan.payload.PopulateapplnWFRequestWrapper;
import com.iexceed.appzillonbanking.cagl.loan.payload.SanctionLoanScheduleRequest;
import com.iexceed.appzillonbanking.cagl.loan.payload.SanctionLoanScheduleRequestWrapper;
import com.iexceed.appzillonbanking.cagl.loan.payload.SanctionReportRequestWrapper;
import com.iexceed.appzillonbanking.cagl.loan.payload.UpdateCRTFlowWrapper;
import com.iexceed.appzillonbanking.cagl.loan.service.LoanService;
import com.iexceed.appzillonbanking.cagl.loan.service.ReportBuildService;
import com.iexceed.appzillonbanking.core.payload.Header;
import com.iexceed.appzillonbanking.core.payload.Response;
import com.iexceed.appzillonbanking.core.payload.ResponseBody;
import com.iexceed.appzillonbanking.core.payload.ResponseHeader;
import com.iexceed.appzillonbanking.core.payload.ResponseWrapper;
import com.iexceed.appzillonbanking.core.utils.CommonUtils;
import com.iexceed.appzillonbanking.interfaceAdapter.utils.AdapterUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import reactor.core.publisher.Mono;

@RestController
@Tag(description = "application/loan", name = "LOAN")
@RequestMapping("application/loan")
public class LoanAPI {

	private static final Logger logger = LogManager.getLogger(LoanAPI.class);

	@Autowired
	private LoanService loanService;

	@Autowired
	private ReportBuildService reportBuildService;

	@Autowired
	private AdapterUtil adapterUtil;

	/*
	 * @ApiResponses({ @ApiResponse(responseCode = "200", description =
	 * "AppzillonBanking API reachable"),
	 * 
	 * @ApiResponse(responseCode = "408", description = "Service Timed Out"),
	 * 
	 * @ApiResponse(responseCode = "500", description = "Internal Server Error"),
	 * 
	 * @ApiResponse(responseCode = "404", description =
	 * "AppzillonBanking not reachable") })
	 * 
	 * @Operation(summary = "Create Application", description =
	 * "API to Create Application")
	 * 
	 * @PostMapping(value = "/create", consumes = MediaType.APPLICATION_JSON_VALUE,
	 * produces = MediaType.APPLICATION_JSON_VALUE) public
	 * ResponseEntity<ResponseWrapper> createApplication(@RequestBody
	 * CreateAppRequestWrapper requestWrapper,
	 * 
	 * @RequestHeader String appId, @RequestHeader String
	 * interfaceId, @RequestHeader String userId,
	 * 
	 * @RequestHeader String masterTxnRefNo, @RequestHeader String deviceId) {
	 * 
	 * ResponseWrapper responseWrapper = new ResponseWrapper();
	 * logger.debug("createApplication request data :: {}", requestWrapper); Header
	 * header = CommonUtils.obtainHeader(appId, interfaceId, userId, masterTxnRefNo,
	 * deviceId); logger.debug("createApplication Header value :: {}", header);
	 * 
	 * Response response =
	 * loanService.createApplication(requestWrapper.getApiRequest());
	 * responseWrapper.setApiResponse(response);
	 * logger.debug("End : createApplication response :: {}", response); return new
	 * ResponseEntity<>(responseWrapper, HttpStatus.OK); }
	 */

	@ApiResponses({ @ApiResponse(responseCode = "200", description = "AppzillonBanking API reachable"),
			@ApiResponse(responseCode = "408", description = "Service Timed Out"),
			@ApiResponse(responseCode = "500", description = "Internal Server Error"),
			@ApiResponse(responseCode = "404", description = "AppzillonBanking not reachable") })
	@Operation(summary = "Save Loan Details", description = "API to Save Loan Details")
	@PostMapping(value = "/save", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<ResponseWrapper> applyLoan(@RequestBody ApplyLoanRequestWrapper requestWrapper,
			@RequestHeader String appId, @RequestHeader String interfaceId, @RequestHeader String userId,
			@RequestHeader String masterTxnRefNo, @RequestHeader String deviceId) {
		ResponseWrapper responseWrapper = new ResponseWrapper();
		logger.debug("saveLoanDetails request data :: {}", requestWrapper);
		Header header = CommonUtils.obtainHeader(appId, interfaceId, userId, masterTxnRefNo, deviceId);
		logger.debug("saveLoanDetails Header value :: {}", header);
		Response response = loanService.applyLoan(requestWrapper.getApiRequest());
		responseWrapper.setApiResponse(response);
		logger.debug("End : saveLoanDetails response :: {}", response);
		return new ResponseEntity<>(responseWrapper, HttpStatus.OK);
	}

	@ApiResponses({ @ApiResponse(responseCode = "200", description = "AppzillonBanking API reachable"),
			@ApiResponse(responseCode = "408", description = "Service Timed Out"),
			@ApiResponse(responseCode = "500", description = "Internal Server Error"),
			@ApiResponse(responseCode = "404", description = "AppzillonBanking not reachable") })
	@Operation(summary = "Check cb", description = "API for cbcheck")
	@PostMapping(value = "/cbcheck", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	public Mono<ResponseEntity<ResponseWrapper>> cbCheck(@RequestBody CbCheckRequestWrapper requestWrapper,
			@RequestHeader String appId, @RequestHeader String interfaceId, @RequestHeader String userId,
			@RequestHeader String masterTxnRefNo, @RequestHeader String deviceId) {

		String schedulerFlag = requestWrapper.getApiRequest().getRequestObj().getSchedulerEnabled();
		if (schedulerFlag != null && schedulerFlag.equalsIgnoreCase("Y")) {
			logger.debug("CB check API call for Scheduler started for the application ID "
					+ requestWrapper.getApiRequest().getRequestObj().getApplicationId());
		} else {
			logger.debug("CB check API call for Application started");
		}
		logger.warn("Start : cbCheck with request :: {}", requestWrapper);
		Header header = CommonUtils.obtainHeader(appId, interfaceId, userId, masterTxnRefNo, deviceId);
		logger.debug("cbCheck Header value :: {} ", header);

		CbRequest cbCheckRequest = requestWrapper.getApiRequest();
		Mono<Object> response = loanService.cbCheck(cbCheckRequest, header,schedulerFlag);
		return adapterUtil.generateResponseWrapper(response, cbCheckRequest.getInterfaceName(), header, true);
	}

	@ApiResponses({ @ApiResponse(responseCode = "200", description = "AppzillonBanking API reachable"),
			@ApiResponse(responseCode = "408", description = "Service Timed Out"),
			@ApiResponse(responseCode = "500", description = "Internal Server Error"),
			@ApiResponse(responseCode = "404", description = "AppzillonBanking not reachable") })
	@Operation(summary = "fetch application", description = "API for fetching application details")
	@PostMapping(value = "/fetchApplication", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<ResponseWrapper> fetchApplication(@RequestBody FetchAppRequestWrapper requestWrapper,
			@RequestHeader String appId, @RequestHeader String interfaceId, @RequestHeader String userId,
			@RequestHeader String masterTxnRefNo, @RequestHeader String deviceId) {

		ResponseWrapper responseWrapper = new ResponseWrapper();
		logger.warn("Start : fetchApplication with request :: {}", requestWrapper);
		Header header = CommonUtils.obtainHeader(appId, interfaceId, userId, masterTxnRefNo, deviceId);
		logger.debug("fetchApplication Header value :: {} ", header);

		FetchAppRequest fetchAppRequest = requestWrapper.getApiRequest();
		Response response = loanService.fetchApplication(fetchAppRequest);
		responseWrapper.setApiResponse(response);
		logger.debug("End : createApplication response :: {}", response);
		return new ResponseEntity<>(responseWrapper, HttpStatus.OK);
	}
	
	@ApiResponses({ @ApiResponse(responseCode = "200", description = "AppzillonBanking API reachable"),
			@ApiResponse(responseCode = "408", description = "Service Timed Out"),
			@ApiResponse(responseCode = "500", description = "Internal Server Error"),
			@ApiResponse(responseCode = "404", description = "AppzillonBanking not reachable") })
	@Operation(summary = "fetch application", description = "API for fetching application details including after otp drop  ")
	@PostMapping(value = "/fetchApplicationlistafterotpdrop", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<ResponseWrapper> fetchApplicationListAfterOTPdrop(@RequestBody FetchAppRequestWrapper requestWrapper,
			@RequestHeader String appId, @RequestHeader String interfaceId, @RequestHeader String userId,
			@RequestHeader String masterTxnRefNo, @RequestHeader String deviceId) {

		ResponseWrapper responseWrapper = new ResponseWrapper();
		logger.warn("Start : fetchApplicationListAfterOTPdrop with request :: {}", requestWrapper);
		Header header = CommonUtils.obtainHeader(appId, interfaceId, userId, masterTxnRefNo, deviceId);
		logger.debug("fetchApplicationListAfterOTPdrop Header value :: {} ", header);

		FetchAppRequest fetchAppRequest = requestWrapper.getApiRequest();
		Response response = loanService.fetchApplicationListAfterOTPdrop(fetchAppRequest);
		responseWrapper.setApiResponse(response);
		logger.debug("fetchApplicationListAfterOTPdrop End :  response :: {}", response);
		return new ResponseEntity<>(responseWrapper, HttpStatus.OK);
	}
		
		@ApiResponses({ @ApiResponse(responseCode = "200", description = "AppzillonBanking API reachable"),
			@ApiResponse(responseCode = "408", description = "Service Timed Out"),
			@ApiResponse(responseCode = "500", description = "Internal Server Error"),
			@ApiResponse(responseCode = "404", description = "AppzillonBanking not reachable") })
	@Operation(summary = "Populate application workflow", description = "API to populate application workflow")
	@PostMapping(value = "/populateapplnworkFlow", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<ResponseWrapper> populateapplnworkFlow(
			@RequestBody PopulateapplnWFRequestWrapper requestWrapper) {
		ResponseWrapper responseWrapper = new ResponseWrapper();
		Response response = loanService.populateApplnWorkFlow(requestWrapper.getApiRequest());
		loanService.updateStatusInMaster(requestWrapper.getApiRequest()); // If INITIATOR assigns and submits
																			// change the master status to PENDING
		responseWrapper.setApiResponse(response);
		logger.warn("End : populateapplnworkFlow method response is:: {}", responseWrapper);
		return new ResponseEntity<>(responseWrapper, HttpStatus.OK);
	}

	@ApiResponses({ @ApiResponse(responseCode = "200", description = "AppzillonBanking API reachable"),
			@ApiResponse(responseCode = "408", description = "Service Timed Out"),
			@ApiResponse(responseCode = "500", description = "Internal Server Error"),
			@ApiResponse(responseCode = "404", description = "AppzillonBanking not reachable") })
	@Operation(summary = "Fetch list of application master", description = "API to fetch application master")
	@GetMapping(value = "/fetchapplist/{kmId}", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<ResponseWrapper> fetchApplicationList(@PathVariable List<String> kmId) {
		ResponseWrapper responseWrapper = new ResponseWrapper();
		Response response = loanService.fetchApplicationList(kmId);
		responseWrapper.setApiResponse(response);
		logger.warn("End : fetchApplicationList method response is:: {}", responseWrapper);
		return new ResponseEntity<>(responseWrapper, HttpStatus.OK);
	}

	
	@ApiResponses({ @ApiResponse(responseCode = "200", description = "AppzillonBanking API reachable"),
		@ApiResponse(responseCode = "408", description = "Service Timed Out"),
		@ApiResponse(responseCode = "500", description = "Internal Server Error"),
		@ApiResponse(responseCode = "404", description = "AppzillonBanking not reachable") })
@Operation(summary = "Fetch list of application master", description = "API to fetch application master")
@PostMapping(value = "/fetchapplist1", produces = MediaType.APPLICATION_JSON_VALUE)
public ResponseEntity<ResponseWrapper> fetchApplicationList(@RequestBody String request) {
		logger.debug("request is :"+ request);
		JSONObject requestObj = new JSONObject(request);
		logger.debug("Request Object "+ requestObj);	
	ResponseWrapper responseWrapper = new ResponseWrapper();
	Response response = loanService.fetchApplicationList1(requestObj);
	responseWrapper.setApiResponse(response);
	logger.warn("End : fetchApplicationList method response is:: {}", responseWrapper);
	return new ResponseEntity<>(responseWrapper, HttpStatus.OK);
}
	
	
	@ApiResponses({ @ApiResponse(responseCode = "200", description = "AppzillonBanking API reachable"),
			@ApiResponse(responseCode = "408", description = "Service Timed Out"),
			@ApiResponse(responseCode = "500", description = "Internal Server Error"),
			@ApiResponse(responseCode = "404", description = "AppzillonBanking not reachable") })
	@Operation(summary = "Fetch Roles", description = "API to Fetch Roles")
	@PostMapping(value = "/fetchrole", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<ResponseWrapper> fetchRole(@RequestBody FetchRoleRequestWrapper requestWrapper) {
		ResponseWrapper responseWrapper = new ResponseWrapper();
		Response response = loanService.fetchRole(requestWrapper.getFetchRoleRequest());
		responseWrapper.setApiResponse(response);
		logger.warn("End : fetchRole method response is:: {}", responseWrapper);
		return new ResponseEntity<>(responseWrapper, HttpStatus.OK);
	}

	@ApiResponses({ @ApiResponse(responseCode = "200", description = "AppzillonBanking API reachable"),
		@ApiResponse(responseCode = "408", description = "Service Timed Out"),
		@ApiResponse(responseCode = "500", description = "Internal Server Error"),
		@ApiResponse(responseCode = "404", description = "AppzillonBanking not reachable") })
@Operation(summary = "Download Sanction Reports", description = "API to Download Sanction Reports")
@GetMapping(value = "/downloadSanctionReport")
public Mono<ResponseEntity<byte[]>> downloadSanctionReport(@RequestParam("id") String applicationId) {
	logger.warn("Start : downloadSactionReport with applicationId :: {}", applicationId);
	 
	return loanService.callandGenerateKFSScheudule(applicationId, "KFS").flatMap(fileObjBytes -> {
		
		HttpHeaders headers = new HttpHeaders();
		headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=SanctionReport.html");
		headers.add(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_OCTET_STREAM_VALUE);
		
		return Mono.just(ResponseEntity.ok().headers(headers).body(fileObjBytes));
	});
	
}

@ApiResponses({ @ApiResponse(responseCode = "200", description = "AppzillonBanking API reachable"),
		@ApiResponse(responseCode = "408", description = "Service Timed Out"),
		@ApiResponse(responseCode = "500", description = "Internal Server Error"),
		@ApiResponse(responseCode = "404", description = "AppzillonBanking not reachable") })
@Operation(summary = "Download DBKit Reports", description = "API to Download Sanction Reports")
@GetMapping(value = "/downloadDBKitReport")
public ResponseEntity<byte[]> downloadDBKitReport(@RequestParam("id") String applicationId) {
	logger.warn("Start : downloadSactionReport with applicationId :: {}", applicationId);

	byte[] fileObjBytes = reportBuildService.generateAndDownloadDBKitReport(applicationId);

	HttpHeaders headers = new HttpHeaders();
	headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=DbKitReport.html");
	headers.add(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_OCTET_STREAM_VALUE);
	logger.warn("DB KIT report downloaded succesfully");
	return ResponseEntity.ok().headers(headers).body(fileObjBytes);
}

	@ApiResponses({ @ApiResponse(responseCode = "200", description = "AppzillonBanking API reachable"),
		@ApiResponse(responseCode = "408", description = "Service Timed Out"),
		@ApiResponse(responseCode = "500", description = "Internal Server Error"),
		@ApiResponse(responseCode = "404", description = "AppzillonBanking not reachable") })
@Operation(summary = "Fetch Sanction Reports", description = "API to Fetch Sanction Reports")
@PostMapping(value = "/fetchSanctionReport", consumes = MediaType.APPLICATION_JSON_VALUE)
	public Mono<ResponseEntity<ResponseWrapper>> fetchSanctionReport(
	        @RequestBody SanctionReportRequestWrapper requestWrapper) {

	    logger.warn("Start : Fetch Sanction Report ");

	    String applicationId = requestWrapper.getApiRequest().getRequestObj().getApplicationId();
	    ResponseWrapper responseWrapper = new ResponseWrapper();
	    Response response = new Response();
	    ResponseHeader responseHeader = new ResponseHeader();
	    response.setResponseHeader(responseHeader);
	    ResponseBody responseBody = new ResponseBody();

	    logger.warn("Fetching Sanction Report for applicationId: {}", applicationId);

	    return loanService.callandGenerateKFSScheudule(applicationId, "KFS")
	        .map(fileObjBytes -> {
	            if (fileObjBytes != null && fileObjBytes.length > 0) {
	                responseBody.setResponseObj(Base64.getEncoder().encodeToString(fileObjBytes));
	                CommonUtils.generateHeaderForSuccess(responseHeader);
	            } else {
	                logger.error("Generated report is empty for applicationId: {}", applicationId);
	                CommonUtils.generateHeaderForFailure(responseHeader, "Generated report is empty");
	                responseBody.setResponseObj("");
	            }
	            response.setResponseBody(responseBody);
	            responseWrapper.setApiResponse(response);
	            return ResponseEntity.ok(responseWrapper);
	        })
	        .onErrorResume(e -> {
	            logger.error("Error while generating Sanction Report for applicationId: {}", applicationId, e);
	            CommonUtils.generateHeaderForFailure(responseHeader, "Failed to generate report");
	            responseBody.setResponseObj("");
	            response.setResponseBody(responseBody);
	            responseWrapper.setApiResponse(response);
	            return Mono.just(ResponseEntity.ok(responseWrapper));
	        });
	}

	
	
	
	@ApiResponses({ @ApiResponse(responseCode = "200", description = "AppzillonBanking API reachable"),
			@ApiResponse(responseCode = "408", description = "Service Timed Out"),
			@ApiResponse(responseCode = "500", description = "Internal Server Error"),
			@ApiResponse(responseCode = "404", description = "AppzillonBanking not reachable") })
	@Operation(summary = "Fetch DBKit Reports", description = "API to Fetch DBKit Reports")
	@PostMapping(value = "/fetchDBKitReport", consumes = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<ResponseWrapper> fetchDBKitReport(@RequestBody SanctionReportRequestWrapper requestWrapper) {

		logger.warn("Start :  fetchDBKitReport Report ");
		ResponseWrapper responseWrapper = new ResponseWrapper();
		String applicationId = requestWrapper.getApiRequest().getRequestObj().getApplicationId();
		Response response = new Response();
		ResponseHeader responseHeader = new ResponseHeader();
		response.setResponseHeader(responseHeader);
		ResponseBody responseBody = new ResponseBody();
		logger.warn("Start : fetchDBKitReport with applicationId :: {}", applicationId);

		try {
			byte[] fileObjBytes = reportBuildService.generateAndDownloadDBKitReport(applicationId);
			responseBody.setResponseObj(Base64.getEncoder().encodeToString(fileObjBytes));
			CommonUtils.generateHeaderForSuccess(responseHeader);
		} catch (Exception exp) {
			logger.error("Error while generate fetchDBKitReport :: {}", exp);
			CommonUtils.generateHeaderForFailure(responseHeader, "Failed to fetchDBKitReport");
			responseBody.setResponseObj("");
		}
		response.setResponseBody(responseBody);
		responseWrapper.setApiResponse(response);
		return new ResponseEntity<>(responseWrapper, HttpStatus.OK);
}

	@ApiResponses({ @ApiResponse(responseCode = "200", description = "AppzillonBanking API reachable"),
			@ApiResponse(responseCode = "408", description = "Service Timed Out"),
			@ApiResponse(responseCode = "500", description = "Internal Server Error"),
			@ApiResponse(responseCode = "404", description = "AppzillonBanking not reachable") })
	@Operation(summary = "Fetch Roles", description = "API to Fetch Roles")
	@PostMapping(value = "/fetchMatrixData", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<ResponseWrapper> fetchMatrixData(@RequestBody FetchRoleRequestWrapper requestWrapper) {
		ResponseWrapper responseWrapper = new ResponseWrapper();
		logger.warn("Start : fetchMatrixData method request is:: {}", requestWrapper);
		Response response = loanService.fetchMatrixData(requestWrapper.getFetchRoleRequest());
		responseWrapper.setApiResponse(response);
		logger.warn("End : fetchMatrixData method response is:: {}", responseWrapper);
		return new ResponseEntity<>(responseWrapper, HttpStatus.OK);
	}

	@ApiResponses({ @ApiResponse(responseCode = "200", description = "AppzillonBanking API reachable"),
			@ApiResponse(responseCode = "408", description = "Service Timed Out"),
			@ApiResponse(responseCode = "500", description = "Internal Server Error"),
			@ApiResponse(responseCode = "404", description = "AppzillonBanking not reachable") })
	@Operation(summary = "FetchCBRecord", description = "FetchCBRecord by doing external service call")
	@PostMapping(value = "/getCBRecord", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	public Mono<ResponseEntity<ResponseWrapper>> fetchCBRecord(@RequestBody FetchCBReportRequestWrapper requestWrapper,
			@RequestHeader(defaultValue = "APZRMB") String appId,
			@RequestHeader(defaultValue = "GetCBRecord") String interfaceId,
			@RequestHeader(defaultValue = "000000000002") String userId,
			@RequestHeader(defaultValue = "12345678") String masterTxnRefNo,
			@RequestHeader(defaultValue = "abcd1234efgh5678") String deviceId) {

		logger.debug("GetCBRecord request data :: {}", requestWrapper);
		Header header = CommonUtils.obtainHeader(appId, interfaceId, userId, masterTxnRefNo, deviceId);
		logger.debug("GetCBRecord Header value :: {}", header);
		FetchCBReportRequest apiRequest = requestWrapper.getApiRequest();
		logger.debug("APIRequest :: {}", apiRequest);

		Mono<ResponseWrapper> monoResponseWrapper = loanService.callCBRecordService(apiRequest, header);
		return monoResponseWrapper.flatMap(resWrapper -> {
			logger.warn("GetCBRecord Response Wrapper ==> {}", resWrapper);
			return Mono.just(new ResponseEntity<>(resWrapper, HttpStatus.OK));
		});
	}
	

	@ApiResponses({ @ApiResponse(responseCode = "200", description = "AppzillonBanking API reachable"),
			@ApiResponse(responseCode = "408", description = "Service Timed Out"),
			@ApiResponse(responseCode = "500", description = "Internal Server Error"),
			@ApiResponse(responseCode = "404", description = "AppzillonBanking not reachable") })
	@Operation(summary = "FetchCBRecord", description = "FetchCBRecord by doing external service call")
	@PostMapping(value = "/fetchLoanSchedule", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	public Mono<ResponseEntity<ResponseWrapper>> fetchLoanSchedule(@RequestBody FetchLoanScheduleRequestWrapper requestWrapper,
			@RequestHeader(defaultValue = "APZRMB") String appId,
			@RequestHeader(defaultValue = "FetchLoanSchedule") String interfaceId,
			@RequestHeader(defaultValue = "000000000002") String userId,
			@RequestHeader(defaultValue = "12345678") String masterTxnRefNo,
			@RequestHeader(defaultValue = "abcd1234efgh5678") String deviceId) {

		logger.debug("FetchLoanSchedule request data :: {}", requestWrapper);
		Header header = CommonUtils.obtainHeader(appId, interfaceId, userId, masterTxnRefNo, deviceId);
		logger.debug("FetchLoanSchedule Header value :: {}", header);
		FetchLoanScheduleRequest apiRequest = requestWrapper.getApiRequest();
		logger.debug("APIRequest :: {}", apiRequest);

		Mono<ResponseWrapper> monoResponseWrapper = loanService.callFetchLoanScheduleService(apiRequest, header);
		return monoResponseWrapper.flatMap(resWrapper -> {
			logger.warn("FetchLoanSchedule Response Wrapper ==> {}", resWrapper);
			return Mono.just(new ResponseEntity<>(resWrapper, HttpStatus.OK));
		});
	}
	

	@ApiResponses({ @ApiResponse(responseCode = "200", description = "AppzillonBanking API reachable"),
			@ApiResponse(responseCode = "408", description = "Service Timed Out"),
			@ApiResponse(responseCode = "500", description = "Internal Server Error"),
			@ApiResponse(responseCode = "404", description = "AppzillonBanking not reachable") })
	@Operation(summary = "FetchCBRecord", description = "FetchCBRecord by doing external service call")
	@PostMapping(value = "/fetchSanctionLoanSchedule", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	public Mono<ResponseEntity<ResponseWrapper>> fetchSanctionLoanSchedule(@RequestBody SanctionLoanScheduleRequestWrapper requestWrapper,
			@RequestHeader(defaultValue = "APZRMB") String appId,
			@RequestHeader(defaultValue = "GetSanctionLoanSchedule") String interfaceId,
			@RequestHeader(defaultValue = "000000000002") String userId,
			@RequestHeader(defaultValue = "12345678") String masterTxnRefNo,
			@RequestHeader(defaultValue = "abcd1234efgh5678") String deviceId) {

		logger.debug("GetSanctionLoanSchedule request data :: {}", requestWrapper);
		Header header = CommonUtils.obtainHeader(appId, interfaceId, userId, masterTxnRefNo, deviceId);
		logger.debug("GetSanctionLoanSchedule Header value :: {}", header);
		SanctionLoanScheduleRequest apiRequest = requestWrapper.getApiRequest();
		logger.debug("APIRequest :: {}", apiRequest);

		Mono<ResponseWrapper> monoResponseWrapper = loanService.callFetchSanctionLoanScheduleService(apiRequest, header);
		return monoResponseWrapper.flatMap(resWrapper -> {
			logger.warn("GetSanctionLoanSchedule Response Wrapper ==> {}", resWrapper);
			return Mono.just(new ResponseEntity<>(resWrapper, HttpStatus.OK));
		});
	}

	/*
	public static void main(String args[]) {
		
		JSONObject requestObj = new JSONObject("{'apiRequest':{'appId':'APZCBO','interfaceName':'DashboardAppListAPI','requestObj':{'kendra':[32061,32070,32076,32077,32082,32089,32090,32098,32103,32107,32110,47529,77494,89892,90206,90560,151920,181372,188567,188590,189662,191880,191919,197681,197834,890856,957876,1620327,1703670,1951541,1951568,2119569,2538630,2605445,2914204,3124645,3147459,5048272,6108630],'kmId':'GK15661'},'kmId':'GK15661'}}");
		
		
		

		List<ApplicationDetailsDto> applicationDtlsList = new ArrayList<>();

		
		// Going to fetch KM iDs and Kendra IDs.
		
		String kmId  =  requestObj.getJSONObject("apiRequest").getString("kmId");
		
		
		JSONArray KendraArray = new JSONArray();
		
		KendraArray =  requestObj.getJSONObject("apiRequest").getJSONObject("requestObj").getJSONArray("kendra");
		List<String> kendraId = new ArrayList<>();
        for (int i = 0; i < KendraArray.length(); i++) {
        	kendraId.add(""+KendraArray.getInt(i));
        }
		
		
		
		/*
		 * String sql =
		 * "SELECT am.APP_ID, am.APPLICATION_ID, am.LATEST_VERSION_NO, am.APPLICATION_DATE, am.APPLICATION_STATUS, "
		 * +
		 * "am.APPLICATION_TYPE, am.BRANCH_CODE, am.CB_CHECK, am.CREATE_TS, am.CREATED_BY, am.CURRENT_SCREEN_ID, "
		 * +
		 * "am.CURRENT_STAGE, am.CUSTOMER_ID, am.KENDRA_ID, am.KENDRANAME, am.KMID, am.KYC_TYPE, am.LEADER, "
		 * +
		 * "am.LOANMODE, am.PRODUCT_CODE, am.PRODUCT_GROUP_CODE, am.REMARKS, am.CUSTOMER_NAME , am.amount, "
		 * + "aw.workflow_seq_no, aw.application_status, " +
		 * "aw.created_ts AS aw_CREATE_TS, aw.created_by AS aw_CREATED_BY " +
		 * "FROM public.tb_uaco_application_master am " +
		 * "LEFT JOIN public.tb_uawf_appln_workflow aw ON cast(am.APPLICATION_ID as text) = cast(aw.APPLICATION_ID as text) "
		 * + "WHERE am.KMID = :kmId AND am.APPLICATION_ID = aw.APPLICATION_ID";
		 */
		
	/*boolean isBranch = false;
		if(kmId != null ) {
			isBranch = kmId.toUpperCase().contains("IN");
		}
	
		String sql = "SELECT am.APP_ID, am.APPLICATION_ID, am.LATEST_VERSION_NO, am.APPLICATION_DATE, am.APPLICATION_STATUS, "
				+ "am.APPLICATION_TYPE, am.BRANCH_CODE, am.CB_CHECK, am.CREATE_TS, am.CREATED_BY, am.CURRENT_SCREEN_ID, "
				+ "am.CURRENT_STAGE, am.CUSTOMER_ID, am.KENDRA_ID, am.KENDRANAME, am.KMID, am.KYC_TYPE, am.LEADER, "
				+ "am.LOANMODE, am.PRODUCT_CODE, am.PRODUCT_GROUP_CODE, am.REMARKS, am.CUSTOMER_NAME, am.amount, "
				+ "aw.workflow_seq_no, aw.application_status, "
				+ "aw.created_ts AS aw_CREATE_TS, aw.created_by AS aw_CREATED_BY "
				+ "FROM public.tb_uaco_application_master am "
				+ "LEFT JOIN public.tb_uawf_appln_workflow aw ON cast(am.APPLICATION_ID as text) = cast(aw.APPLICATION_ID as text) "
				+ "AND aw.workflow_seq_no = (" + "    SELECT MAX(sub_aw.workflow_seq_no) "
				+ "    FROM public.tb_uawf_appln_workflow sub_aw "
				+ "    WHERE cast(sub_aw.APPLICATION_ID as text) = cast(am.APPLICATION_ID as text)" + ") "
				+ "WHERE " + (isBranch ? " am.BRANCH_CODE in :branchCode " : " am.KENDRA_ID IN :kendraIds ") +" AND am.APPLICATION_ID = aw.APPLICATION_ID AND am.APPLICATION_STATUS !='CANCELLED'";
		
		logger.debug("Final SQL query is "+ sql);
		
	}
	*/
	@ApiResponses({ @ApiResponse(responseCode = "200", description = "Record Deleted Successfully"),
		@ApiResponse(responseCode = "404", description = "No record found to delete"),
		@ApiResponse(responseCode = "500", description = "Internal Server Error") })
@Operation(summary = "Delete Application", description = "API to delete Customer application")
@DeleteMapping(value = "/deleteApplication/{applicationId}", produces = MediaType.APPLICATION_JSON_VALUE)
public ResponseEntity<Map<String, Object>> deleteApplication(@PathVariable String applicationId) {
	Boolean isDeleted = loanService.deleteApplication(applicationId);
	 Map<String, Object> response = new HashMap<>();
	 if (isDeleted) {
            response.put("message", "Record Deleted Successfully");
            response.put("status", 200);
            return ResponseEntity.ok(response); 
        } else {
            response.put("message", "No record found to delete");
            response.put("status", 404);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response); 
        }																					
																													
	}

	@ApiResponses({ @ApiResponse(responseCode = "200", description = "AppzillonBanking API reachable"),
		@ApiResponse(responseCode = "408", description = "Service Timed Out"),
		@ApiResponse(responseCode = "500", description = "Internal Server Error"),
		@ApiResponse(responseCode = "404", description = "AppzillonBanking not reachable") })
@Operation(summary = "Fetch list of application master", description = "API to fetch application master for DEO")
@GetMapping(value = "/fetchapplistfordeo/{branchId}", produces = MediaType.APPLICATION_JSON_VALUE)
public ResponseEntity<ResponseWrapper> fetchApplicationListForDEO(@PathVariable String branchId) {
	ResponseWrapper responseWrapper = new ResponseWrapper();		
		JSONObject requestObj = new JSONObject();
		requestObj.put("branchId", branchId);
		requestObj.put("role", "DEO");
		logger.debug("Request Object "+ requestObj);
		Response response=loanService.fetchApplicationList1(requestObj);
	 responseWrapper.setApiResponse(response);
	logger.warn("End : fetchApplicationListForDEO method response is:: {}", responseWrapper);
	return new ResponseEntity<>(responseWrapper, HttpStatus.OK);
	}
	
	@ApiResponses({ @ApiResponse(responseCode = "200", description = "AppzillonBanking API reachable"),
			@ApiResponse(responseCode = "408", description = "Service Timed Out"),
			@ApiResponse(responseCode = "500", description = "Internal Server Error"),
			@ApiResponse(responseCode = "404", description = "AppzillonBanking not reachable") })
	@Operation(summary = "Fetch AuditTrail Details", description = "API to Fetch Audit Trail Details")
	@PostMapping(value = "/fetchAuditTrail", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<ResponseWrapper> fetchAuditTrialData(
			@RequestBody ApplyAuditTrailRequestWrapper requestWrapper, @RequestHeader String appId,
			@RequestHeader String interfaceId, @RequestHeader String userId, @RequestHeader String masterTxnRefNo,
			@RequestHeader String deviceId) {
		ResponseWrapper responseWrapper = new ResponseWrapper();
		logger.debug("Start: AuditTrailDetails responseWrapper :: {}", requestWrapper);
		Response response = loanService.fetchAuditTrialData(requestWrapper.getApiRequest());
		responseWrapper.setApiResponse(response);
		logger.debug("End : AuditTrailDetails response :: {}", response);
		return new ResponseEntity<>(responseWrapper, HttpStatus.OK);
	}

	@ApiResponses({ @ApiResponse(responseCode = "200", description = "AppzillonBanking API reachable"),
			@ApiResponse(responseCode = "408", description = "Service Timed Out"),
			@ApiResponse(responseCode = "500", description = "Internal Server Error"),
			@ApiResponse(responseCode = "404", description = "AppzillonBanking not reachable") })
	@Operation(summary = "Fetch AuditTrail Details", description = "API to Fetch Audit Trail ApplicationId")
	@PostMapping(value = "/fetchAuditTrailApplicationId", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<ResponseWrapper> fetchAuditTrialApplicationId(
			@RequestBody AuditTrailApplicationIdRequestWrapper requestWrapper, @RequestHeader String appId,
			@RequestHeader String interfaceId, @RequestHeader String userId, @RequestHeader String masterTxnRefNo,
			@RequestHeader String deviceId) {
		ResponseWrapper responseWrapper = new ResponseWrapper();
		logger.debug("Start : AuditTrailApplicationId requestWrapper :: {}", requestWrapper);
		Response response = loanService.fetchAuditTrialApplicationId(requestWrapper.getApiRequest());
		responseWrapper.setApiResponse(response);
		logger.debug("End : AuditTrailApplicationId response :: {}", response);
		return new ResponseEntity<>(responseWrapper, HttpStatus.OK);
	}
   
   
	@ApiResponses({ @ApiResponse(responseCode = "200", description = "AppzillonBanking API reachable"),
			@ApiResponse(responseCode = "408", description = "Service Timed Out"),
			@ApiResponse(responseCode = "500", description = "Internal Server Error"),
			@ApiResponse(responseCode = "404", description = "AppzillonBanking not reachable") })
	@Operation(summary = "Fetch States", description = "API to fetch states")
	@PostMapping(value = "/fetchstates", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<ResponseWrapper> fetchstates(@RequestBody FetchStateRequestWrapper requestWrapper) {
		ResponseWrapper responseWrapper = new ResponseWrapper();
		String code = null;
		code = requestWrapper.getApiRequest().getRequestObj().getCode();
		if (code == null || code.isEmpty()) {
			logger.warn("No code provided. Fetching all states.");
			Response response = loanService.FetchStateData(null);
			responseWrapper.setApiResponse(response);
		} else {
			logger.info("Fetching state data for code: {}", code);
			Response response = loanService.FetchStateData(code);
			responseWrapper.setApiResponse(response);
		}
		return new ResponseEntity<>(responseWrapper, HttpStatus.OK);
	}
	
	
	
	@ApiResponses({ @ApiResponse(responseCode = "200", description = "AppzillonBanking API reachable"),
			@ApiResponse(responseCode = "408", description = "Service Timed Out"),
			@ApiResponse(responseCode = "500", description = "Internal Server Error"),
			@ApiResponse(responseCode = "404", description = "AppzillonBanking not reachable") })
	@Operation(summary = "Update CRT Flow", description = "API to fetch states")
	@PostMapping(value = "/updatecrtworkflow", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<Map<String, Object>> updateCRTFlow(@RequestBody UpdateCRTFlowWrapper requestWrapper) {
	    Map<String, Object> response = loanService.updateCRTFlow(requestWrapper.getApiRequest());
	    logger.debug("End : AuditTrailApplicationId response :: {}", response);
	    return new ResponseEntity<>(response, HttpStatus.OK);
	}
	
	
	@ApiResponses({ @ApiResponse(responseCode = "200", description = "Passbook generated successfully"),
			@ApiResponse(responseCode = "408", description = "Service Timed Out"),
			@ApiResponse(responseCode = "500", description = "Internal Server Error"),
			@ApiResponse(responseCode = "404", description = "Service not reachable") })
	@Operation(summary = "Generate Passbook", description = "API to generate passbook as Base64 PDF string")
	@PostMapping(value = "/generatePassbook", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	public Mono<ResponseEntity<ResponseWrapper>> generatePassbook(@RequestBody GeneratePassbookWrapper requestWrapper,
			@RequestHeader String appId, @RequestHeader String interfaceId, @RequestHeader String userId,
			@RequestHeader String masterTxnRefNo, @RequestHeader String deviceId) {
		logger.debug("Generate Passbook request data :: {}", requestWrapper);
		Header header = CommonUtils.obtainHeader(appId, interfaceId, userId, masterTxnRefNo, deviceId);
		logger.debug("Generate Passbook Header value :: {}", header);
		ResponseWrapper responseWrapper = new ResponseWrapper();
		Response response = new Response();
		ResponseHeader responseHeader = new ResponseHeader();
		response.setResponseHeader(responseHeader);
		ResponseBody responseBody = new ResponseBody();

		return loanService.generatePassbook(requestWrapper.getApiRequest(), header).map(fileBytes -> {
			if (fileBytes != null && fileBytes.length > 0) {
				logger.debug("Passbook generated, encoding to Base64..." );
				logger.debug("fileBytes..."+fileBytes );
				String base64Encoded = Base64.getEncoder().encodeToString(fileBytes);
				logger.debug("base64Encoded..."+base64Encoded );
				responseBody.setResponseObj(base64Encoded);
				CommonUtils.generateHeaderForSuccess(responseHeader);
			} else {
				logger.error("Generated passbook is empty");
				CommonUtils.generateHeaderForFailure(responseHeader, "Generated passbook is empty");
				responseBody.setResponseObj("");
			}
			response.setResponseBody(responseBody);
			responseWrapper.setApiResponse(response);
			return ResponseEntity.ok(responseWrapper);
		}).onErrorResume(e -> {
			logger.error("Error generating passbook", e);
			CommonUtils.generateHeaderForFailure(responseHeader, "Failed to generate passbook");
			responseBody.setResponseObj("");
			response.setResponseBody(responseBody);
			responseWrapper.setApiResponse(response);
			return Mono.just(ResponseEntity.ok(responseWrapper));
		});
	}

	@ApiResponses({ @ApiResponse(responseCode = "200", description = "AppzillonBanking API reachable"),
			@ApiResponse(responseCode = "408", description = "Service Timed Out"),
			@ApiResponse(responseCode = "500", description = "Internal Server Error"),
			@ApiResponse(responseCode = "404", description = "AppzillonBanking not reachable") })
	@Operation(summary = "Fetch Loan List Details", description = "API to Fetch Loan List")
	@PostMapping(value = "/fetchLoanList", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<ResponseWrapper> fetchLoanList(@RequestBody FetchLoanRequestWrapper requestWrapper) {
		ResponseWrapper responseWrapper = new ResponseWrapper();
		logger.debug("Fetch Loan List request data :: {}", requestWrapper);
		Response response = loanService.fetchLoanList(requestWrapper.getApiRequest());
		logger.debug("response:  " + response);
		responseWrapper.setApiResponse(response);
		logger.debug("End : Fetch Loan List response :: {}", response);
		return new ResponseEntity<>(responseWrapper, HttpStatus.OK);
	}
	
	
	@ApiResponses({ @ApiResponse(responseCode = "200", description = "AppzillonBanking API reachable"),
			@ApiResponse(responseCode = "408", description = "Service Timed Out"),
			@ApiResponse(responseCode = "500", description = "Internal Server Error"),
			@ApiResponse(responseCode = "404", description = "AppzillonBanking not reachable") })
	@Operation(summary = "Fetch Target Details", description = "API to Fetch Target List")
	@PostMapping(value = "/fetchTarget", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<ResponseWrapper> fetchTarget(@RequestBody FetchLoanTargetWrapper requestWrapper) {
		ResponseWrapper responseWrapper = new ResponseWrapper();
		logger.debug("Fetch Target request data :: {}", requestWrapper);
		Response response = loanService.fetchTarget(requestWrapper.getApiRequest());
		logger.debug("response:  " + response);
		responseWrapper.setApiResponse(response);
		logger.debug("End : Fetch Target response :: {}", response);
		return new ResponseEntity<>(responseWrapper, HttpStatus.OK);
	}
	
	
	@ApiResponses({ @ApiResponse(responseCode = "200", description = "AppzillonBanking API reachable"),
			@ApiResponse(responseCode = "408", description = "Service Timed Out"),
			@ApiResponse(responseCode = "500", description = "Internal Server Error"),
			@ApiResponse(responseCode = "404", description = "AppzillonBanking not reachable") })
	@Operation(summary = "Fetch Emi Product List", description = "API to Fetch Emi Product List")
	@PostMapping(value = "/fetchEmiProduct", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<ResponseWrapper> fetchEmiProduct(@RequestBody FetchEmiProductListWrapper requestWrapper) {
		ResponseWrapper responseWrapper = new ResponseWrapper();
		logger.debug("Fetch Emi Product List request data :: {}", requestWrapper);
		Response response = loanService.fetchEmiProductList(requestWrapper.getApiRequest());
		logger.debug("response:  " + response);
		responseWrapper.setApiResponse(response);
		logger.debug("End : Fetch Emi Product List response :: {}", response);
		return new ResponseEntity<>(responseWrapper, HttpStatus.OK);
	}
	 
}
