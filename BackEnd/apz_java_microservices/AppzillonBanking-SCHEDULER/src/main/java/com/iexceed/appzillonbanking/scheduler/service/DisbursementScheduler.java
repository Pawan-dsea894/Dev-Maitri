package com.iexceed.appzillonbanking.scheduler.service;

import java.net.URISyntaxException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.iexceed.appzillonbanking.core.constants.CommonConstants;
import com.iexceed.appzillonbanking.core.domain.ab.TbUaobApiAuditLogs;
import com.iexceed.appzillonbanking.core.payload.Header;
import com.iexceed.appzillonbanking.core.payload.Response;
import com.iexceed.appzillonbanking.core.repository.ab.TbUaobApiAuditLogsRepository;
import com.iexceed.appzillonbanking.interfaceAdapter.service.InterfaceAdapter;

import reactor.core.publisher.Mono;

@Service
public class DisbursementScheduler {

	private static final Logger LOG = LogManager.getLogger(DisbursementScheduler.class);

	@Autowired
	TbUaobApiAuditLogsRepository apiAuditTableRepo; 
	
	@Autowired
	private InterfaceAdapter interfaceAdapter;

	private static final Logger logger = LogManager.getLogger(DisbursementScheduler.class.getName());

	/*
	@Scheduled(cron = "${ab.common.notificationScheduleCron}")
	public void scheduleTask() throws URISyntaxException, JsonProcessingException {
		try {
			logger.debug("Disbursement scheduler started at "  + LocalDateTime.now().toString());
			reTryApi();
			logger.debug("Disbursement scheduler ended at "  + LocalDateTime.now().toString());
		} catch (Exception e) {
			logger.error("Exception in execute method of ExecuteScheduler in ", e);
		}
	}
	*/
	
	public void reTryApi() {
		try {
			List<TbUaobApiAuditLogs> apiReqToProcessList=apiAuditTableRepo.findTop100BySchedulerStatusOrderByReqTsDesc(CommonConstants.AUDIT_TABLE_PENDING_STATUS);
			for (TbUaobApiAuditLogs obj : apiReqToProcessList) {
				obj.setSchedulerStatus(CommonConstants.AUDIT_TABLE_INPROGRESS_STATUS);
			}
			apiAuditTableRepo.saveAll(apiReqToProcessList);
			if (!apiReqToProcessList.isEmpty()) {
				for (TbUaobApiAuditLogs recordToProcess : apiReqToProcessList) {
					LOG.debug("Current recordToProcess::" + recordToProcess);
					frameRequestAndCallAPI(recordToProcess);
				}
			}
		} catch (Exception ex) {
			LOG.error("Exception in reTryApi method. ", ex);
		}
	}

	private  Mono<Object> frameRequestAndCallAPI(TbUaobApiAuditLogs recordToProcess) throws JsonMappingException, JsonProcessingException {
		String requestPayloadStr=recordToProcess.getRequestPayload();
		String interfaceName=recordToProcess.getApiName()+"_API_RETRY";
		Header header=new Header();
		header.setAppId(recordToProcess.getAppId());
		header.setInterfaceId(interfaceName);
		if(CommonConstants.CB_CHECK_INTF_JSON_NAME.equalsIgnoreCase(recordToProcess.getApiName()) || 
				CommonConstants.PRECLOSURE_INTF_JSON_NAME.equalsIgnoreCase(recordToProcess.getApiName())) { // CB Check and Disbursement APIs
			Object request = new ObjectMapper().readValue(requestPayloadStr, Object.class);
			Mono<Object> responseMonoObj= interfaceAdapter.callExternalService(header, request, interfaceName, true);
			responseMonoObj.doOnError(error -> {
				LOG.error("Inside doOnError in frameRequestAndCallAPI method "+error);
				updateApiAuditTable(recordToProcess, CommonConstants.AUDIT_TABLE_FAIL_STATUS);
			}).subscribe(val -> {
				LOG.debug("Inside subscribe of frameRequestAndCallAPI method" + val);
				this.checkForApiAuditTableStatus(val, recordToProcess);
			}, error -> {
				LOG.error("Inside error of frameRequestAndCallAPI method" + error);
				updateApiAuditTable(recordToProcess, CommonConstants.AUDIT_TABLE_FAIL_STATUS);
			});	
		} 	
		return Mono.empty();
	}

	private void checkForApiAuditTableStatus(Object val, TbUaobApiAuditLogs recordToProcess) {
		if (val instanceof Response) {
			LOG.debug("Response is an instance of Response POJO in checkForApiAuditTableStatus");
			try {
				String extApiResp = new ObjectMapper().writeValueAsString(val);
				JSONObject responseJSON = new JSONObject(extApiResp);
				LOG.debug("Final Response:: {}", responseJSON);
				if (responseJSON.has("ResponseHeader")
						&& responseJSON.getJSONObject("ResponseHeader").has("ResponseCode")
						&& CommonConstants.SUCCESS.equalsIgnoreCase(
								responseJSON.getJSONObject("ResponseHeader").getString("ResponseCode"))) {
					updateApiAuditTable(recordToProcess, CommonConstants.AUDIT_TABLE_SUCCESS_STATUS);
				} else {
					updateApiAuditTable(recordToProcess, CommonConstants.AUDIT_TABLE_FAIL_STATUS);
				}
			} catch (Exception ex) {
				LOG.error("Exception Occured in checkForApiAuditTableStatus::", ex);
				updateApiAuditTable(recordToProcess, CommonConstants.AUDIT_TABLE_FAIL_STATUS);
			}
		} else {
			LOG.debug("Response is not an instance of Response POJO, Considering as Success");
			updateApiAuditTable(recordToProcess, CommonConstants.AUDIT_TABLE_SUCCESS_STATUS);
		}
	}

	private void updateApiAuditTable(TbUaobApiAuditLogs recordToProcess, String status) {		
		recordToProcess.setUpdateTs(new Timestamp(System.currentTimeMillis()));
		recordToProcess.setSchedulerStatus(status);
		apiAuditTableRepo.save(recordToProcess);
	}
}
