package com.iexceed.appzillonbanking.kendra.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.iexceed.appzillonbanking.core.payload.Response;
import com.iexceed.appzillonbanking.core.payload.ResponseBody;
import com.iexceed.appzillonbanking.core.payload.ResponseHeader;
import com.iexceed.appzillonbanking.core.utils.CommonUtils;
import com.iexceed.appzillonbanking.kendra.domain.cus.LocationMappingDetails;
import com.iexceed.appzillonbanking.kendra.domain.cus.TbAsmiUser;
import com.iexceed.appzillonbanking.kendra.domain.cus.TbAsmiUserRole;
import com.iexceed.appzillonbanking.kendra.payload.AsmiUserDetailsResponse;
import com.iexceed.appzillonbanking.kendra.payload.FetchBranchIdDetails;
import com.iexceed.appzillonbanking.kendra.payload.FetchUserData;
import com.iexceed.appzillonbanking.kendra.payload.ResponseUserDetails;
import com.iexceed.appzillonbanking.kendra.payload.UserDetailsDataRequest;
import com.iexceed.appzillonbanking.kendra.payload.UserDetailsFetchRequest;
import com.iexceed.appzillonbanking.kendra.payload.UserDetailsRequest;
import com.iexceed.appzillonbanking.kendra.repository.cus.TbAsmiUserRepo;
import com.iexceed.appzillonbanking.kendra.repository.cus.TbAsmiUserRoleRepo;
import com.iexceed.appzillonbanking.kendra.repository.cus.TbUaumLocationDetailsRepo;
import com.iexceed.appzillonbanking.kendra.service.FetchUserDetailsService;

import jakarta.transaction.Transactional;

@Service
public class FetchUserDetailsService {

	private static final Logger logger = LogManager.getLogger(FetchUserDetailsService.class);

	public static final String EXCEPTION_MSG = "Something went wrong, Please try again!! :";
	public static final String EXCEPTION_OCCURED = "Exception occurred :";

	@Autowired
	private TbAsmiUserRepo tbAsmiUserRepo;
	
	@Autowired
	private TbAsmiUserRoleRepo tbAsmiUserRoleRepo;
	
	@Autowired
	private TbUaumLocationDetailsRepo tbUaumLocationDetails;

