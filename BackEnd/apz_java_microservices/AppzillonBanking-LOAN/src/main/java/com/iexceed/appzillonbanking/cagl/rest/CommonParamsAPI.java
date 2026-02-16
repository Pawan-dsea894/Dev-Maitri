package com.iexceed.appzillonbanking.cagl.rest;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.iexceed.appzillonbanking.cagl.loan.payload.CommonParamRequestWrapper;
import com.iexceed.appzillonbanking.cagl.loan.payload.FetchLitByLanguageRequest;
import com.iexceed.appzillonbanking.cagl.loan.payload.FetchLitByLanguageRequestWrapper;
import com.iexceed.appzillonbanking.cagl.loan.payload.FetchLovRequestWrapper;
import com.iexceed.appzillonbanking.cagl.loan.service.CommonParamService;
import com.iexceed.appzillonbanking.core.payload.RequestWrapper;
import com.iexceed.appzillonbanking.core.payload.Response;
import com.iexceed.appzillonbanking.core.payload.ResponseBody;
import com.iexceed.appzillonbanking.core.payload.ResponseHeader;
import com.iexceed.appzillonbanking.core.payload.ResponseWrapper;
import com.iexceed.appzillonbanking.core.utils.CommonUtils;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@Tag(description = "application/core", name = "CommonParameters")
@RequestMapping("application/core")
public class CommonParamsAPI {

	@Autowired
	private CommonParamService commonParamService;

	private static final Logger logger = LogManager.getLogger(CommonParamsAPI.class);

	@ApiResponses({ @ApiResponse(responseCode = "200", description = "AppzillonBanking API reachable"),
			@ApiResponse(responseCode = "408", description = "Service Timed Out"),
			@ApiResponse(responseCode = "500", description = "Internal Server Error"),
			@ApiResponse(responseCode = "404", description = "AppzillonBanking not reachable") })
	@Operation(summary = "Obtain the Common Params", description = "API to obtain the Common Params")
	@PostMapping(value = "/fetchCommonParams", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<ResponseWrapper> fetchCommonParams(
			@RequestBody CommonParamRequestWrapper commonRequestParam) {
		logger.debug("COB Start : Fetch Common Params with request :: " + commonRequestParam.toString());
		ResponseWrapper commonParamResponseWrapper = new ResponseWrapper();
		Response commonParamResponse = new Response();
		ResponseHeader responseHeader = new ResponseHeader();
		ResponseBody responseBody = new ResponseBody();
		try {
			commonParamResponse = commonParamService.fetchAllData(commonRequestParam.getApiRequest());
		} catch (Exception e) {
			logger.error("COB COMMON PARAMS ERROR = ", e);
			CommonUtils.generateHeaderForGenericError(responseHeader);
			responseBody.setResponseObj("");

			commonParamResponse.setResponseHeader(responseHeader);
			commonParamResponse.setResponseBody(responseBody);
		}
		commonParamResponseWrapper.setApiResponse(commonParamResponse);
		logger.debug("End : COB Fetch Common Params with response :: " + commonParamResponseWrapper.toString());
		return new ResponseEntity<>(commonParamResponseWrapper, HttpStatus.OK);
	}

	@ApiResponses({ @ApiResponse(responseCode = "200", description = "AppzillonBanking API reachable"),
		@ApiResponse(responseCode = "408", description = "Service Timed Out"),
		@ApiResponse(responseCode = "500", description = "Internal Server Error"),
		@ApiResponse(responseCode = "404", description = "AppzillonBanking not reachable") })
	@Operation(summary = "Fetch LIT Codes", description = "API to fetch LIT Codes based on language from property file")
	@PostMapping(value = "/fetchlitbylanguage", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<ResponseWrapper> fetchLitByLanguage(@RequestBody FetchLitByLanguageRequestWrapper requestWrapper) {
		ResponseWrapper responseWrapper = new ResponseWrapper();
		FetchLitByLanguageRequest request = requestWrapper.getApiRequest();
		Response response = commonParamService.fetchLitByLanguage(request);
		responseWrapper.setApiResponse(response);
		logger.warn("End : fetchLitByLanguage method response is:: " + responseWrapper.toString());
		return new ResponseEntity<>(responseWrapper, HttpStatus.OK);
	}

	@ApiResponses({ @ApiResponse(responseCode = "200", description = "AppzillonBanking API reachable"),
		@ApiResponse(responseCode = "408", description = "Service Timed Out"),
		@ApiResponse(responseCode = "500", description = "Internal Server Error"),
		@ApiResponse(responseCode = "404", description = "AppzillonBanking not reachable") })
	@Operation(summary = "Fetch list of LOV names", description = "API to Fetch list of LOV names")
	@PostMapping(value = "/fetchlovmaster", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<ResponseWrapper> fetchLovMaster(@RequestBody RequestWrapper requestWrapper) {
		ResponseWrapper fetchLovMasterResponseWrapper = new ResponseWrapper();
		Response fetchLovMasterResponse = commonParamService.fetchLovMaster(requestWrapper.getApiRequest());
		fetchLovMasterResponseWrapper.setApiResponse(fetchLovMasterResponse);
		logger.warn("End : fetchLovMaster method response is:: " + fetchLovMasterResponseWrapper.toString());
		return new ResponseEntity<>(fetchLovMasterResponseWrapper, HttpStatus.OK);
	}

	@ApiResponses({ @ApiResponse(responseCode = "200", description = "AppzillonBanking API reachable"),
		@ApiResponse(responseCode = "408", description = "Service Timed Out"),
		@ApiResponse(responseCode = "500", description = "Internal Server Error"),
		@ApiResponse(responseCode = "404", description = "AppzillonBanking not reachable") })
	@Operation(summary = "Fetch List Of Values Master table data", description = "API to Fetch List Of Values Master table data")
	@PostMapping(value = "/fetchlov", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<ResponseWrapper> fetchLov(@RequestBody FetchLovRequestWrapper requestWrapper) {
		ResponseWrapper responseWrapper = new ResponseWrapper();
		Response response = commonParamService.fetchLov(requestWrapper.getApiRequest());
		responseWrapper.setApiResponse(response);
		logger.warn("End : fetchLov method response is:: " + responseWrapper.toString());
		return new ResponseEntity<>(responseWrapper, HttpStatus.OK);
	}

}