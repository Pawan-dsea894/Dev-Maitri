package com.iexceed.appzillonbanking.kendra.service;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.google.gson.Gson;
import com.iexceed.appzillonbanking.core.payload.Header;
import com.iexceed.appzillonbanking.core.payload.Response;
import com.iexceed.appzillonbanking.core.payload.ResponseBody;
import com.iexceed.appzillonbanking.core.payload.ResponseHeader;
import com.iexceed.appzillonbanking.core.payload.ResponseWrapper;
import com.iexceed.appzillonbanking.core.utils.CommonUtils;
import com.iexceed.appzillonbanking.kendra.domain.cus.SRCreation;
import com.iexceed.appzillonbanking.kendra.domain.cus.TbAsmiUser;
import com.iexceed.appzillonbanking.kendra.payload.FetchAllSRRecordsRequest;
import com.iexceed.appzillonbanking.kendra.payload.MeetingDateUpdateRequest;
import com.iexceed.appzillonbanking.kendra.payload.MeetingDateUpdateRequestFields;
import com.iexceed.appzillonbanking.kendra.payload.MeetingDayUpdateRequest;
import com.iexceed.appzillonbanking.kendra.payload.MeetingDayUpdateRequestFields;
import com.iexceed.appzillonbanking.kendra.payload.MeetingFrequencyUpdateRequest;
import com.iexceed.appzillonbanking.kendra.payload.MeetingFrequencyUpdateRequestFields;
import com.iexceed.appzillonbanking.kendra.payload.MeetingTimeUpdateRequest;
import com.iexceed.appzillonbanking.kendra.payload.MeetingTimeUpdateRequestFields;
import com.iexceed.appzillonbanking.kendra.payload.SRApplicationFetchRequest;
import com.iexceed.appzillonbanking.kendra.payload.SRApplicationFetchRequestFields;
import com.iexceed.appzillonbanking.kendra.payload.SRApplicationRequest;
import com.iexceed.appzillonbanking.kendra.payload.SRApplicationRequestFields;
import com.iexceed.appzillonbanking.kendra.payload.SRUpdateApplicationRequest;
import com.iexceed.appzillonbanking.kendra.payload.SRUpdateApplicationRequestFields;
import com.iexceed.appzillonbanking.kendra.repository.cus.SRCreationRepository;
import com.iexceed.appzillonbanking.kendra.repository.cus.TOfficeDetailsRepo;
import com.iexceed.appzillonbanking.kendra.repository.cus.TbAsmiUserRepo;

import reactor.core.publisher.Mono;

@Service
public class SRCreationService {

	private static final Logger logger = LogManager.getLogger(SRCreationService.class);

	public static final String EXCEPTION_MSG = "Something went wrong, Please try again!!";
	public static final String EXCEPTION_OCCURED = "Exception occurred :";

	@Autowired
	private SRCreationRepository srCreationRepository;

	@Autowired
	private KendraAssignmentService kService;

	@Autowired
	private KendraService kendraService;
	
	@Autowired
	private TbAsmiUserRepo tbAsmiUserRepo;
	
	@Autowired
	private TOfficeDetailsRepo tbOfficeDataRepository;

