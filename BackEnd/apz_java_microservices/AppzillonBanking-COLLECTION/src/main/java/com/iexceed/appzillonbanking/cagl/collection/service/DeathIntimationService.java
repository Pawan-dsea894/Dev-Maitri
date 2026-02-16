package com.iexceed.appzillonbanking.cagl.collection.service;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.iexceed.appzillonbanking.cagl.collection.constants.CommonConstants;
import com.iexceed.appzillonbanking.cagl.collection.domain.ab.ApplicationMaster;
import com.iexceed.appzillonbanking.cagl.collection.domain.ab.ApplicationWorkflow;
import com.iexceed.appzillonbanking.cagl.collection.domain.ab.TbUacoDeathIntimationDtls;
import com.iexceed.appzillonbanking.cagl.collection.domain.ab.WorkflowDefinition;
import com.iexceed.appzillonbanking.cagl.collection.payload.DeathIntimationApprovePushbackRequest;
import com.iexceed.appzillonbanking.cagl.collection.payload.DeathIntimationApprovePushbackRequestFields;
import com.iexceed.appzillonbanking.cagl.collection.payload.DeathIntimationFields;
import com.iexceed.appzillonbanking.cagl.collection.payload.DeathIntimationRequest;
import com.iexceed.appzillonbanking.cagl.collection.payload.DeathIntimationRequestFields;
import com.iexceed.appzillonbanking.cagl.collection.payload.FetchDeathIntimationRequest;
import com.iexceed.appzillonbanking.cagl.collection.payload.FetchDeathIntimationRequestFields;
import com.iexceed.appzillonbanking.cagl.collection.repository.ab.ApplicationMasterRepository;
import com.iexceed.appzillonbanking.cagl.collection.repository.ab.ApplicationWorkflowRepository;
import com.iexceed.appzillonbanking.cagl.collection.repository.ab.TbUacoDeathIntimationRepository;
import com.iexceed.appzillonbanking.cagl.collection.repository.ab.WorkflowDefinitionRepository;
import com.iexceed.appzillonbanking.cagl.collection.util.CollectionUtil;
import com.iexceed.appzillonbanking.core.payload.Response;
import com.iexceed.appzillonbanking.core.payload.ResponseBody;
import com.iexceed.appzillonbanking.core.payload.ResponseHeader;
import com.iexceed.appzillonbanking.core.utils.CommonUtils;

@Service
public class DeathIntimationService {

	@Autowired
	private ApplicationMasterRepository applicationMasterRepository;

	@Autowired
	private ApplicationWorkflowRepository applicationWorkflowRepository;

	@Autowired
	private WorkflowDefinitionRepository workflowDefinitionRepository;

	@Autowired
	private TbUacoDeathIntimationRepository tbUacoDeathIntimationRepository;

	private static final String APPLN_TYPE = "DEATHINTIMATION";
	private static final String SEND_FOR_APPROVAL = "SENDFORAPPROVAL";

	private static final Logger logger = LogManager.getLogger(DeathIntimationService.class);

