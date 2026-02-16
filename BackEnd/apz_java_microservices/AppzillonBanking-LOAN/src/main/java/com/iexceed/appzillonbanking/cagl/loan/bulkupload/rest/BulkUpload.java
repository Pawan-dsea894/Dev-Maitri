package com.iexceed.appzillonbanking.cagl.loan.bulkupload.rest;

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

import com.iexceed.appzillonbanking.cagl.loan.bulkupload.payload.BulkUploadRequestWrapper;
import com.iexceed.appzillonbanking.cagl.loan.bulkupload.payload.FetchBulkUploadRequestWrapper;
import com.iexceed.appzillonbanking.cagl.loan.bulkupload.payload.FetchExcelDataRequestWrapper;
import com.iexceed.appzillonbanking.cagl.loan.bulkupload.service.BulkUploadService;
import com.iexceed.appzillonbanking.core.payload.Header;
import com.iexceed.appzillonbanking.core.payload.Response;
import com.iexceed.appzillonbanking.core.payload.ResponseWrapper;
import com.iexceed.appzillonbanking.core.utils.CommonUtils;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@Tag(description = "application/bulkUpload", name = "BulkUpload")
@RequestMapping("application/bulkUpload")
public class BulkUpload {

	private static final Logger logger = LogManager.getLogger(BulkUpload.class);

	@Autowired
	private BulkUploadService bulkUploadService;

	/**
	 * Rest API Service to insert bulk upload data in DB
	 * 
	 * @author akshay.shahane
	 * @since 01.08.2024
	 * @return Object which will give status of data insertion
	 */
	@ApiResponses({ @ApiResponse(responseCode = "200", description = "AppzillonBanking API reachable"),
			@ApiResponse(responseCode = "408", description = "Service Timed Out"),
			@ApiResponse(responseCode = "500", description = "Internal Server Error"),
			@ApiResponse(responseCode = "404", description = "AppzillonBanking not reachable") })
	@Operation(summary = "Save Bulk Data", description = "API to Save Bulk Excel Data into DB")
	@PostMapping(value = "/insertExcelData", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<ResponseWrapper> insertExcelDataIntoDB(@RequestBody BulkUploadRequestWrapper requestWrapper,
			@RequestHeader(defaultValue = "APZRMB") String appId,
			@RequestHeader(defaultValue = "SaveExcelData") String interfaceId,
			@RequestHeader(defaultValue = "000000000002") String userId,
			@RequestHeader(defaultValue = "12345678") String masterTxnRefNo,
			@RequestHeader(defaultValue = "abcd1234efgh5678") String deviceId) {

		ResponseWrapper responseWrapper = new ResponseWrapper();
		Response response = new Response();
		//logger.debug("SaveExcelData request data :: {}", requestWrapper);
		Header header = CommonUtils.obtainHeader(appId, interfaceId, userId, masterTxnRefNo, deviceId);
		logger.debug("SaveExcelData Header value :: {}", header);
		try {
			response = bulkUploadService.saveExcelData(requestWrapper.getApiRequest());
			logger.debug("Final response :: {}", response);
		} catch (Exception e) {
			logger.error("Error Occured in SaveExcelData:{}", e);
		}
		responseWrapper.setApiResponse(response);
		logger.debug("End : SaveExcelData response :: {}", response);
		return new ResponseEntity<>(responseWrapper, HttpStatus.OK);
	}
	
	/**
	 * Rest API Service to fetch bulk upload data in DB
	 * 
	 * @author akshay.shahane
	 * @since 01.08.2024
	 * @return Object which will give list of records
	 */
	@ApiResponses({ @ApiResponse(responseCode = "200", description = "AppzillonBanking API reachable"),
			@ApiResponse(responseCode = "408", description = "Service Timed Out"),
			@ApiResponse(responseCode = "500", description = "Internal Server Error"),
			@ApiResponse(responseCode = "404", description = "AppzillonBanking not reachable") })
	@Operation(summary = "Fetch Bulk Data", description = "API to fetch Bulk Excel Data into DB")
	@PostMapping(value = "/fetchExcelData", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<ResponseWrapper> fetchExcelDataIntoDB(@RequestBody FetchBulkUploadRequestWrapper requestWrapper,
			@RequestHeader(defaultValue = "APZRMB") String appId,
			@RequestHeader(defaultValue = "FetchExcelData") String interfaceId,
			@RequestHeader(defaultValue = "000000000002") String userId,
			@RequestHeader(defaultValue = "12345678") String masterTxnRefNo,
			@RequestHeader(defaultValue = "abcd1234efgh5678") String deviceId) {

		ResponseWrapper responseWrapper = new ResponseWrapper();
		Response response = new Response();
		logger.debug("FetchExcelData request data :: {}", requestWrapper);
		Header header = CommonUtils.obtainHeader(appId, interfaceId, userId, masterTxnRefNo, deviceId);
		logger.debug("FetchExcelData Header value :: {}", header);
		try {
			response = bulkUploadService.fetchExcelData(requestWrapper.getApiRequest());
			logger.debug("Final response :: {}", response);
		} catch (Exception e) {
			logger.error("Error Occured in FetchExcelData:{}", e);
		}
		responseWrapper.setApiResponse(response);
		logger.debug("End : FetchExcelData response :: {}", response);
		return new ResponseEntity<>(responseWrapper, HttpStatus.OK);
	}
	
	
	/**
	 * Rest API Service to fetch records for particular docId
	 * 
	 * @author akshay.shahane
	 * @since 21.08.2024
	 * @return Object which will give status of data insertion
	 */
	@ApiResponses({ @ApiResponse(responseCode = "200", description = "AppzillonBanking API reachable"),
			@ApiResponse(responseCode = "408", description = "Service Timed Out"),
			@ApiResponse(responseCode = "500", description = "Internal Server Error"),
			@ApiResponse(responseCode = "404", description = "AppzillonBanking not reachable") })
	@Operation(summary = "Fetch Excel inserted records from DB", description = "API to Fetch inserted Data in DB from Excel")
	@PostMapping(value = "/fetchExcelInsert", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<ResponseWrapper> fetchRecords(@RequestBody FetchExcelDataRequestWrapper requestWrapper,
			@RequestHeader(defaultValue = "APZRMB") String appId,
			@RequestHeader(defaultValue = "FetchExcelInsert") String interfaceId,
			@RequestHeader(defaultValue = "000000000002") String userId,
			@RequestHeader(defaultValue = "12345678") String masterTxnRefNo,
			@RequestHeader(defaultValue = "abcd1234efgh5678") String deviceId) {

		ResponseWrapper responseWrapper = new ResponseWrapper();
		Response response = new Response();
		logger.debug("fetchExcelInsert request data :: {}", requestWrapper);
		Header header = CommonUtils.obtainHeader(appId, interfaceId, userId, masterTxnRefNo, deviceId);
		logger.debug("fetchExcelInsert Header value :: {}", header);
		try {
			response = bulkUploadService.fetchExcelInsertDataService(requestWrapper.getApiRequest());
			logger.debug("Final response :: {}", response);
		} catch (Exception e) {
			logger.error("Error Occured in fetchExcelInsert:{}", e);
		}
		responseWrapper.setApiResponse(response);
		logger.debug("End : fetchExcelInsert response :: {}", response);
		return new ResponseEntity<>(responseWrapper, HttpStatus.OK);
	}
}