	public Response saveApplication(SRApplicationRequest apiRequest, Header header) {
		logger.debug("Inside saveApplication service layer");
		logger.debug("Printing Incoming apirequest:" + apiRequest);
		SRCreation srcreation = new SRCreation();
		SRApplicationRequestFields requestObj = apiRequest.getRequestObj();
		Response response = new Response();
		ResponseHeader respHeader = new ResponseHeader();
		ResponseBody respBody = new ResponseBody();
		JSONObject responeObject = new JSONObject();
		String applicationId = kService.generateRandomNumber(17);

		try {
			String createdTime = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss:SSSXXX")
					.format(new Timestamp(System.currentTimeMillis()));
			logger.debug("Current TimeStamp:" + createdTime);
			String appStatus = "";
			if (requestObj.getAppStatus() != null) {
				appStatus = requestObj.getAppStatus().toUpperCase();
			}
			logger.debug("appStatus in UpperCase:" + appStatus);
			srcreation.setApplicationId(applicationId);
			srcreation.setSrType(requestObj.getSrType());
			srcreation.setPayload(requestObj.getPayload());
			srcreation.setCreatedRole(requestObj.getCreatedRole());
			srcreation.setCurrRole(requestObj.getCurrRole());
			srcreation.setNextRole(requestObj.getNextRole());
			srcreation.setAppStatus(appStatus);
			srcreation.setCreateTs(createdTime);
			srcreation.setCreateBy(requestObj.getCreateBy());
			srcreation.setUpdateTs(createdTime);
			srcreation.setUpdateBy(requestObj.getUpdateBy());
			srcreation.setRemarks(requestObj.getRemarks());
			srcreation.setAddInfo1(requestObj.getAddInfo1());
			srcreation.setAddInfo2(requestObj.getAddInfo2());
			srcreation.setBranchId(requestObj.getBranchId());
			logger.debug("Printing entity request before insertion:" + srcreation);

			srCreationRepository.save(srcreation);
			logger.debug("Record inserted into PUBLIC.TB_UASR_APPLICATION table");

			responeObject.put("applicationId", applicationId);
			responeObject.put("branchId", requestObj.getBranchId());
			responeObject.put("versionNum", 1);
			responeObject.put("status", "success");
			responeObject.put("message", "SR Application details saved successfully!!");
			respBody.setResponseObj(responeObject.toString());
			CommonUtils.generateHeaderForSuccess(respHeader);
			response.setResponseBody(respBody);
			response.setResponseHeader(respHeader);

			logger.debug("Printing final response: " + response);
		} catch (Exception e) {
			logger.error(EXCEPTION_OCCURED + e);
			respBody.setResponseObj("");
			CommonUtils.generateHeaderForFailure(respHeader, EXCEPTION_OCCURED);

			response.setResponseBody(respBody);
			response.setResponseHeader(respHeader);
		}
		return response;
	}