	public Response createDeathIntimation(DeathIntimationRequest deathIntimationRequest) {
		Response response;
		try {
			logger.debug("Inside createDeathIntimation request::{}", deathIntimationRequest);
			DeathIntimationRequestFields deathIntimationRequestFields = deathIntimationRequest.getRequestObj();
			// Store data in TbUacoDeathIntimationDtls table as list of values.
			List<TbUacoDeathIntimationDtls> tbUacoDeathIntimationDtlsLst = new ArrayList<>();
			List<ApplicationMaster> applnMstLst = new ArrayList<>();
			List<ApplicationWorkflow> applnWFLst = new ArrayList<>();
			for (DeathIntimationFields deathIntimationField : deathIntimationRequestFields.getDeathIntimation()) {
				String applicationId = CollectionUtil.generateDeathIntimationApplnId(deathIntimationField.getCustId(),
						deathIntimationField.getKendraId());
				ApplicationMaster applnMaster = ApplicationMaster.builder()
						.appId(deathIntimationRequestFields.getAppId()).applicationId(applicationId).versionNum("1")
						.kendraId(deathIntimationField.getKendraId()).applicationDate(LocalDate.now())
						.createdBy(deathIntimationRequestFields.getKmId()).applicationType(APPLN_TYPE)
						.applicationStatus("INPROGRESS").branchCode(deathIntimationRequestFields.getBranchId())
						.currentStage(APPLN_TYPE).kmId(deathIntimationRequestFields.getKmId())
						.kendraName(deathIntimationField.getKendraName()).leader(null).amount(null)
						.applicationRefNo(null).build();
				applnMstLst.add(applnMaster);

				TbUacoDeathIntimationDtls tbUacoDeathIntimationDtls = TbUacoDeathIntimationDtls.builder()
						.appId(deathIntimationRequestFields.getAppId()).applicationId(applicationId).versionNo("1")
						.kendraId(deathIntimationField.getKendraId()).customerId(deathIntimationField.getCustId())
						.customerName(deathIntimationField.getCustName()).intimationType(deathIntimationField.getType())
						.createdBy(deathIntimationRequestFields.getKmId()).createTs(new Timestamp(new Date().getTime()))
						.payload(deathIntimationField.getPayload()).build();
				tbUacoDeathIntimationDtlsLst.add(tbUacoDeathIntimationDtls);

				ApplicationWorkflow applnWF = ApplicationWorkflow.builder().appId(deathIntimationRequest.getAppId())
						.applicationId(applicationId).versionNum(Integer.valueOf("1")).workflowSeqNum(0)
						.createdBy(deathIntimationRequest.getUserId()).createTs(LocalDateTime.now())
						.applicationStatus("INPROGRESS").remarks(null)
						.currentRole(deathIntimationRequestFields.getKmUserRole()).nextWorkFlowStage(SEND_FOR_APPROVAL)
						.build();
				applnWFLst.add(applnWF);
			}
			applicationMasterRepository.saveAll(applnMstLst);
			tbUacoDeathIntimationRepository.saveAll(tbUacoDeathIntimationDtlsLst);
			applicationWorkflowRepository.saveAll(applnWFLst);

			ResponseHeader respHeader = ResponseHeader.builder()
					.responseCode(com.iexceed.appzillonbanking.core.constants.CommonConstants.SUCCESS)
					.responseMessage("Death Intimation submitted Successfully").build();

			ResponseBody respBody = ResponseBody.builder().responseObj("").build();
			response = Response.builder().responseHeader(respHeader).responseBody(respBody).build();
		} catch (Exception ex) {
			logger.error(CommonConstants.EXCEP_OCCURED, ex);
			ResponseHeader respHeader = ResponseHeader.builder()
					.responseCode(com.iexceed.appzillonbanking.core.constants.CommonConstants.FAILURE)
					.responseMessage(CommonConstants.EXCEPTION_MSG).build();
			ResponseBody respBody = ResponseBody.builder().responseObj("").build();
			response = Response.builder().responseHeader(respHeader).responseBody(respBody).build();
		}
		logger.debug("Inside createDeathIntimation response::{}", response);
		return response;
	}

