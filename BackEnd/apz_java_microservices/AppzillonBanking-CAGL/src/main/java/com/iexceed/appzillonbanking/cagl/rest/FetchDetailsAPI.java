package com.iexceed.appzillonbanking.cagl.rest;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.iexceed.appzillonbanking.cagl.constants.CommonConstants;
import com.iexceed.appzillonbanking.cagl.customer.payload.CustomerResponse;
import com.iexceed.appzillonbanking.cagl.customer.payload.CustomerResponseBody;
import com.iexceed.appzillonbanking.cagl.customer.payload.CustomerResponseHeader;
import com.iexceed.appzillonbanking.cagl.customer.payload.CustomerResponseWrapper;
import com.iexceed.appzillonbanking.cagl.domain.cus.GkKendraAssignment;
import com.iexceed.appzillonbanking.cagl.dto.CustData;
import com.iexceed.appzillonbanking.cagl.entity.BranchLatlong;
import com.iexceed.appzillonbanking.cagl.entity.KendraLatLongEntity;
import com.iexceed.appzillonbanking.cagl.entity.ServerDate;
import com.iexceed.appzillonbanking.cagl.payload.FetchKendraInfoRequestWrapper;
import com.iexceed.appzillonbanking.cagl.payload.KendraLatLongReqWrapper;
import com.iexceed.appzillonbanking.cagl.payload.KendraRequestWrapper;
import com.iexceed.appzillonbanking.cagl.payload.KendraResponseWrapper;
import com.iexceed.appzillonbanking.cagl.payload.LatLoangRequestWrapper;
import com.iexceed.appzillonbanking.cagl.payload.ProductResponseBody;
import com.iexceed.appzillonbanking.cagl.payload.Response;
import com.iexceed.appzillonbanking.cagl.payload.ResponseBody;
import com.iexceed.appzillonbanking.cagl.payload.ResponseHeader;
import com.iexceed.appzillonbanking.cagl.payload.ResponseObject;
import com.iexceed.appzillonbanking.cagl.repository.cus.ServerDateRepo;
import com.iexceed.appzillonbanking.cagl.service.FetchDetailsService;
import com.iexceed.appzillonbanking.cagl.user.roles.payload.UserResponse;
import com.iexceed.appzillonbanking.cagl.user.roles.payload.UserResponseBody;
import com.iexceed.appzillonbanking.cagl.user.roles.payload.UserResponseHeader;
import com.iexceed.appzillonbanking.cagl.user.roles.payload.UserResponseWrapper;
import com.iexceed.appzillonbanking.core.payload.ResponseWrapper;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@Tag(description = "application/loan", name = "LOAN")
@RequestMapping("application/loan")
public class FetchDetailsAPI {

	private static final Logger logger = LogManager.getLogger(FetchDetailsAPI.class);

	@Autowired
	private FetchDetailsService service;
	
	@Autowired
	ServerDateRepo dateRepo;
	