	public Mono<ResponseWrapper> updateSRRecord(SRUpdateApplicationRequest apiRequest, Header header) {
		Response response = new Response();
		ResponseHeader respHeader = new ResponseHeader();
		ResponseBody respBody = new ResponseBody();
		ResponseWrapper respWrapper = new ResponseWrapper();
		SRUpdateApplicationRequestFields requestObj = apiRequest.getRequestObj();
		JSONObject responeObject = new JSONObject();
		logger.debug("Inside updateSRRecord");
		logger.debug("Incoming API Request : " + apiRequest);

		String applicationId = apiRequest.getRequestObj().getApplicationId();
		logger.debug("applicationId : " + applicationId);

		Optional<SRCreation> srupdtionOpt = srCreationRepository.findByApplicationIdAndSrType(applicationId,
				apiRequest.getRequestObj().getSrType());
		logger.debug("SRCreation from DB : " + srupdtionOpt);
		if (srupdtionOpt.isPresent()) {
			SRCreation srupdtion = srupdtionOpt.get();

			// forming below request to call T24-API : START
			String appStatus = srupdtion.getAppStatus();
			String branchId = srupdtion.getBranchId();

			if ("Approved".equalsIgnoreCase(apiRequest.getRequestObj().getAppStatus())) {
				logger.debug("AppStatus : " + apiRequest.getRequestObj().getAppStatus());
				if ("DayChanges".equalsIgnoreCase(requestObj.getSrType())) {
					logger.debug("SRType is DayChanges ");
					MeetingDayUpdateRequest meetingDayUpdateRequest = new MeetingDayUpdateRequest();
					MeetingDayUpdateRequestFields meetingDayUpdateRequestFields = new MeetingDayUpdateRequestFields();
					meetingDayUpdateRequest.setAppId(apiRequest.getAppId());
					meetingDayUpdateRequest.setInterfaceName("meetingDayUpdate");
					meetingDayUpdateRequest.setServiceName("meetingDayUpdate");
					meetingDayUpdateRequest.setUserId(apiRequest.getUserId());

					String payload = srupdtion.getPayload();
					JSONObject paylodJson = new JSONObject(payload);

					String dayVal = "";
					int kendraId = 0;
					if (paylodJson.has("newDay")) {
						dayVal = paylodJson.getString("newDay");
						dayVal = dayVal.substring(0, 3).toUpperCase();
					}

					if (paylodJson.has("kendraId")) {
						kendraId = paylodJson.getInt("kendraId");
					}

					meetingDayUpdateRequestFields.setKendraId(kendraId + "");
					meetingDayUpdateRequestFields.setNextMeetingDay(dayVal);
					meetingDayUpdateRequestFields.setBranchId(branchId);
					meetingDayUpdateRequest.setRequestObj(meetingDayUpdateRequestFields);

					logger.debug("Printing constructed request to call T24 API: {}", meetingDayUpdateRequest);
					Mono<ResponseWrapper> t24ServiceResponse = kendraService.meetingDayUpdate(meetingDayUpdateRequest,
							header);
					
					return t24ServiceResponse.flatMap(responseWrapper -> {
	                    logger.debug("Received Response Wrapper of DayChanges: {}", responseWrapper);
	                    String responseObj = responseWrapper.getApiResponse().getResponseBody().getResponseObj();
	                    responeObject.put("srType", "DayChanges");
	                    responeObject.put("t24Response", responseObj);	
	                    responeObject.put("applicationId", applicationId);
	                    responeObject.put("versionNum", 1);
	                    responeObject.put("status", srupdtion.getAppStatus());

	                    if (srupdtion.getAppStatus().equalsIgnoreCase("success")) {
	                        responeObject.put("message", "SR Application Updated successfully!!");
	                    }
	                    
	                    updateSRRecordInDB(responseObj, apiRequest, srupdtion);

	                    respBody.setResponseObj(responeObject.toString());
	                    CommonUtils.generateHeaderForSuccess(respHeader);
	                    response.setResponseBody(respBody);
	                    response.setResponseHeader(respHeader);
	                    respWrapper.setApiResponse(response);
	                    return Mono.just(respWrapper); 
	                });

				} else if ("FrequencyChanges".equalsIgnoreCase(requestObj.getSrType())) {
					logger.debug("SRType is FrequencyChanges");
					MeetingFrequencyUpdateRequest meetingFrequencyUpdateRequest = new MeetingFrequencyUpdateRequest();
					MeetingFrequencyUpdateRequestFields meetingFrequencyUpdateRequestFields = new MeetingFrequencyUpdateRequestFields();
					meetingFrequencyUpdateRequest.setAppId(apiRequest.getAppId());
					meetingFrequencyUpdateRequest.setInterfaceName("meetingFrequencyUpdate");
					meetingFrequencyUpdateRequest.setServiceName("meetingFrequencyUpdate");
					meetingFrequencyUpdateRequest.setUserId(apiRequest.getUserId());

					String payload = srupdtion.getPayload();
					JSONObject paylodJson = new JSONObject(payload);
					String freqVal = "";
					int kendraId = 0;

					if (paylodJson.has("newFrequency")) {
						freqVal = paylodJson.getString("newFrequency");
						freqVal = freqVal.toUpperCase();
					}
					if (paylodJson.has("kendraId")) {
						kendraId = paylodJson.getInt("kendraId");
					}

					meetingFrequencyUpdateRequestFields.setKendraId(kendraId + "");
					meetingFrequencyUpdateRequestFields.setMeetingFrequency(freqVal);
					meetingFrequencyUpdateRequestFields.setBranchId(branchId);
					meetingFrequencyUpdateRequest.setRequestObj(meetingFrequencyUpdateRequestFields);

					logger.debug("Printing constructed request to call T24 API: {}", meetingFrequencyUpdateRequest);
					Mono<ResponseWrapper> t24ServiceResponse = kendraService
							.meetingFrequencyUpdate(meetingFrequencyUpdateRequest, header);
					
					return t24ServiceResponse.flatMap(responseWrapper -> {
	                    logger.debug("Received Response Wrapper of FrequencyChanges: {}", responseWrapper);
	                    String responseObj = responseWrapper.getApiResponse().getResponseBody().getResponseObj();
	                    responeObject.put("srType", "FrequencyChanges");
	                    responeObject.put("t24Response", responseObj);	
	                    responeObject.put("applicationId", applicationId);
	                    responeObject.put("versionNum", 1);
	                    responeObject.put("status", srupdtion.getAppStatus());

	                    if (srupdtion.getAppStatus().equalsIgnoreCase("success")) {
	                        responeObject.put("message", "SR Application Updated successfully!!");
	                    }
	                    
	                    updateSRRecordInDB(responseObj, apiRequest, srupdtion);

	                    respBody.setResponseObj(responeObject.toString());
	                    CommonUtils.generateHeaderForSuccess(respHeader);
	                    response.setResponseBody(respBody);
	                    response.setResponseHeader(respHeader);
	                    respWrapper.setApiResponse(response);
	                    return Mono.just(respWrapper); 
	                });

				} else if ("TimeChanges".equalsIgnoreCase(requestObj.getSrType())) {
					logger.debug("SRType is TimeChanges");
					MeetingTimeUpdateRequest meetingTimeUpdateRequest = new MeetingTimeUpdateRequest();
					MeetingTimeUpdateRequestFields meetingTimeUpdateRequestFields = new MeetingTimeUpdateRequestFields();
					meetingTimeUpdateRequest.setAppId(apiRequest.getAppId());
					meetingTimeUpdateRequest.setInterfaceName("meetingFrequencyUpdate");
					meetingTimeUpdateRequest.setServiceName("meetingFrequencyUpdate");
					meetingTimeUpdateRequest.setUserId(apiRequest.getUserId());

					String payload = srupdtion.getPayload();
					JSONObject paylodJson = new JSONObject(payload);
					int kendraId = 0;
					String meetingTimeFrom = "";
					String meetingTimeTo = "";
					if (paylodJson.has("kendraId")) {
						kendraId = paylodJson.getInt("kendraId");
						logger.debug("kendraId from payload is :"+kendraId);
					}
					if (paylodJson.has("newStartTime")) {
						meetingTimeFrom = paylodJson.getString("newStartTime");
						logger.debug("meetingTimeFrom from payload is :"+meetingTimeFrom);
					}
					if (paylodJson.has("newEndTime")) {
						meetingTimeTo = paylodJson.getString("newEndTime");
						logger.debug("meetingTimeTo from payload is :"+meetingTimeTo);
					}
					meetingTimeUpdateRequestFields.setKendraId(kendraId + "");
					meetingTimeUpdateRequestFields.setMeetingTimeFrom(meetingTimeFrom);
					meetingTimeUpdateRequestFields.setMeetingTimeTo(meetingTimeTo);
					meetingTimeUpdateRequestFields.setBranchId(branchId);
					meetingTimeUpdateRequest.setRequestObj(meetingTimeUpdateRequestFields);

					logger.debug("Printing constructed request to call T24 API: {}", meetingTimeUpdateRequest);
					Mono<ResponseWrapper> t24ServiceResponse = kendraService.meetingTimeUpdate(meetingTimeUpdateRequest,
							header);
					
					return t24ServiceResponse.flatMap(responseWrapper -> {
	                    logger.debug("Received Response Wrapper of TimeChanges: {}", responseWrapper);
	                    String responseObj = responseWrapper.getApiResponse().getResponseBody().getResponseObj();
	                    responeObject.put("srType", "TimeChanges");
	                    responeObject.put("t24Response", responseObj);	
	                    responeObject.put("applicationId", applicationId);
	                    responeObject.put("versionNum", 1);
	                    responeObject.put("status", srupdtion.getAppStatus());

	                    if (srupdtion.getAppStatus().equalsIgnoreCase("success")) {
	                        responeObject.put("message", "SR Application Updated successfully!!");
	                    }
	                    
	                    updateSRRecordInDB(responseObj, apiRequest, srupdtion);

	                    respBody.setResponseObj(responeObject.toString());
	                    CommonUtils.generateHeaderForSuccess(respHeader);
	                    response.setResponseBody(respBody);
	                    response.setResponseHeader(respHeader);
	                    respWrapper.setApiResponse(response);
	                    return Mono.just(respWrapper); 
	                });
				}
			} else {
				logger.debug("Application is rejected");
				srupdtion.setAppStatus(apiRequest.getRequestObj().getAppStatus());
				srCreationRepository.save(srupdtion);
				
                responeObject.put("applicationId", applicationId);
                responeObject.put("versionNum", 1);
                responeObject.put("status", apiRequest.getRequestObj().getAppStatus());
                respBody.setResponseObj(responeObject.toString());
                CommonUtils.generateHeaderForSuccess(respHeader);
                response.setResponseBody(respBody);
                response.setResponseHeader(respHeader);
                respWrapper.setApiResponse(response);
                return Mono.just(respWrapper); 
			}
		}
			// T24-API Calling : END
		return Mono.just(respWrapper);

	}
	
