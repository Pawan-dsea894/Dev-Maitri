package com.iexceed.appzillonbanking.cagl.collection.rest;

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

import com.iexceed.appzillonbanking.cagl.collection.payload.QRPaymentCBRequest;
import com.iexceed.appzillonbanking.cagl.collection.payload.QRUPICallbackResponse;
import com.iexceed.appzillonbanking.cagl.collection.service.ExternalAPIService;
import com.iexceed.appzillonbanking.core.payload.Response;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@Tag(description = "application/collection", name = "External API")
@RequestMapping("application/collection")
public class ExternalAPI {

	private static final Logger logger = LogManager.getLogger(ExternalAPI.class);

	@Autowired
	private ExternalAPIService externalAPIService;

	@ApiResponses({ @ApiResponse(responseCode = "200", description = "AppzillonBanking API reachable"),
			@ApiResponse(responseCode = "408", description = "Service Timed Out"),
			@ApiResponse(responseCode = "500", description = "Internal Server Error"),
			@ApiResponse(responseCode = "404", description = "AppzillonBanking not reachable") })
	@Operation(summary = "QR Payment Callback API", description = "QR UPI Payment Callback API")
	@PostMapping(value = "/QRPaymentCB", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<QRUPICallbackResponse> qrPaymentCB(@RequestBody QRPaymentCBRequest qrPaymentCBRequest) {

		logger.warn("Start : qrPaymentCB with request :: {}", qrPaymentCBRequest);
		Response response = externalAPIService.qrPaymentCB(qrPaymentCBRequest);
		QRUPICallbackResponse qrupiCallbackResponse = new QRUPICallbackResponse();
		if (com.iexceed.appzillonbanking.core.constants.CommonConstants.SUCCESS
				.equalsIgnoreCase(response.getResponseHeader().getResponseCode())) {
			qrupiCallbackResponse.setStatus("0");
			qrupiCallbackResponse.setMessage("Payment processed successfully.");
		} else if ("NR-BILLNUM".equalsIgnoreCase(response.getResponseHeader().getResponseCode())) {
			qrupiCallbackResponse.setStatus("1");
			qrupiCallbackResponse.setMessage("No matching billNumber found");
		} else if ("INVL-BILLNUM".equalsIgnoreCase(response.getResponseHeader().getResponseCode())) {
			qrupiCallbackResponse.setStatus("1");
			qrupiCallbackResponse.setMessage("Invalid value for billNumber field.");
		} else {
			qrupiCallbackResponse.setStatus("1");
			qrupiCallbackResponse.setMessage("Failed to process the request");
		}
		logger.debug("End : qrPaymentCB response :: {}", qrupiCallbackResponse);
		return new ResponseEntity<>(qrupiCallbackResponse, HttpStatus.OK);
	}
}
