package com.iexceed.appzillonbanking.cagl.incomeassesment.core;

import java.time.LocalDateTime;
import java.util.Optional;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.iexceed.appzillonbanking.cagl.incomeassesment.core.domain.ab.UserRole;
import com.iexceed.appzillonbanking.cagl.incomeassesment.core.repository.ab.UserRoleRepository;
import com.iexceed.appzillonbanking.cagl.incomeassesment.domain.ab.ApplicationMaster;
import com.iexceed.appzillonbanking.cagl.incomeassesment.domain.ab.ApplicationWorkflow;
import com.iexceed.appzillonbanking.cagl.incomeassesment.payload.PopulateapplnWFRequest;
import com.iexceed.appzillonbanking.cagl.incomeassesment.payload.PopulateapplnWFRequestFields;
import com.iexceed.appzillonbanking.cagl.incomeassesment.payload.WorkFlowDetails;
import com.iexceed.appzillonbanking.cagl.incomeassesment.repository.ab.ApplicationMasterRepository;
import com.iexceed.appzillonbanking.cagl.incomeassesment.repository.ab.ApplicationWorkflowRepository;
import com.iexceed.appzillonbanking.core.payload.Response;
import com.iexceed.appzillonbanking.core.payload.ResponseBody;
import com.iexceed.appzillonbanking.core.payload.ResponseHeader;
import com.iexceed.appzillonbanking.core.utils.CommonUtils;
import com.iexceed.appzillonbanking.core.utils.FallbackUtils;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;

@Service
public class CommonAssessmentService {

	private static final Logger logger = LogManager.getLogger(CommonAssessmentService.class);

	@Autowired
	private UserRoleRepository userRoleRepository;

	@Autowired
	private ApplicationMasterRepository applicationMasterRepo;

	@Autowired
	private ApplicationWorkflowRepository applnWfRepository;

	@CircuitBreaker(name = "fallback", fallbackMethod = "populateApplnWorkFlowFallback")
	public Response populateApplnWorkFlow(PopulateapplnWFRequest request) {
		Response response = new Response();
		ResponseHeader responseHeader = new ResponseHeader();
		ResponseBody responseBody = new ResponseBody();
		PopulateapplnWFRequestFields reqFields = request.getRequestObj();
		int wfSeqNum = 1;
		Optional<ApplicationWorkflow> wfObj = applnWfRepository
				.findTopByAppIdAndApplicationIdAndVersionNumOrderByWorkflowSeqNumDesc(reqFields.getAppId(),
						reqFields.getApplicationId(), Integer.parseInt(reqFields.getVersionNum()));
		if (wfObj.isPresent()) {
			ApplicationWorkflow dbObj = wfObj.get();
			wfSeqNum = dbObj.getWorkflowSeqNum() + 1;
		}
		WorkFlowDetails workFlow = reqFields.getWorkflow();
		ApplicationWorkflow workFlowObj = new ApplicationWorkflow();
		workFlowObj.setAppId(reqFields.getAppId());
		workFlowObj.setApplicationId(reqFields.getApplicationId());

		String status = workFlow.getNextWorkflowStatus();
		workFlowObj.setApplicationStatus(status);
		workFlowObj.setCreatedBy(reqFields.getCreatedBy());
		workFlowObj.setCreateTs(LocalDateTime.now());
		if (workFlow != null) {
			workFlowObj.setNextWorkFlowStage(workFlow.getNextStageId());
			workFlowObj.setCurrentRole(workFlow.getCurrentRole());
			workFlowObj.setRemarks(workFlow.getRemarks());
		}

		workFlowObj.setVersionNum(Integer.parseInt(reqFields.getVersionNum()));
		workFlowObj.setWorkflowSeqNum(wfSeqNum);

		applnWfRepository.save(workFlowObj);
		responseBody.setResponseObj("");
		CommonUtils.generateHeaderForSuccess(responseHeader);
		response.setResponseBody(responseBody);
		response.setResponseHeader(responseHeader);
		return response;
	}

	@CircuitBreaker(name = "fallback", fallbackMethod = "updateStatusInMasterFallback")
	public void updateStatusInMaster(PopulateapplnWFRequest apiRequest) {
		PopulateapplnWFRequestFields requestObj = apiRequest.getRequestObj();
		Optional<ApplicationMaster> appMaster = applicationMasterRepo.findByAppIdAndApplicationIdAndVersionNum(
				requestObj.getAppId(), requestObj.getApplicationId(), requestObj.getVersionNum());
		if (appMaster.isPresent()) {
			ApplicationMaster appMasterObj = appMaster.get();
			updateStatus(AppStatus.INPROGRESS.getValue(), appMasterObj, AppStatus.PENDING.getValue());
			if (!CommonUtils.isNullOrEmpty(appMasterObj.getApplicationId())) {
				Optional<ApplicationMaster> appMasterRelated = applicationMasterRepo
						.findByAppIdAndApplicationIdAndVersionNum(requestObj.getAppId(),
								appMasterObj.getApplicationId(), requestObj.getVersionNum());
				if (appMasterRelated.isPresent()) {
					ApplicationMaster appMasterObjRelated = appMasterRelated.get();
					updateStatus(AppStatus.INPROGRESS.getValue(), appMasterObjRelated, AppStatus.PENDING.getValue());
				}
			}
		}
	}

	public void updateStatus(String fromStatus, ApplicationMaster appMasterObj, String toStatus) {
		if (fromStatus.equalsIgnoreCase(appMasterObj.getApplicationStatus())) {
			appMasterObj.setApplicationStatus(toStatus);
			applicationMasterRepo.save(appMasterObj);
		}
	}

	@CircuitBreaker(name = "fallback", fallbackMethod = "fetchRoleIdFallback")
	public String fetchRoleId(String appId, String userId) {
		String roleId = "";
		Optional<UserRole> objDb = userRoleRepository.findByAppIdAndUserId(appId, userId);
		if (objDb.isPresent()) {
			UserRole obj = objDb.get();
			roleId = obj.getRoleId();
		}
		return roleId;
	}

	private String fetchRoleIdFallback(String appId, String userId, Exception e) {
		logger.error("fetchRoleIdFallback error : ", e);
		return "";
	}

	private void updateStatusInMasterFallback(PopulateapplnWFRequest apiRequest, Exception e) {
		logger.error("updateStatusInMasterFallback error : request is : {} and error is: {} ", apiRequest, e);
	}

	private Response populateApplnWorkFlowFallback(PopulateapplnWFRequest request, Exception e) {
		logger.error("populateApplnWorkFlowFallback error : ", e);
		return FallbackUtils.genericFallback();
	}
}