	private void updateSRRecordInDB(String t24Response, SRUpdateApplicationRequest apiRequest, SRCreation srupdtion) {
		logger.debug("Inside saveSRRecordInDB");
		SRUpdateApplicationRequestFields requestObj = apiRequest.getRequestObj();
		JSONObject responseObject = new JSONObject(t24Response);
		logger.debug("Printing responseObject:" + responseObject);
		String createdTime = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss:SSSXXX")
				.format(new Timestamp(System.currentTimeMillis()));
		logger.debug("Current TimeStamp:" + createdTime);
		if (responseObject.has("header")) {
			JSONObject header = responseObject.getJSONObject("header");
			if (header.has("status")) {
				String status = header.getString("status");
				if ("success".equalsIgnoreCase(status)) {
					if (requestObj.getAppStatus() != null) {
						srupdtion.setAppStatus(requestObj.getAppStatus());
					}
					if (requestObj.getCreateBy() != null) {
						srupdtion.setCreateBy(requestObj.getCreateBy());
					}
					if (requestObj.getUpdateTs() != null) {
						srupdtion.setUpdateBy(requestObj.getUpdateBy());
					}
					if (requestObj.getRemarks() != null) {
						srupdtion.setRemarks(requestObj.getRemarks());
					}
					srupdtion.setUpdateTs(createdTime);
					logger.debug("Updated entity request is : " + srupdtion);
					srCreationRepository.save(srupdtion);
					logger.debug("Record Updated into PUBLIC.TB_UASR_APPLICATION table");

				}
			} else {
				logger.debug("Record not updated as t24Response got failed response");
			}
		}
	}
	

