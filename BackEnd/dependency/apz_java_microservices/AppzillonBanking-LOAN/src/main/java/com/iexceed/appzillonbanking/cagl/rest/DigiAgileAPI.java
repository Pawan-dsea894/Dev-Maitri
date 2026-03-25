package com.iexceed.appzillonbanking.cagl.rest;
import com.iexceed.appzillonbanking.cagl.loan.payload.DigiAgileUserDtls;
import com.iexceed.appzillonbanking.cagl.loan.service.DigiAgileUserSaveService;
import com.iexceed.appzillonbanking.cagl.loan.service.DigiAgileUserSaveServiceWithValidation;
import com.iexceed.appzillonbanking.core.payload.ResponseWrapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
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
import com.iexceed.appzillonbanking.core.payload.Response;

@RestController
@RequestMapping("/application/digiAgileUserInfo")
@Tag(description = "/application/digiAgileUserInfo", name = "LOAN")
public class DigiAgileAPI {

	private static final Logger logger = LogManager.getLogger(DigiAgileAPI.class);

	@Autowired
	DigiAgileUserSaveService digiAgileUserSaveService;

	@Autowired
	DigiAgileUserSaveServiceWithValidation digiAgileUserSaveServiceWithValidation;

	@ApiResponses({ @ApiResponse(responseCode = "200", description = "AppzillonBanking API reachable"),
			@ApiResponse(responseCode = "408", description = "Service Timed Out"),
			@ApiResponse(responseCode = "500", description = "Internal Server Error"),
			@ApiResponse(responseCode = "404", description = "AppzillonBanking not reachable") })
	@Operation(summary = "Save Digi Agile Loan Details", description = "API to Save Digi Agil Loan Details")
	@PostMapping(value = "/save", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<ResponseWrapper> userInfo(@RequestBody DigiAgileUserDtls digiAgileUserDtls) {
		logger.debug("Request for digiAgileUserDtls " + digiAgileUserDtls);
		ResponseWrapper responseWrapper = new ResponseWrapper();
		logger.debug("saveDigi Agile LoanDetails request data :: {}", responseWrapper);
		Response response = digiAgileUserSaveServiceWithValidation.saveDigiAgileUserInfo(digiAgileUserDtls);
		responseWrapper.setApiResponse(response);
		logger.debug("End : save DigiAgile LoanDetails response :: {}", response);
		return new ResponseEntity<>(responseWrapper, HttpStatus.OK);
	}
}
