package com.iexceed.appzillonbanking.logs.service;

import java.security.SecureRandom;
import java.sql.Timestamp;
import java.time.LocalDateTime;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.codehaus.jettison.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.JsonObject;
import com.iexceed.appzillonbanking.core.constants.CommonConstants;
import com.iexceed.appzillonbanking.core.domain.ab.TbUaobApiAuditLogs;
import com.iexceed.appzillonbanking.core.payload.Header;
import com.iexceed.appzillonbanking.core.repository.ab.TbUaobApiAuditLogsRepository;
import com.iexceed.appzillonbanking.logs.payload.LogData;

@Service
public class LogExternalReqRes {

	private static final Logger logger = LogManager.getLogger(LogExternalReqRes.class);
	
	@Autowired
	private LoggingServiceProxy proxy;
	
	@Autowired
	private TbUaobApiAuditLogsRepository tbUaobApiAuditLogsRepository;
	
	@Autowired
	private LoggingService loggingService;
	
	

	public void logTransactionToDb(Header header, String request, String response, LocalDateTime startDateTime,
			LocalDateTime endDateTime, String status, String requestType, JSONObject interfaceJsonContent, Boolean isJSONAdapterCall) {
		LogData logData = new LogData();
		logData.setAppId(header.getAppId());
		logData.setDeviceId(header.getDeviceId());
		logData.setInterfaceId(header.getInterfaceId());
		logData.setMasterTxnRefNo(header.getMasterTxnRefNo());
		logData.setStTm(startDateTime);
		logData.setEndTm(endDateTime);
		logData.setStatus(status);
		logData.setEndpointType(requestType);
		logData.setRequest(request);
		logData.setResponse(response);
		logData.setTxnRefNo(getTxnRefNum(header.getUserId()));
		logData.setUserId(header.getUserId());
		proxy.logTransactionDetails(logData, interfaceJsonContent, isJSONAdapterCall);
	}

	private static String getTxnRefNum(String pUserId) {
		SecureRandom random = new SecureRandom();
		int randomno = random.nextInt(1000000);
		if (isNullOrEmpty(pUserId))
			return System.currentTimeMillis() + "" + randomno;
		else
			return (pUserId + System.currentTimeMillis() + "" + randomno);
	}

	private static boolean isNullOrEmpty(String pValue) {
		return pValue == null || pValue.isEmpty();
	}

	public void logExternalApiRequestResponse(JSONObject interfaceJsonContent, String restRequest, String response, String interfaceName, Header header, Timestamp requestTs, String apiStatus, String apiRequest) {
		if (interfaceJsonContent.has("logging") && interfaceJsonContent.getJSONObject("logging").has("externalAPILogRequired") && interfaceJsonContent.getJSONObject("logging").has("externalAPILogType")) {
			String externalAPILogRequired = interfaceJsonContent.getJSONObject("logging").getString("externalAPILogRequired");
			if ("Y".equalsIgnoreCase(externalAPILogRequired)) {
				Timestamp timestamp=new Timestamp(System.currentTimeMillis());
				String externalAPILogType = interfaceJsonContent.getJSONObject("logging").getString("externalAPILogType");	
				TbUaobApiAuditLogs dbObj = new TbUaobApiAuditLogs();
				dbObj.setSeqId(getTxnRefNum(null));
				if(loggingService.reqLogRequired(externalAPILogType)) {
					dbObj.setRequestPayload(restRequest);
				}
				logger.warn("incoming response is : {} " , response);
				if(loggingService.respLogRequired(externalAPILogType)) {
					dbObj.setResponsePayload(response);
				}
				if ("StandardMeetingDayCollection".equalsIgnoreCase(interfaceName)
				        || "RepeatCollection".equalsIgnoreCase(interfaceName)) {

				    JSONObject respObj = new JSONObject(response);
				    logger.warn("respObj is : {} ", respObj.toString());

				    if (respObj.has("header") && respObj.getJSONObject("header").has("status")) {
				        String status = respObj.getJSONObject("header").getString("status");
				        logger.warn("status for respObj is : {}", status);

				        if ("success".equalsIgnoreCase(status)) {
				            dbObj.setStatus("success");
				        } else {
				            dbObj.setStatus("failure");
				        }
				    } else {
				        logger.warn("status node not present in response, setting failure");
				        dbObj.setStatus("failure");
				    }
				}
				JSONObject apiRequestJson=new JSONObject(apiRequest);
				if(apiRequestJson.has("requestObj")) {
					logger.warn("apiRequestJson is {}",apiRequestJson);
					JSONObject requestObjJson=apiRequestJson.getJSONObject("requestObj");
					if(requestObjJson.has("applicationId")) {						
						String aplicationId=requestObjJson.getString("applicationId");
						dbObj.setApplicationId(aplicationId);	
					}
					if(requestObjJson.has("versionNo")) {	
						dbObj.setVersionNo(requestObjJson.getString("versionNo"));
					}
					if(requestObjJson.has("custDtlId")) {	
						dbObj.setCustDtlId(requestObjJson.getString("custDtlId"));
					}
				}
				else if (apiRequestJson.has("apiRequest")) {
					try {
						logger.warn("apiRequestJson is {}", apiRequestJson);
						logger.warn("Interface name is :: " + interfaceName);
						// this is CB response case
						JSONObject requestObjJson = apiRequestJson.getJSONObject("apiRequest")
								.getJSONObject("requestObj");
						if (requestObjJson.has("applicationId")) {
							String aplicationId = requestObjJson.getString("applicationId");
							dbObj.setApplicationId(aplicationId);
						}
						if (requestObjJson.has("versionNo")) {
							dbObj.setVersionNo(requestObjJson.getString("versionNo"));
						}
						if (requestObjJson.has("custDtlId")) {
							dbObj.setCustDtlId(requestObjJson.getString("custDtlId"));
						}
						if ("StandardMeetingDayCollection".equalsIgnoreCase(interfaceName)
						        || "NonMeetingCollection".equalsIgnoreCase(interfaceName)
						        || "RepeatCollection".equalsIgnoreCase(interfaceName)
						        || "GetRepeatCollection".equalsIgnoreCase(interfaceName)) {
						        if (requestObjJson != null) {
						            JSONObject body = requestObjJson.optJSONObject("body");
						            logger.warn("body is :: {} " , body.toString());
						            if (body != null) {
						                String refId = body.optString("referenceId", "").trim();
						                logger.warn("refId is : " + refId);
						                if (!refId.isEmpty()) {
						                    dbObj.setApplicationId(refId);
						                }
						            }
						        }
						    }
					} catch (Exception e) {
						logger.debug("something went wrong while parsing the json");
					}
				}
				dbObj.setApiName(interfaceName);
				dbObj.setAppId(header.getAppId());
				dbObj.setReqTs(requestTs);
				dbObj.setResTs(timestamp);
				dbObj.setApiStatus(apiStatus);
				dbObj.setCreateTs(timestamp);
				dbObj.setUpdateTs(timestamp);
				
				logger.warn("dbobj to be saved in the api audit logs table : {} " , dbObj.toString());
				if(CommonConstants.EXT_API_ERR_STATUS.equalsIgnoreCase(apiStatus)) {
					dbObj.setSchedulerStatus(CommonConstants.AUDIT_TABLE_PENDING_STATUS);
				}
				tbUaobApiAuditLogsRepository.save(dbObj);
			}
		}
	}
}