	public Response fetchSRApplication(SRApplicationFetchRequest apiRequest, Header header) {
		logger.debug("===Inside fetchSRApplication===");
		logger.debug("Incoming API Request : " + apiRequest);
		Response response = new Response();
		ResponseHeader respHeader = new ResponseHeader();
		ResponseBody respBody = new ResponseBody();
		SRCreation srCreation = new SRCreation();
		JSONObject responeObject = new JSONObject();
		Optional<SRCreation> SRcreationRecord = Optional.of(srCreation);
		SRApplicationFetchRequestFields requestObj = apiRequest.getRequestObj();
		String currRole = requestObj.getCurrRole();
		String createBy = requestObj.getCreateBy();
		String appId = requestObj.getApplicationId();
		String srType = requestObj.getSrType();
		try {
			if (currRole != null && createBy != null && appId != null) {
				SRcreationRecord = srCreationRepository.findByCurrRoleAndCreateByAndApplicationIdAndSrType(currRole,
						createBy, appId, srType);
			} else if (appId != null && currRole == null && createBy != null) {
				SRcreationRecord = srCreationRepository.findByCreateByAndApplicationIdAndSrType(createBy, appId,
						srType);
			} else if (appId != null && currRole != null && createBy == null) {
				SRcreationRecord = srCreationRepository.findByCurrRoleAndApplicationIdAndSrType(currRole, appId,
						srType);
			}

			if (SRcreationRecord.isPresent()) {
				srCreation = SRcreationRecord.get();
			}
			responeObject.put("response", new Gson().toJson(srCreation));
			respBody.setResponseObj(responeObject.toString());
			CommonUtils.generateHeaderForSuccess(respHeader);
			response.setResponseBody(respBody);
			response.setResponseHeader(respHeader);
		} catch (Exception e) {
			logger.debug(EXCEPTION_OCCURED + " while fetching the SRCreation Record:" + e);
		}
		return response;
	}