	@ApiResponses({ @ApiResponse(responseCode = "200", description = "AppzillonBanking API reachable"),
		@ApiResponse(responseCode = "408", description = "Service Timed Out"),
		@ApiResponse(responseCode = "500", description = "Internal Server Error"),
		@ApiResponse(responseCode = "404", description = "AppzillonBanking not reachable") })
       @Operation(summary = "Fetch Kendra details", description = "API to Fetch Kendra,Customer,eligible loans,earning members details")
      @PostMapping(value = "/cdh/kendraCustomerDetails", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<KendraResponseWrapper> fetchKendraCustomerDetails(@RequestBody KendraRequestWrapper reqWrapper) {
		KendraResponseWrapper wrapper = null;
		try
		{
			List<CustData> customerList =  service.getCustomerDetailListByKendra(reqWrapper.getApiRequest().getRequestObj().getKendraId());	
			Map<String, Object> responseMap = new HashMap<>();
			responseMap.put("customerDetails", customerList);
			
			List<ResponseObject> respObjList = (List<ResponseObject>) responseMap.get("customerDetails");
			ResponseBody respBody = ResponseBody.builder().responseObj(respObjList)
					.build();
			ResponseHeader header = ResponseHeader.builder().responseCode(CommonConstants.SUCCESS)
					.responseMessage(CommonConstants.RESP_SUCCESS_STATUS).build();
			Response resp = Response.builder().responseBody(respBody).responseHeader(header).build();
			wrapper = KendraResponseWrapper.builder().apiResponse(resp).build();
		}catch(Exception e) {
			logger.error(CommonConstants.EXCEP_OCCURED, e);
			ResponseHeader header = ResponseHeader.builder().responseCode(CommonConstants.FAILURE)
					.responseMessage(CommonConstants.RESP_FAILURE_MSG).build();
			Response resp = Response.builder().responseHeader(header).build();
			wrapper = KendraResponseWrapper.builder().apiResponse(resp).build();
			
		}
		return new ResponseEntity<>(wrapper, HttpStatus.OK);
		
	}

	@ApiResponses({ @ApiResponse(responseCode = "200", description = "AppzillonBanking API reachable"),
			@ApiResponse(responseCode = "408", description = "Service Timed Out"),
			@ApiResponse(responseCode = "500", description = "Internal Server Error"),
			@ApiResponse(responseCode = "404", description = "AppzillonBanking not reachable") })
	@Operation(summary = "Fetch Kendra details", description = "API to Fetch Kendra,Customer,eligible loans,earning members details")
	@PostMapping(value = "/cdh/kendraFetch", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<KendraResponseWrapper> fetchKendraDetails(@RequestBody KendraRequestWrapper reqWrapper) {
		String userId = null;
		String roleId = null;
		KendraResponseWrapper wrapper = null;
		try {
			userId = reqWrapper.getApiRequest().getRequestObj().getUserId();
			roleId =""; 
			try {	
				roleId=	reqWrapper.getApiRequest().getRequestObj().getRoleId();
			}
			catch(Exception e) {}
			logger.debug("fetchKendraDtls request data for the userId :: {} and roleId :: {}", userId, roleId);
			GkKendraAssignment assignedKendra = new GkKendraAssignment();
			assignedKendra.setUserId(userId);
			
			// These Kendra List need to include as it assigned Temp
			assignedKendra.setExcludedFlag("N");
			List<String> kList = service.getAssignedKendraList(assignedKendra);
			logger.debug("assigned kendra kList : {}" +kList);
			logger.error("assigned kendra kList : {}", kList);

			// These kendra list need to exclude as it has moved from current KM list
			assignedKendra.setExcludedFlag("Y");
			List<String> excludedkList = service.getAssignedKendraList(assignedKendra);
			logger.debug("assigned kendra excludedkList : {}" +excludedkList);
			logger.error("assigned kendra excludedkList : {}" ,excludedkList);

			//List<String> kList = service.getAssignedKendraList(assignedKendra);
			Map<String, Object> responseMap = null;
			if(roleId != null && !roleId.isEmpty()) {
				if(roleId.equalsIgnoreCase(CommonConstants.DEO_ROLE)) {
				logger.debug("assigned kendra list inside DEO Role : {}", kList);
			    responseMap = service.fetchCdhKendraDetailsForDeo(userId, kList,reqWrapper.getApiRequest().getRequestObj());
			}
				else if(roleId.equalsIgnoreCase("KM")){
					logger.error("Going to use BM as KM ");
					logger.error("assigned kendra list are : {}", kList);
					logger.error("assigned kendra list are : {}", excludedkList);
					logger.error("assigned kendra of userId is :{}",userId);
					responseMap = service.fetchCdhKendraDetails(userId, kList,excludedkList,roleId,reqWrapper.getApiRequest().getRequestObj());	
					// This might be a BM but acting as KM
				}
			} else{
				logger.error("assigned kendra list are : {}", kList);
				logger.error("assigned kendra list are : {}", excludedkList);
				logger.error("assigned kendra of userId are  :{}",userId);
				responseMap = service.fetchCdhKendraDetails(userId, kList,excludedkList,"",reqWrapper.getApiRequest().getRequestObj());	
			}	
         // Sonar Fix 
			List<ResponseObject> respObjList = Collections.emptyList();
			if (responseMap != null && responseMap.get("loans") != null) {
				respObjList = (List<ResponseObject>) responseMap.get("loans");
			}
			//List<CollectionsData> collectionsData = (List<CollectionsData>) responseMap.get("collections");
			ResponseBody respBody = ResponseBody.builder().responseObj(respObjList)
					//.collectionsObj(collectionsData)
					.build();
			ResponseHeader header = ResponseHeader.builder().responseCode(CommonConstants.SUCCESS)
					.responseMessage(CommonConstants.RESP_SUCCESS_STATUS).build();
			Response resp = Response.builder().responseBody(respBody).responseHeader(header).build();
			wrapper = KendraResponseWrapper.builder().apiResponse(resp).build();

		} catch (Exception ex) {
			logger.error(CommonConstants.EXCEP_OCCURED, ex);
			ResponseHeader header = ResponseHeader.builder().responseCode(CommonConstants.FAILURE)
					.responseMessage(CommonConstants.RESP_FAILURE_MSG).build();
			Response resp = Response.builder().responseHeader(header).build();
			wrapper = KendraResponseWrapper.builder().apiResponse(resp).build();
		}
		logger.debug("Final Response::{}", wrapper);
		return new ResponseEntity<>(wrapper, HttpStatus.OK);
	}

	
	@ApiResponses({ @ApiResponse(responseCode = "200", description = "AppzillonBanking API reachable"),
		@ApiResponse(responseCode = "408", description = "Service Timed Out"),
		@ApiResponse(responseCode = "500", description = "Internal Server Error"),
		@ApiResponse(responseCode = "404", description = "AppzillonBanking not reachable") })
@Operation(summary = "Fetch Kendra details", description = "API to Fetch Kendra,Customer,eligible loans,earning members details")
@PostMapping(value = "/cdh/kendraCustomerFetch", produces = MediaType.APPLICATION_JSON_VALUE)
public ResponseEntity<KendraResponseWrapper> kendraCustomerFetch(@RequestBody String requestObject) {

		logger.debug("Request for KendraCustoemrFetch" + requestObject);
		logger.error("Request for KendraCustoemrFetch" + requestObject);
		
		JSONObject reqObj  = new JSONObject(requestObject);
		
		String customerID = reqObj.getJSONObject("apiRequest").getJSONObject("requestObj").get("customerId")+"";
		
	KendraResponseWrapper wrapper = null;
	try {
		Map<String, Object> responseMap = null;
		
		
			responseMap = service.fetchCDHCustomerDetails(customerID);	
			
		List<ResponseObject> respObjList = (List<ResponseObject>) responseMap.get("loans");
		//List<CollectionsData> collectionsData = (List<CollectionsData>) responseMap.get("collections");
		ResponseBody respBody = ResponseBody.builder().responseObj(respObjList)
				//.collectionsObj(collectionsData)
				.build();
		ResponseHeader header = ResponseHeader.builder().responseCode(CommonConstants.SUCCESS)
				.responseMessage(CommonConstants.RESP_SUCCESS_STATUS).build();
		Response resp = Response.builder().responseBody(respBody).responseHeader(header).build();
		wrapper = KendraResponseWrapper.builder().apiResponse(resp).build();

	} catch (Exception ex) {
		logger.error(CommonConstants.EXCEP_OCCURED, ex);
		ResponseHeader header = ResponseHeader.builder().responseCode(CommonConstants.FAILURE)
				.responseMessage(CommonConstants.RESP_FAILURE_MSG).build();
		Response resp = Response.builder().responseHeader(header).build();
		wrapper = KendraResponseWrapper.builder().apiResponse(resp).build();
	}
	logger.debug("Final Response::{}", wrapper);
	return new ResponseEntity<>(wrapper, HttpStatus.OK);
}

	
	
	
	
	@ApiResponses({ @ApiResponse(responseCode = "200", description = "AppzillonBanking API reachable"),
			@ApiResponse(responseCode = "408", description = "Service Timed Out"),
			@ApiResponse(responseCode = "500", description = "Internal Server Error"),
			@ApiResponse(responseCode = "404", description = "AppzillonBanking not reachable") })
	@Operation(summary = "Fetch Product details", description = "API to Fetch Product,Purpose and Sub purpose details")
	@GetMapping(value = "/cdh/productFetch", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<ProductResponseBody> fetchLoanProductDetails() {

		logger.info("Product,purpose and subpurpose detail response data :: {}");
		ProductResponseBody respBody = new ProductResponseBody();
		respBody.setResponseObj(service.fetchProductDetals());
		if (!service.fetchProductDetals().isEmpty()) {
			logger.debug("product details fetch is success:"+respBody);
			logger.error("product details fetch is success : "+respBody);
		} else {
			logger.debug("product details fetch list is empty !!!");
		}
		return new ResponseEntity<>(respBody, HttpStatus.OK);
	}

	@ApiResponses({ @ApiResponse(responseCode = "200", description = "AppzillonBanking API reachable"),
			@ApiResponse(responseCode = "408", description = "Service Timed Out"),
			@ApiResponse(responseCode = "500", description = "Internal Server Error"),
			@ApiResponse(responseCode = "404", description = "AppzillonBanking not reachable") })
	@Operation(summary = "Fetch User roles", description = "API to Fetch user roles")
	@GetMapping(value = "/cdh/fetch/user/Roles", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<UserResponseWrapper> fetchUserDesignation(@RequestBody KendraRequestWrapper reqWrapper) {
		UserResponseWrapper wrapper;
		UserResponse resp;
		UserResponseHeader header;
		UserResponseBody respBody;
		String userId = null;

		userId = reqWrapper.getApiRequest().getRequestObj().getUserId();
		logger.debug("fetch user role for the user:: {}", userId);
		wrapper = new UserResponseWrapper();
		respBody = new UserResponseBody();
		respBody.setResponseObj(service.fetchUserDesignation(userId));

		header = new UserResponseHeader();
		header.setResponseCode(CommonConstants.SUCCESS);
		header.setResponseMessage(CommonConstants.RESP_SUCCESS_STATUS);
		resp = new UserResponse();
		resp.setResponseBody(respBody);
		resp.setResponseHeader(header);
		wrapper.setApiResponse(resp);
		return new ResponseEntity<>(wrapper, HttpStatus.OK);
	}

	@ApiResponses({ @ApiResponse(responseCode = "200", description = "AppzillonBanking API reachable"),
			@ApiResponse(responseCode = "408", description = "Service Timed Out"),
			@ApiResponse(responseCode = "500", description = "Internal Server Error"),
			@ApiResponse(responseCode = "404", description = "AppzillonBanking not reachable") })
	@Operation(summary = "Fetch Customer details", description = "API to Fetch Custome details")
	@GetMapping(value = "/cdh/fetch/customer", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<CustomerResponseWrapper> fetchCustomer(@RequestBody KendraRequestWrapper reqWrapper) {
		CustomerResponseWrapper wrapper;
		CustomerResponse resp;
		CustomerResponseHeader header;
		CustomerResponseBody respBody;
		String userId = null;

		userId = reqWrapper.getApiRequest().getRequestObj().getUserId();
		logger.debug("fetch customer details for the customer id:: {}", userId);
		wrapper = new CustomerResponseWrapper();
		respBody = new CustomerResponseBody();
		respBody.setResponseObj(service.fetchCustomerDetails(userId));

		header = new CustomerResponseHeader();
		header.setResponseCode(CommonConstants.SUCCESS);
		header.setResponseMessage(CommonConstants.RESP_SUCCESS_STATUS);
		resp = new CustomerResponse();
		resp.setResponseBody(respBody);
		resp.setResponseHeader(header);
		wrapper.setApiResponse(resp);
		return new ResponseEntity<>(wrapper, HttpStatus.OK);
	}

	@ApiResponses({ @ApiResponse(responseCode = "200", description = "AppzillonBanking API reachable"),
			@ApiResponse(responseCode = "408", description = "Service Timed Out"),
			@ApiResponse(responseCode = "500", description = "Internal Server Error"),
			@ApiResponse(responseCode = "404", description = "AppzillonBanking not reachable") })
	@Operation(summary = "save or create Branch LatLong details", description = "API to save or create LatLong Branch details")
	@PostMapping(value = "/cdh/branch/latlong/save", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<ResponseWrapper> createLatLongRecords(@RequestBody LatLoangRequestWrapper reqWrapper) {
		logger.debug("Branch LatLong save/create API invoked for the request wrapper " + reqWrapper);
		BranchLatlong latLong = new BranchLatlong();
		latLong.setBranch(reqWrapper.getApiRequest().getRequestObj().getBranch());
		latLong.setBranch_ID(reqWrapper.getApiRequest().getRequestObj().getBranchId());
		latLong.setLatitude(reqWrapper.getApiRequest().getRequestObj().getLatitude());
		latLong.setLongitude(reqWrapper.getApiRequest().getRequestObj().getLongitude());
		latLong.setUpdatedBy(reqWrapper.getApiRequest().getRequestObj().getUpdatedBy());
		latLong.setUpdatedOn(service.getTimeStamp());
		ResponseWrapper resWrapper = service.createBranchLatLongRecordsService(latLong);
		return new ResponseEntity<>(resWrapper, HttpStatus.OK);
	}

	@ApiResponses({ @ApiResponse(responseCode = "200", description = "AppzillonBanking API reachable"),
			@ApiResponse(responseCode = "408", description = "Service Timed Out"),
			@ApiResponse(responseCode = "500", description = "Internal Server Error"),
			@ApiResponse(responseCode = "404", description = "AppzillonBanking not reachable") })
	@Operation(summary = "save or create Kendra LatLong details", description = "API to save or create LatLong Kendra details")
	@PostMapping(value = "/cdh/kendra/latlong/save", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<ResponseWrapper> createKendraLatLongRecords(@RequestBody KendraLatLongReqWrapper reqWrapper) {
		logger.debug("Branch LatLong save/create API invoked for the request wrapper " + reqWrapper);
		KendraLatLongEntity latLong = new KendraLatLongEntity();
		latLong.setKendraID(reqWrapper.getApiRequest().getRequestObj().getKendraId());
		latLong.setLat(reqWrapper.getApiRequest().getRequestObj().getLat());
		latLong.setLongit(reqWrapper.getApiRequest().getRequestObj().getLongit());
		latLong.setUpdatedBy(reqWrapper.getApiRequest().getRequestObj().getUpdatedBy());
		latLong.setUpdatedAt(service.getTimeStamp());
		latLong.setAddress(reqWrapper.getApiRequest().getRequestObj().getAddress());
		ResponseWrapper resWrapper = service.createKendraLatLongRecordsService(latLong);
		return new ResponseEntity<>(resWrapper, HttpStatus.OK);
	}

	@ApiResponses({ @ApiResponse(responseCode = "200", description = "AppzillonBanking API reachable"),
			@ApiResponse(responseCode = "408", description = "Service Timed Out"),
			@ApiResponse(responseCode = "500", description = "Internal Server Error"),
			@ApiResponse(responseCode = "404", description = "AppzillonBanking not reachable") })
	@Operation(summary = "Fetch Branch Lat Long details", description = "API to Fetch Branch Lat Long details")
	@PostMapping(value = "/cdh/fetch/latlong", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<ResponseWrapper> fetchBranchLatLong(@RequestBody KendraRequestWrapper reqWrapper)
			throws JsonProcessingException {
		ResponseHeader header = new ResponseHeader();
		header.setResponseCode(CommonConstants.SUCCESS);
		header.setResponseMessage(CommonConstants.RESP_SUCCESS_STATUS);
		ResponseWrapper resWrapper = new ResponseWrapper();
		try {
			logger.debug("Printing incoming lat long request :{}",reqWrapper);
		if (reqWrapper.getApiRequest().getRequestObj().getLatLongFlag().equalsIgnoreCase("branch")) {
			logger.debug("fetching Lat Long details for Branch");
			resWrapper = service.fetchBranchLatLong(reqWrapper.getApiRequest().getRequestObj().getUserId());
			logger.debug("latlong response of branch {}",resWrapper);
		} else {
			logger.debug("fetching Lat Long details for Kendra ");
			resWrapper =service
					.fetchKendrLatLong(Integer.parseInt(reqWrapper.getApiRequest().getRequestObj().getUserId()));
			logger.debug("latlong response of kendra {}",resWrapper);
		}
		} catch(Exception e) {
			logger.error(CommonConstants.EXCEP_OCCURED,e);
		}
		return new ResponseEntity<>(resWrapper, HttpStatus.OK);
//		//respBody.setResponseObj(respObject);
//		resp.setResponseBody(respBody);
//		resp.setResponseHeader(header);
//		wrapper.setApiResponse(resp);
//
//	//	return new ResponseEntity(wrapper, HttpStatus.FOUND);
//		logger.debug("Printing final latlong fetch response :{}",wrapper);
//		return new ResponseEntity<LatLongRespWrapper>(wrapper, HttpStatus.OK);
	}
	
	@ApiResponses({ @ApiResponse(responseCode = "200", description = "AppzillonBanking API reachable"),
			@ApiResponse(responseCode = "408", description = "Service Timed Out"),
			@ApiResponse(responseCode = "500", description = "Internal Server Error"),
			@ApiResponse(responseCode = "404", description = "AppzillonBanking not reachable") })
	@Operation(summary = "t24_serverDate", description = "API to save or create LatLong Kendra details")
	@GetMapping(value = "/cdh/kendra/server-date", produces = MediaType.APPLICATION_JSON_VALUE)
	public List<ServerDate> getServerDate() {
		logger.debug("Creating ServerDate Api: ");
		return dateRepo.findAll();
	}
	
	@ApiResponses({ @ApiResponse(responseCode = "200", description = "AppzillonBanking API reachable"),
			@ApiResponse(responseCode = "408", description = "Service Timed Out"),
			@ApiResponse(responseCode = "500", description = "Internal Server Error"),
			@ApiResponse(responseCode = "404", description = "AppzillonBanking not reachable") })
	@Operation(summary = "Fetch Kendra Info", description = "API to Fetch Kendra details")
	@PostMapping(value = "/cdh/fetchKendraInfo", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<ResponseWrapper> fetchKendraInfo(@RequestBody FetchKendraInfoRequestWrapper reqWrapper) {
		logger.debug("fetchKendraInfo request data :: {}", reqWrapper);
		String branchId = reqWrapper.getApiRequest().getRequestObj().getBranchId();
		String nextMeetingDt = reqWrapper.getApiRequest().getRequestObj().getNextMeetingDt();
		//String roleName = reqWrapper.getApiRequest().getRequestObj().getRoleName();
		com.iexceed.appzillonbanking.core.payload.Response response = service.fetchCdhKendraInfo(branchId,
				nextMeetingDt);
		logger.debug("fetchKendraInfo response :: {}", response);
		ResponseWrapper respWrapper = ResponseWrapper.builder().apiResponse(response).build();
		logger.debug("respWrapper :: {}", respWrapper);
		return new ResponseEntity<>(respWrapper, HttpStatus.OK);
	}
}