	public Response fetchUserDetailRecord(UserDetailsFetchRequest apiRequest) {
		logger.debug("Inside fetchUserDetailRecord service layer of fetchUserDetail");
		logger.debug("Printing Incoming apiRequest:" + apiRequest);
		Response response = new Response();
		ResponseHeader respHeader = new ResponseHeader();
		ResponseBody respBody = new ResponseBody();

		try {
			String userId = apiRequest.getRequestObj().getUserId();
			if (userId != null) {

				Optional<TbAsmiUser> userDetailRec = tbAsmiUserRepo.findByUserId(userId);
				Optional<List<TbAsmiUserRole>> userRoleRec = tbAsmiUserRoleRepo.findByUserId(userId);
				Optional<LocationMappingDetails> locDetails = tbUaumLocationDetails.findByUserId(userId);
				
				if (userDetailRec.isPresent()) {
					List<TbAsmiUserRole> userRoleRecords = new ArrayList<TbAsmiUserRole>();
					LocationMappingDetails userLocDetails = new LocationMappingDetails();
					
					ObjectMapper map = new ObjectMapper();
					LinkedHashMap<String, String> resp = new LinkedHashMap<>();
					
					if(userRoleRec.isPresent()) {
						userRoleRecords = userRoleRec.get();
						
						
						logger.debug("Printing userRoleRecords:"+userRoleRecords.toString());
						JSONArray userRoleArray = new JSONArray();
						for (TbAsmiUserRole role : userRoleRecords) {
			                JSONObject jsonRole = new JSONObject(map.writeValueAsString(role));
			                userRoleArray.put(jsonRole);
			            }

						//akshay.shahane :: changes for DEO role to be first index :: START
						logger.debug("printing length of userRoleArray:" + userRoleArray.length());
						if (userRoleArray.length() == 2) {
							try {
								JSONObject temp = userRoleArray.getJSONObject(0);
								logger.debug("printing temp:" + temp);
								if (temp.has("roleId")) {
									String roleIdVal = temp.getString("roleId");
									if (roleIdVal.equalsIgnoreCase("KM")) {
										userRoleArray.put(0, userRoleArray.getJSONObject(1));
										userRoleArray.put(1, temp);
										String modifiedUserRoleData = userRoleArray.toString();
										logger.debug("Printing modifiedUserRoleData:" + modifiedUserRoleData);
										resp.put("userRole", modifiedUserRoleData);
										logger.debug("Printing modified resp:" + resp);
									} else {
										resp.put("userRole", map.writeValueAsString(userRoleRecords));
									}
								} else {
									resp.put("userRole", map.writeValueAsString(userRoleRecords));
								}
							} catch (Exception e) {
								logger.error("Error occured while changing index position of data:" + e);
								resp.put("userRole", map.writeValueAsString(userRoleRecords));
							}
						} else {
							resp.put("userRole", map.writeValueAsString(userRoleRecords));
						}
						//akshay.shahane :: changes for DEO role to be first index :: END
						
						//resp.put("userRole", map.writeValueAsString(userRoleRecords));
					} else {
						resp.put("userRole", "");
					}
					TbAsmiUser userDetail = userDetailRec.get();
					if(locDetails.isPresent()) {
						userLocDetails = locDetails.get();
						resp.put("userLocDet", map.writeValueAsString(userLocDetails));
					} else {
						resp.put("userLocDet", "");
					}
					
					resp.put("userDetail", map.writeValueAsString(userDetail));
					
					
					String finalResp = new Gson().toJson(resp, LinkedHashMap.class);
					respBody.setResponseObj(finalResp);
					CommonUtils.generateHeaderForSuccess(respHeader);
					respHeader.setResponseMessage("Fetching Success");
					response.setResponseBody(respBody);
					response.setResponseHeader(respHeader);
				} else {
					logger.error("No User Present !!!");
					respBody.setResponseObj("User is not present in Database");
					CommonUtils.generateHeaderForFailure(respHeader, EXCEPTION_OCCURED);
					respHeader.setResponseMessage(EXCEPTION_OCCURED + "User is not present in Database");
					response.setResponseBody(respBody);
					response.setResponseHeader(respHeader);
				}

			}
		} catch (Exception e) {
			logger.error(EXCEPTION_OCCURED + e);
			respBody.setResponseObj(e.getMessage().toString());
			CommonUtils.generateHeaderForFailure(respHeader, EXCEPTION_OCCURED);
			respHeader.setResponseMessage(EXCEPTION_OCCURED + e.getMessage().toString());
			response.setResponseBody(respBody);
			response.setResponseHeader(respHeader);
		}
		return response;
	}
	
	public Response fetchUserRoleDetailRecords(UserDetailsFetchRequest apiRequest) {
		logger.debug("Inside fetchUserRoleDetailRecord service layer of fetchUserRoleDetail");
		logger.debug("Printing Incoming apiRequest:" + apiRequest);
		Response response = new Response();
		ResponseHeader respHeader = new ResponseHeader();
		ResponseBody respBody = new ResponseBody();

		try {
			String userId = apiRequest.getRequestObj().getUserId();
			if (userId != null) {

				Optional<List<TbAsmiUserRole>> userRoleRec = tbAsmiUserRoleRepo.findByUserId(userId);
				ObjectMapper map = new ObjectMapper();
				LinkedHashMap<String, String> resp = new LinkedHashMap<>();
				List<TbAsmiUserRole> userRoleRecords = new ArrayList<TbAsmiUserRole>();
				
				if (userRoleRec.isPresent()) {
					userRoleRecords = userRoleRec.get();
					resp.put("userRole", map.writeValueAsString(userRoleRecords));
				} else {
					resp.put("userRole", "");
				}
				// Getting the flag for COLLECTION or LOAN user
				Optional<TbAsmiUser> userDetailRec = tbAsmiUserRepo.findByUserId(userId);
				resp.put("user_type", userDetailRec.map(TbAsmiUser::getUser_type).orElse(""));
				
				String finalResp = new Gson().toJson(resp, LinkedHashMap.class);
				respBody.setResponseObj(finalResp);
				CommonUtils.generateHeaderForSuccess(respHeader);
				respHeader.setResponseMessage("Fetching Success");
				response.setResponseBody(respBody);
				response.setResponseHeader(respHeader);
			} else {
				logger.error("No User Role Present !!!");
				respBody.setResponseObj("User Role is not present in Database");
				CommonUtils.generateHeaderForFailure(respHeader, EXCEPTION_OCCURED);
				respHeader.setResponseMessage(EXCEPTION_OCCURED + "User Role is not present in Database");
				response.setResponseBody(respBody);
				response.setResponseHeader(respHeader);
			}
		} catch (Exception e) {
			logger.error(EXCEPTION_OCCURED + e);
			respBody.setResponseObj(e.getMessage().toString());
			CommonUtils.generateHeaderForFailure(respHeader, EXCEPTION_OCCURED);
			respHeader.setResponseMessage(EXCEPTION_OCCURED + e.getMessage().toString());
			response.setResponseBody(respBody);
			response.setResponseHeader(respHeader);
		}
		return response;
	}
	