	public Response fetchDeathIntimation(FetchDeathIntimationRequest fetchDeathIntimationRequest) {
		Response response;
		try {
			logger.debug("Inside fetchDeathIntimation request::{}", fetchDeathIntimationRequest);
			FetchDeathIntimationRequestFields fetchDeathIntimationRequestFields = fetchDeathIntimationRequest
					.getRequestObj();

			List<ApplicationMaster> applnMasterLst = new ArrayList<>();
			if ("KM".equalsIgnoreCase(fetchDeathIntimationRequestFields.getKmUserRole())) {
				applnMasterLst = applicationMasterRepository
						.findByAppIdAndApplicationTypeAndCreatedByOrderByApplicationDateDesc(
								fetchDeathIntimationRequest.getAppId(), APPLN_TYPE,
								fetchDeathIntimationRequest.getUserId());

			} else if ("BM".equalsIgnoreCase(fetchDeathIntimationRequestFields.getKmUserRole())) {
				applnMasterLst = applicationMasterRepository.findByBranchCodeAndApplicationType(
						fetchDeathIntimationRequestFields.getBranchId(), APPLN_TYPE);
			}
			if (!applnMasterLst.isEmpty()) {

				Map<String, String> applicationMasterMap = applnMasterLst.stream()
						.collect(Collectors.groupingBy(ApplicationMaster::getApplicationId,
								Collectors.maxBy(Comparator.comparingInt(am -> Integer.parseInt(am.getVersionNum())))))
						.values().stream().flatMap(Optional::stream).collect(Collectors
								.toMap(ApplicationMaster::getApplicationId, ApplicationMaster::getApplicationStatus));

				List<String> applicationIds = new ArrayList<>(applicationMasterMap.keySet());

				List<TbUacoDeathIntimationDtls> tbUacoDeathIntDtlsLst = tbUacoDeathIntimationRepository
						.findByAppIdAndApplicationIdIn(fetchDeathIntimationRequest.getAppId(), applicationIds);

				List<Map<String, Object>> deathIntimationResponseMap = tbUacoDeathIntDtlsLst.stream()
						.map(deathIntimation -> {
							Map<String, Object> responseMap = new ObjectMapper().convertValue(deathIntimation,
									new TypeReference<Map<String, Object>>() {
									});
							responseMap.put("status", applicationMasterMap.get(deathIntimation.getApplicationId()));
							return responseMap;
						}).toList();

				String tbUacoDeathIntDtlsStr = new ObjectMapper().writeValueAsString(deathIntimationResponseMap);
				ResponseHeader respHeader = ResponseHeader.builder()
						.responseCode(com.iexceed.appzillonbanking.core.constants.CommonConstants.SUCCESS)
						.responseMessage("").build();
				ResponseBody respBody = ResponseBody.builder().responseObj(tbUacoDeathIntDtlsStr).build();
				response = Response.builder().responseHeader(respHeader).responseBody(respBody).build();
			} else {
				ResponseHeader respHeader = new ResponseHeader();
				CommonUtils.generateHeaderForNoResult(respHeader);
				ResponseBody respBody = ResponseBody.builder().responseObj("").build();
				response = Response.builder().responseHeader(respHeader).responseBody(respBody).build();
			}
		} catch (Exception ex) {
			logger.error(CommonConstants.EXCEP_OCCURED, ex);
			ResponseHeader respHeader = ResponseHeader.builder()
					.responseCode(com.iexceed.appzillonbanking.core.constants.CommonConstants.FAILURE)
					.responseMessage(CommonConstants.EXCEPTION_MSG).build();
			ResponseBody respBody = ResponseBody.builder().responseObj("").build();
			response = Response.builder().responseHeader(respHeader).responseBody(respBody).build();
		}
		logger.debug("Inside fetchDeathIntimation response::{}", response);
		return response;
	}

