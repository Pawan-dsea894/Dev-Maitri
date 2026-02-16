package com.iexceed.appzillonbanking.kyc.rest;

import com.iexceed.appzillonbanking.kyc.payload.*;
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
import com.iexceed.appzillonbanking.core.payload.ResponseBody;
import com.iexceed.appzillonbanking.core.payload.ResponseHeader;
import com.iexceed.appzillonbanking.core.payload.ResponseWrapper;
import com.iexceed.appzillonbanking.core.utils.CommonUtils;
import com.iexceed.appzillonbanking.kyc.service.KYCService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import reactor.core.publisher.Mono;

@RestController
@Tag(description = "application/kyc", name = "KYC")
@RequestMapping("application/kyc/")
public class KYCServiceController {

	private static final Logger logger = LogManager.getLogger(KYCServiceController.class);

	@Autowired
	KYCService kycService;

	@ApiResponses({ @ApiResponse(responseCode = "200", description = "AppzillonBanking API reachable"),
			@ApiResponse(responseCode = "408", description = "Service Timed Out"),
			@ApiResponse(responseCode = "500", description = "Internal Server Error"),
			@ApiResponse(responseCode = "404", description = "AppzillonBanking not reachable") })
	@Operation(summary = "OCR Process", description = "API to OCR Process for Aadhaar and Voter")
	@PostMapping(value = "ocr", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	public Mono<ResponseEntity<ResponseWrapper>> dedupeCheck(@RequestBody OCRRequestWrapper requestWrapper,
			@RequestHeader String appId, @RequestHeader String interfaceId, @RequestHeader String userId,
			@RequestHeader String masterTxnRefNo, @RequestHeader String deviceId) {

		logger.debug("ocr request data :: {}", requestWrapper);
		Header header = CommonUtils.obtainHeader(appId, interfaceId, userId, masterTxnRefNo, deviceId);
		logger.debug("ocr Header value :: {}", header);
		try {
			Mono<ResponseWrapper> monoResponseWrapper = kycService.callOCR(requestWrapper.getApiRequest(), header);
			return monoResponseWrapper.flatMap(resWrapper -> {
				logger.warn("ocr Response Wrapper ==> {}", resWrapper);
				return Mono.just(new ResponseEntity<>(resWrapper, HttpStatus.OK));
			});
		} catch (Exception e) {
			ResponseWrapper errorResponseWrapper = new ResponseWrapper();
			Response errorResponse = new Response();
			ResponseHeader errorHeader = new ResponseHeader();
			ResponseBody errorBody = new ResponseBody();
			CommonUtils.generateHeaderForFailure(errorHeader, e.getMessage());
			errorBody.setResponseObj("");
			errorResponse.setResponseBody(errorBody);
			errorResponse.setResponseHeader(errorHeader);
			errorResponseWrapper.setApiResponse(errorResponse);
			return Mono.just(new ResponseEntity<>(errorResponseWrapper, HttpStatus.OK));
		}
	}
	
	@ApiResponses({ @ApiResponse(responseCode = "200", description = "AppzillonBanking API reachable"),
		@ApiResponse(responseCode = "408", description = "Service Timed Out"),
		@ApiResponse(responseCode = "500", description = "Internal Server Error"),
		@ApiResponse(responseCode = "404", description = "AppzillonBanking not reachable") })
@Operation(summary = "Pan Verify Process", description = "API to Verify Pan Number")
@PostMapping(value = "panVerify", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
public Mono<ResponseEntity<ResponseWrapper>> panVerify(@RequestBody PanVerifyRequestWrapper requestWrapper,
		@RequestHeader String appId, @RequestHeader String interfaceId, @RequestHeader String userId,
		@RequestHeader String masterTxnRefNo, @RequestHeader String deviceId) {

	logger.debug("panVerify request data :: {}", requestWrapper);
	Header header = CommonUtils.obtainHeader(appId, interfaceId, userId, masterTxnRefNo, deviceId);
	logger.debug("panVerify Header value :: {}", header);
	try {
		Mono<ResponseWrapper> monoResponseWrapper = kycService.panVerify(requestWrapper.getApiRequest(), header);
		return monoResponseWrapper.flatMap(resWrapper -> {
			logger.warn("panVerify Response Wrapper ==> {}", resWrapper);
			return Mono.just(new ResponseEntity<>(resWrapper, HttpStatus.OK));
		});
	} catch (Exception e) {
		ResponseWrapper errorResponseWrapper = new ResponseWrapper();
		Response errorResponse = new Response();
		ResponseHeader errorHeader = new ResponseHeader();
		ResponseBody errorBody = new ResponseBody();
		CommonUtils.generateHeaderForFailure(errorHeader, e.getMessage());
		errorBody.setResponseObj("");
		errorResponse.setResponseBody(errorBody);
		errorResponse.setResponseHeader(errorHeader);
		errorResponseWrapper.setApiResponse(errorResponse);
		return Mono.just(new ResponseEntity<>(errorResponseWrapper, HttpStatus.OK));
	}
}
	
@ApiResponses({ @ApiResponse(responseCode = "200", description = "AppzillonBanking API reachable"),
		@ApiResponse(responseCode = "408", description = "Service Timed Out"),
		@ApiResponse(responseCode = "500", description = "Internal Server Error"),
		@ApiResponse(responseCode = "404", description = "AppzillonBanking not reachable") })
@Operation(summary = "drivingLicenseVerify Verify Process", description = "API to Verify drivingLicenseVerify")
@PostMapping(value = "drivingLicenseVerify", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
public Mono<ResponseEntity<ResponseWrapper>> drivingLicenseVerify(@RequestBody DrivinglicenseVerifyRequestWrapper requestWrapper,
		@RequestHeader(defaultValue = "APZRMB") String appId,
		@RequestHeader(defaultValue = "DrivingLicenseVerify") String interfaceId,
		@RequestHeader(defaultValue = "000000000002") String userId,
		@RequestHeader(defaultValue = "12345678") String masterTxnRefNo,
		@RequestHeader(defaultValue = "abcd1234efgh5678") String deviceId) {

	logger.debug("drivingLicenseVerify request data :: {}", requestWrapper);
	Header header = CommonUtils.obtainHeader(appId, interfaceId, userId, masterTxnRefNo, deviceId);
	logger.debug("drivingLicenseVerify Header value :: {}", header);
	try {
		Mono<ResponseWrapper> monoResponseWrapper = kycService.drivingLicenseVerify(requestWrapper.getApiRequest(),
				header);
		return monoResponseWrapper.flatMap(resWrapper -> {
			logger.warn("drivingLicenseVerify Response Wrapper ==> {}", resWrapper);
			return Mono.just(new ResponseEntity<>(resWrapper, HttpStatus.OK));
		});
	} catch (Exception e) {
		ResponseWrapper errorResponseWrapper = new ResponseWrapper();
		Response errorResponse = new Response();
		ResponseHeader errorHeader = new ResponseHeader();
		ResponseBody errorBody = new ResponseBody();
		CommonUtils.generateHeaderForFailure(errorHeader, e.getMessage());
		errorBody.setResponseObj("");
		errorResponse.setResponseBody(errorBody);
		errorResponse.setResponseHeader(errorHeader);
		errorResponseWrapper.setApiResponse(errorResponse);
		return Mono.just(new ResponseEntity<>(errorResponseWrapper, HttpStatus.OK));
	}
}
	
	@ApiResponses({ @ApiResponse(responseCode = "200", description = "AppzillonBanking API reachable"),
		@ApiResponse(responseCode = "408", description = "Service Timed Out"),
		@ApiResponse(responseCode = "500", description = "Internal Server Error"),
		@ApiResponse(responseCode = "404", description = "AppzillonBanking not reachable") })
@Operation(summary = "Voter Authentication", description = "API to Authenticate voter id")
@PostMapping(value = "voterAuthentication", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
public Mono<ResponseEntity<ResponseWrapper>> voterAuthentication(@RequestBody VoterAuthenticateRequestWrapper requestWrapper,
		@RequestHeader String appId, @RequestHeader String interfaceId, @RequestHeader String userId,
		@RequestHeader String masterTxnRefNo, @RequestHeader String deviceId) {

	logger.debug("Voter Authentication request data :: {}", requestWrapper);
	Header header = CommonUtils.obtainHeader(appId, interfaceId, userId, masterTxnRefNo, deviceId);
	logger.debug("Voter Authentication Header value :: {}", header);
	try {
		Mono<ResponseWrapper> monoResponseWrapper = kycService.voterAuthentication(requestWrapper.getApiRequest(), header);
		return monoResponseWrapper.flatMap(resWrapper -> {
			logger.warn("Voter Authentication Response Wrapper ==> {}", resWrapper);
			return Mono.just(new ResponseEntity<>(resWrapper, HttpStatus.OK));
		});
	} catch (Exception e) {
		ResponseWrapper errorResponseWrapper = new ResponseWrapper();
		Response errorResponse = new Response();
		ResponseHeader errorHeader = new ResponseHeader();
		ResponseBody errorBody = new ResponseBody();
		CommonUtils.generateHeaderForFailure(errorHeader, e.getMessage());
		errorBody.setResponseObj("");
		errorResponse.setResponseBody(errorBody);
		errorResponse.setResponseHeader(errorHeader);
		errorResponseWrapper.setApiResponse(errorResponse);
		return Mono.just(new ResponseEntity<>(errorResponseWrapper, HttpStatus.OK));
	}
}
	

	@ApiResponses({ @ApiResponse(responseCode = "200", description = "AppzillonBanking API reachable"),
			@ApiResponse(responseCode = "408", description = "Service Timed Out"),
			@ApiResponse(responseCode = "500", description = "Internal Server Error"),
			@ApiResponse(responseCode = "404", description = "AppzillonBanking not reachable") })
	@Operation(summary = "Dedupe Check", description = "API to Check Dedupe for KYC, Mobile and Bank Info")
	@PostMapping(value = "dedupe", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	public Mono<ResponseEntity<ResponseWrapper>> dedupeCheck(@RequestBody DedupeRequestWrapper requestWrapper,
			@RequestHeader String appId, @RequestHeader String interfaceId, @RequestHeader String userId,
			@RequestHeader String masterTxnRefNo, @RequestHeader String deviceId) {

		logger.debug("dedupeCheck request data :: {}", requestWrapper);
		Header header = CommonUtils.obtainHeader(appId, interfaceId, userId, masterTxnRefNo, deviceId);
		logger.debug("dedupeCheck Header value :: {}", header);
		try {
			Mono<ResponseWrapper> monoResponseWrapper = kycService.dedupeCheck(requestWrapper.getApiRequest(),
					header, requestWrapper.getApiRequest().getServiceName());
			return monoResponseWrapper.flatMap(resWrapper -> {
				logger.warn("dedupeCheck Response Wrapper ==> {}", resWrapper);
				return Mono.just(new ResponseEntity<>(resWrapper, HttpStatus.OK));
			});
		} catch (Exception e) {
			ResponseWrapper errorResponseWrapper = new ResponseWrapper();
			Response errorResponse = new Response();
			ResponseHeader errorHeader = new ResponseHeader();
			ResponseBody errorBody = new ResponseBody();
			CommonUtils.generateHeaderForFailure(errorHeader, e.getMessage());
			errorBody.setResponseObj("");
			errorResponse.setResponseBody(errorBody);
			errorResponse.setResponseHeader(errorHeader);
			errorResponseWrapper.setApiResponse(errorResponse);
			return Mono.just(new ResponseEntity<>(errorResponseWrapper, HttpStatus.OK));
		}
	}

	
	@ApiResponses({ @ApiResponse(responseCode = "200", description = "AppzillonBanking API reachable"),
			@ApiResponse(responseCode = "408", description = "Service Timed Out"),
			@ApiResponse(responseCode = "500", description = "Internal Server Error"),
			@ApiResponse(responseCode = "404", description = "AppzillonBanking not reachable") })
	@Operation(summary = "MobileNumber Updation", description = "API to Update Mobile Number on basis of phoneNumber and memberId")
	@PostMapping(value = "updateMobileNumber", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	public Mono<ResponseEntity<ResponseWrapper>> updateMobileNumber(
			@RequestBody MobileNumberUpdateWrapper requestWrapper, @RequestHeader(defaultValue = "APZRMB") String appId,
			@RequestHeader(defaultValue = "MobileNumberUpdate") String interfaceId,
			@RequestHeader(defaultValue = "000000000002") String userId,
			@RequestHeader(defaultValue = "12345678") String masterTxnRefNo,
			@RequestHeader(defaultValue = "abcd1234efgh5678") String deviceId) {

		logger.debug("mobileNumberUpdate request data :: {}", requestWrapper);
		Header header = CommonUtils.obtainHeader(appId, interfaceId, userId, masterTxnRefNo, deviceId);
		logger.debug("mobileNumberUpdate Header value :: {}", header);
		Mono<ResponseWrapper> monoResponseWrapper = kycService.updateMobileNumService(requestWrapper.getApiRequest(),
				header, requestWrapper.getApiRequest().getServiceName());
		return monoResponseWrapper.flatMap(resWrapper -> {
			logger.warn("mobileNumberUpdate Response Wrapper ==> {}", resWrapper);
			return Mono.just(new ResponseEntity<>(resWrapper, HttpStatus.OK));
		});
	}


	@ApiResponses({ @ApiResponse(responseCode = "200", description = "AppzillonBanking API reachable"),
			@ApiResponse(responseCode = "408", description = "Service Timed Out"),
			@ApiResponse(responseCode = "500", description = "Internal Server Error"),
			@ApiResponse(responseCode = "404", description = "AppzillonBanking not reachable") })
	@Operation(summary = "Dedup For CustomerImage", description = "API to Check Dedup For Customer Image")
	@PostMapping(value = "dedupe/custImage", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	public Mono<ResponseEntity<ResponseWrapper>> deDupForCustomerImage(@RequestBody DedupeFaceWrapper requestWrapper,
																	   @RequestHeader String appId, @RequestHeader String interfaceId, @RequestHeader String userId,
																	   @RequestHeader String masterTxnRefNo, @RequestHeader String deviceId) {

		logger.debug("DeDup Customer Image request data :: {}", requestWrapper);
		Header header = CommonUtils.obtainHeader(appId, interfaceId, userId, masterTxnRefNo, deviceId);
		logger.debug("DeDup Customer Image Header value :: {}", header);
		try {
			Mono<ResponseWrapper> monoResponseWrapper = kycService.dedupeCustImage(requestWrapper.getApiRequest(),
					header);
			return monoResponseWrapper.flatMap(resWrapper -> {
				logger.warn("DeDup Customer Image Response Wrapper ==> {}", resWrapper);
				return Mono.just(new ResponseEntity<>(resWrapper, HttpStatus.OK));
			});
		} catch (Exception e) {
			ResponseWrapper errorResponseWrapper = new ResponseWrapper();
			Response errorResponse = new Response();
			ResponseHeader errorHeader = new ResponseHeader();
			ResponseBody errorBody = new ResponseBody();
			CommonUtils.generateHeaderForFailure(errorHeader, e.getMessage());
			errorBody.setResponseObj("");
			errorResponse.setResponseBody(errorBody);
			errorResponse.setResponseHeader(errorHeader);
			errorResponseWrapper.setApiResponse(errorResponse);
			return Mono.just(new ResponseEntity<>(errorResponseWrapper, HttpStatus.OK));
		}
	}

	@ApiResponses({ @ApiResponse(responseCode = "200", description = "AppzillonBanking API reachable"),
			@ApiResponse(responseCode = "408", description = "Service Timed Out"),
			@ApiResponse(responseCode = "500", description = "Internal Server Error"),
			@ApiResponse(responseCode = "404", description = "AppzillonBanking not reachable") })
	@Operation(summary = "FaceVerify", description = "API to verify Face")
	@PostMapping(value = "faceMatch", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	public Mono<ResponseEntity<ResponseWrapper>> validateFaceMatch(@RequestBody FaceMatchWrapper requestWrapper,
																	   @RequestHeader String appId, @RequestHeader String interfaceId, @RequestHeader String userId,
																	   @RequestHeader String masterTxnRefNo, @RequestHeader String deviceId) {

		logger.debug("validate Face Match request data :: {}", requestWrapper);
		Header header = CommonUtils.obtainHeader(appId, interfaceId, userId, masterTxnRefNo, deviceId);
		logger.debug("validate Face Match Header value :: {}", header);
		try {
			Mono<ResponseWrapper> monoResponseWrapper = kycService.faceVerify(requestWrapper.getApiRequest(), header);
			return monoResponseWrapper.flatMap(resWrapper -> {
				logger.warn("validate Face Match Response Wrapper ==> {}", resWrapper);

				return Mono.just(new ResponseEntity<>(resWrapper, HttpStatus.OK));
			});
		} catch (Exception e) {
			ResponseWrapper errorResponseWrapper = new ResponseWrapper();
			Response errorResponse = new Response();
			ResponseHeader errorHeader = new ResponseHeader();
			ResponseBody errorBody = new ResponseBody();
			CommonUtils.generateHeaderForFailure(errorHeader, e.getMessage());
			errorBody.setResponseObj("");
			errorResponse.setResponseBody(errorBody);
			errorResponse.setResponseHeader(errorHeader);
			errorResponseWrapper.setApiResponse(errorResponse);
			return Mono.just(new ResponseEntity<>(errorResponseWrapper, HttpStatus.OK));
		}
	}


}