	public Response fetchAllSRApplication(FetchAllSRRecordsRequest apiRequest, Header header) {
		Response response = new Response();
		ResponseHeader respHeader = new ResponseHeader();
		ResponseBody respBody = new ResponseBody();
		JSONObject responeObject = new JSONObject();
		
		List<SRCreation> allAMRecords = new ArrayList<>();
		logger.debug("Inside fetchAllSRApplication, request is:" + apiRequest);

		try {
			String branchId = apiRequest.getRequestObj().getBranchId();
			String createBy = apiRequest.getRequestObj().getCreatedBy();
			String userRole = apiRequest.getRequestObj().getUserRole();
			Optional<List<SRCreation>> opSRRecords = Optional.empty();

			if (branchId != null && !branchId.equals("")) {
				logger.debug("BM role data fetching");
				opSRRecords = srCreationRepository.findByBranchId(branchId);
			} else if (userRole != null && !userRole.isEmpty() && "AM".equalsIgnoreCase(userRole)) {
				logger.debug("==============* AM role data fetch START *==============");

				Optional<TbAsmiUser> userRecordOp = tbAsmiUserRepo.findByAmId(apiRequest.getRequestObj().getAmId());
				
				if (userRecordOp.isPresent()) {
					
					TbAsmiUser tbAsmiUser = userRecordOp.get();
					logger.debug("userRecordOp is present, data is:" + tbAsmiUser);
					String areaId = tbAsmiUser.getAddInfo2();
					logger.debug("areaId is:" + areaId);
					List<String> branchDataOfAm = tbOfficeDataRepository.findByAreaId(areaId);
					logger.debug("branchDataOfAm is:" + branchDataOfAm);
					allAMRecords = srCreationRepository.findAllAMRecords(branchDataOfAm);
					logger.debug("Printing final AM records:" + allAMRecords);

					String finalResponse = new Gson().toJson(allAMRecords);
					responeObject.put("apiResponse", finalResponse);
					respBody.setResponseObj(responeObject.toString());
					CommonUtils.generateHeaderForSuccess(respHeader);
					response.setResponseBody(respBody);
					response.setResponseHeader(respHeader);
					logger.debug("Printing Final response :::" + response);

					logger.debug("==============* AM role data fetch END ==============");
					return response;
				} else {
					logger.debug("User not Present in DB");

					responeObject.put("apiResponse", "User not Present in DB");
					respBody.setResponseObj(responeObject.toString());
					CommonUtils.generateHeaderForSuccess(respHeader);
					response.setResponseBody(respBody);
					response.setResponseHeader(respHeader);
					logger.debug("==============* AM role data fetch END ==============");
					return response;
				}
			} else {
				logger.debug("KM role data fetching");
				opSRRecords = srCreationRepository.findByCreateBy(createBy);
			}

			if (opSRRecords.isPresent()) {
				List<SRCreation> SRRecords = opSRRecords.get();
				responeObject.put("apiResponse", SRRecords);
				respBody.setResponseObj(responeObject.toString());
				CommonUtils.generateHeaderForSuccess(respHeader);
				response.setResponseBody(respBody);
				response.setResponseHeader(respHeader);
			}

		} catch (Exception e) {
			logger.debug(EXCEPTION_OCCURED + " while fetching the SRCreation Record:" + e);
		}
		logger.debug("Printing Final response:" + response);
		return response;

	}
}
