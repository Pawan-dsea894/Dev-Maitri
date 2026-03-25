package com.iexceed.appzillonbanking.cagl.rest;

import java.util.Base64;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.iexceed.appzillonbanking.cagl.document.payload.SanctionReportRequestWrapper;
import com.iexceed.appzillonbanking.cagl.document.service.DocumentService;
import com.iexceed.appzillonbanking.cagl.document.service.DocumentService1;
import com.iexceed.appzillonbanking.cagl.document.service.ReportBuildService;
import com.iexceed.appzillonbanking.core.payload.Response;
import com.iexceed.appzillonbanking.core.payload.ResponseBody;
import com.iexceed.appzillonbanking.core.payload.ResponseHeader;
import com.iexceed.appzillonbanking.core.payload.ResponseWrapper;
import com.iexceed.appzillonbanking.core.utils.CommonUtils;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import reactor.core.publisher.Mono;

@RestController
@Tag(description = "application/document", name = "DOCUMENT")
@RequestMapping("application/document")
public class DocumentAPI {

	private static final Logger logger = LogManager.getLogger(DocumentAPI.class);

	@Autowired
	private DocumentService documentService;

	@Autowired
	ReportBuildService reportBuildService;

	@Autowired
	DocumentService1 documentService1;

	@ApiResponses({ @ApiResponse(responseCode = "200", description = "AppzillonBanking API reachable"),
			@ApiResponse(responseCode = "408", description = "Service Timed Out"),
			@ApiResponse(responseCode = "500", description = "Internal Server Error"),
			@ApiResponse(responseCode = "404", description = "AppzillonBanking not reachable") })
	@Operation(summary = "Download Sanction Reports", description = "API to Download Sanction Reports")
	@GetMapping(value = "/downloadSanctionReportHis")
	public Mono<ResponseEntity<byte[]>> downloadSanctionReportHis(@RequestParam("id") String applicationId) {
		logger.warn("Start : downloadSactionReport with applicationId :: {}", applicationId);

		return documentService.callandGenerateKFSScheuduleHis(applicationId, "KFS").flatMap(fileObjBytes -> {

			HttpHeaders headers = new HttpHeaders();
			headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=SanctionReport.html");
			headers.add(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_OCTET_STREAM_VALUE);

			return Mono.just(ResponseEntity.ok().headers(headers).body(fileObjBytes));
		});
	}


	//=========================Fetch Report=========================
	/**
	 * @API_Author Ankit.CAG
	 * @API->Use to fetchSanctionReport
	 */


	@ApiResponses({ @ApiResponse(responseCode = "200", description = "AppzillonBanking API reachable"),
			@ApiResponse(responseCode = "408", description = "Service Timed Out"),
			@ApiResponse(responseCode = "500", description = "Internal Server Error"),
			@ApiResponse(responseCode = "404", description = "AppzillonBanking not reachable") })
	@Operation(summary = "Fetch Sanction Reports", description = "API to Fetch Sanction Reports")
	@PostMapping(value = "/fetchReports", consumes = MediaType.APPLICATION_JSON_VALUE)
	public Mono<ResponseEntity<ResponseWrapper>> fetchSanctionReport(
			@RequestBody SanctionReportRequestWrapper requestWrapper) {

		logger.warn("Start : Fetch Sanction Report ");
		String applicationId = requestWrapper.getApiRequest().getRequestObj().getApplicationId();
		logger.warn("applicationId:-"+applicationId);
		logger.warn("ReportType:"+requestWrapper.getApiRequest().getRequestObj().getReportType());
		ResponseWrapper responseWrapper = new ResponseWrapper();
		Response response = new Response();
		ResponseHeader responseHeader = new ResponseHeader();
		response.setResponseHeader(responseHeader);
		ResponseBody responseBody = new ResponseBody();

		logger.warn("Fetching Sanction Report for applicationId: {}", applicationId);

		return documentService1.callandGenerateKFSScheudule(applicationId,"KFS",requestWrapper.getApiRequest().getRequestObj().getReportType())
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

	//=====================fetchDBKitReport=================================

	@ApiResponses({ @ApiResponse(responseCode = "200", description = "AppzillonBanking API reachable"),
			@ApiResponse(responseCode = "408", description = "Service Timed Out"),
			@ApiResponse(responseCode = "500", description = "Internal Server Error"),
			@ApiResponse(responseCode = "404", description = "AppzillonBanking not reachable") })
	@Operation(summary = "Fetch DBKit Reports", description = "API to Fetch DBKit Reports")
	@PostMapping(value = "/getDBKitReport", consumes = MediaType.APPLICATION_JSON_VALUE)
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



}