	public Response approvePushbackDeathIntimation(DeathIntimationApprovePushbackRequest approvePushbackRequest) {
		Response response = new Response();
		ResponseHeader responseHeader = new ResponseHeader();
		ResponseBody responseBody = new ResponseBody();
		try {
			DeathIntimationApprovePushbackRequestFields approvePushbackReqFields = approvePushbackRequest
					.getRequestObj();
			Optional<ApplicationMaster> applnMstOpt = applicationMasterRepository
					.findTopByAppIdAndApplicationIdAndKendraIdAndVersionNum(approvePushbackRequest.getAppId(),
							approvePushbackReqFields.getApplicationId(), approvePushbackReqFields.getKendraId(),
							approvePushbackReqFields.getVersionNo());
			logger.debug("Application Master::{}", applnMstOpt);
			if (applnMstOpt.isPresent()) {

				List<WorkflowDefinition> wfDefnLst = workflowDefinitionRepository.findByAppIdAndCurrentRole(
						approvePushbackRequest.getAppId(), approvePushbackReqFields.getKmUserRole());
				logger.debug("Application Workflow Defn List::{}", wfDefnLst);

				Optional<WorkflowDefinition> matchedWorkflow = Optional.empty();
				if ("APPROVE".equalsIgnoreCase(approvePushbackReqFields.getAction())) {
					matchedWorkflow = wfDefnLst.stream()
							.filter(wfDefn -> "BMDEATHINTIMATION".equalsIgnoreCase(wfDefn.getWorkFlowId())
									&& SEND_FOR_APPROVAL.equalsIgnoreCase(wfDefn.getFromStageId())
									&& wfDefn.getStageSeqNum() == 1)
							.findFirst();
				} else if ("PUSHBACK".equalsIgnoreCase(approvePushbackReqFields.getAction())) {
					matchedWorkflow = wfDefnLst.stream()
							.filter(wfDefn -> "BMDEATHINTIMATION".equalsIgnoreCase(wfDefn.getWorkFlowId())
									&& SEND_FOR_APPROVAL.equalsIgnoreCase(wfDefn.getFromStageId())
									&& wfDefn.getStageSeqNum() == 2)
							.findFirst();
				}
				matchedWorkflow.ifPresent(wfDefn -> {
					logger.debug("Matched Workflow::{}", wfDefn);
					updateApplicationWorkflow(approvePushbackRequest, wfDefn.getNextStageId(), wfDefn.getNextWFStatus(),
							approvePushbackReqFields.getRemarks());
					ApplicationMaster applnMaster = applnMstOpt.get();
					applnMaster.setCurrentStage(wfDefn.getNextStageId());
					applnMaster.setApplicationStatus(wfDefn.getNextWFStatus());
					logger.debug("Final Update ApplicationMaster::{}", applnMaster);
					applicationMasterRepository.save(applnMaster);
					CommonUtils.generateHeaderForSuccess(responseHeader);
					responseHeader.setResponseMessage("");
					response.setResponseBody(responseBody);
					response.setResponseHeader(responseHeader);
				});
			} else {
				CommonUtils.generateHeaderForNoResult(responseHeader);
				responseBody.setResponseObj("");
				response.setResponseBody(responseBody);
				response.setResponseHeader(responseHeader);
			}
		} catch (Exception ex) {
			logger.error(CommonConstants.EXCEP_OCCURED, ex);
			CommonUtils.generateHeaderForGenericError(responseHeader);
			responseBody.setResponseObj("");
			response.setResponseBody(responseBody);
			response.setResponseHeader(responseHeader);
		}
		return response;
	}

	private void updateApplicationWorkflow(DeathIntimationApprovePushbackRequest approvePushbackRequest,
			String currentStage, String workflowStatus, String remarks) {

		Optional<ApplicationWorkflow> applnWFOpt = applicationWorkflowRepository
				.findTopByAppIdAndApplicationIdAndVersionNumOrderByWorkflowSeqNumDesc(approvePushbackRequest.getAppId(),
						approvePushbackRequest.getRequestObj().getApplicationId(),
						Integer.parseInt(approvePushbackRequest.getRequestObj().getVersionNo()));

		logger.debug("ApplicationWorkflow::{}", applnWFOpt);
		if (applnWFOpt.isPresent()) {
			ApplicationWorkflow applnWF = ApplicationWorkflow.builder().appId(approvePushbackRequest.getAppId())
					.applicationId(approvePushbackRequest.getRequestObj().getApplicationId())
					.versionNum(Integer.parseInt(approvePushbackRequest.getRequestObj().getVersionNo()))
					.workflowSeqNum(applnWFOpt.get().getWorkflowSeqNum() + 1).applicationStatus(workflowStatus)
					.createdBy(approvePushbackRequest.getUserId()).createTs(LocalDateTime.now())
					.currentRole(approvePushbackRequest.getRequestObj().getKmUserRole()).nextWorkFlowStage(currentStage)
					.remarks(remarks).build();
			applicationWorkflowRepository.save(applnWF);
		}
	}
}