	@Transactional
	public String fetchBranchId(FetchBranchIdDetails fetchBranchIdDetails) {
		try {
			String userId = fetchBranchIdDetails.getUserId();
			if (userId != null && !userId.isEmpty()) {
				String branchId = tbAsmiUserRepo.fetchBranchId(userId);
				if (branchId == null) {
					branchId = tbUaumLocationDetails.fetchBranchID(userId);
					logger.error("branchId is not present :" +branchId);
				} else {
					logger.debug("Branch ID fetched successfully: {}", branchId);
				}
				return branchId;
			}
		} catch (Exception e) {
			logger.error("An error occurred while fetching branch ID: {}", e.getMessage(), e);
		}
		return null;
	}

	@Transactional
	public Response fetchUserDetailsOnBranchId(UserDetailsRequest apiRequest) {
		logger.debug("Inside fetchDetailsOnBranchId service layer of fetchDetail");
		logger.debug("Printing Incoming apiRequest:" + apiRequest);
		Response response = new Response();
		ResponseHeader respHeader = new ResponseHeader();
		ResponseBody respBody = new ResponseBody();
		JSONObject responseObject = new JSONObject();
		try {
			String branchId = apiRequest.getRequestObj().getBranchId();
			List<ResponseUserDetails> userDetails = tbAsmiUserRepo.findUsersDetailsByAddInfo2(branchId);
			logger.debug("Fetched Details: {}", userDetails);
			if (userDetails.isEmpty()) {
				logger.warn("No user details found for branchId: {}", branchId);
			}
			responseObject.put("usersDetails", userDetails);
			respBody.setResponseObj(responseObject.toString());
			CommonUtils.generateHeaderForSuccess(respHeader);
			respHeader.setResponseMessage("User details fetched successfully.");
			response.setResponseBody(respBody);
			response.setResponseHeader(respHeader);
		} catch (Exception e) {
			logger.error("An error occurred: {}", e.getMessage(), e);
			respBody.setResponseObj("An error occurred: " + e.getMessage());
			CommonUtils.generateHeaderForFailure(respHeader, "GENERAL_ERROR");
			respHeader.setResponseMessage("An error occurred: " + e.getMessage());
			response.setResponseBody(respBody);
			response.setResponseHeader(respHeader);
		}
		return response;
	}
	
	@Transactional
	public AsmiUserDetailsResponse fetchUserDetailsData1(FetchUserData fetchUserData) {
		logger.debug("Inside fetchUserDetailsData service layer.");
		logger.debug("fetchUserData.", fetchUserData);
		String userId = fetchUserData.getUserId();
		logger.debug("Fetch userId data", userId);
		if (userId != null && !userId.trim().isEmpty()) {
			return tbAsmiUserRepo.fetchUserDetails(userId).orElse(null);
		}
		return null;
	}
}
