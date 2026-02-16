package com.iexceed.appzillonbanking.cagl.collection.service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.gson.JsonObject;
import com.iexceed.appzillonbanking.cagl.collection.constants.CommonConstants;
import com.iexceed.appzillonbanking.cagl.collection.domain.ab.ApplicationMaster;
import com.iexceed.appzillonbanking.cagl.collection.domain.ab.ApplicationWorkflow;
import com.iexceed.appzillonbanking.cagl.collection.domain.ab.CollectionReport;
import com.iexceed.appzillonbanking.cagl.collection.domain.ab.TbUacoKendraDtls;
import com.iexceed.appzillonbanking.cagl.collection.domain.ab.TbUacoKendraDtlsId;
import com.iexceed.appzillonbanking.cagl.collection.domain.ab.TbUaobCustDtls;
import com.iexceed.appzillonbanking.cagl.collection.domain.ab.WorkflowDefinition;
import com.iexceed.appzillonbanking.cagl.collection.payload.ApplicationWFDetails;
import com.iexceed.appzillonbanking.cagl.collection.payload.ApprovePushbackApplnRequest;
import com.iexceed.appzillonbanking.cagl.collection.payload.ApprovePushbackApplnRequestFields;
import com.iexceed.appzillonbanking.cagl.collection.payload.CollectionGroupDtls;
import com.iexceed.appzillonbanking.cagl.collection.payload.CollectionMemberDtls;
import com.iexceed.appzillonbanking.cagl.collection.payload.CollectionsData;
import com.iexceed.appzillonbanking.cagl.collection.payload.CustomerDetails;
import com.iexceed.appzillonbanking.cagl.collection.payload.FetchRptAndNonMeetingColReq;
import com.iexceed.appzillonbanking.cagl.collection.payload.FetchRptAndNonMeetingColReqFields;
import com.iexceed.appzillonbanking.cagl.collection.payload.StdMeetingDayAmtFields;
import com.iexceed.appzillonbanking.cagl.collection.payload.StdMeetingDayBody;
import com.iexceed.appzillonbanking.cagl.collection.payload.StdMeetingDayCustomerDtls;
import com.iexceed.appzillonbanking.cagl.collection.payload.StdMeetingDayCustomerFields;
import com.iexceed.appzillonbanking.cagl.collection.payload.StdMeetingDayPosCustomerFields;
import com.iexceed.appzillonbanking.cagl.collection.payload.StdMeetingLoanDtls;
import com.iexceed.appzillonbanking.cagl.collection.payload.StdMeetingRequest;
import com.iexceed.appzillonbanking.cagl.collection.repository.ab.ApplicationMasterRepository;
import com.iexceed.appzillonbanking.cagl.collection.repository.ab.ApplicationWorkflowRepository;
import com.iexceed.appzillonbanking.cagl.collection.repository.ab.CollectionReportRepository;
import com.iexceed.appzillonbanking.cagl.collection.repository.ab.TbUacoKendraDtlsRepository;
import com.iexceed.appzillonbanking.cagl.collection.repository.ab.TbUaobCustomerDtlsRepo;
import com.iexceed.appzillonbanking.cagl.collection.repository.ab.WorkflowDefinitionRepository;
import com.iexceed.appzillonbanking.core.payload.Header;
import com.iexceed.appzillonbanking.core.payload.Response;
import com.iexceed.appzillonbanking.core.payload.ResponseBody;
import com.iexceed.appzillonbanking.core.payload.ResponseHeader;
import com.iexceed.appzillonbanking.core.utils.CommonUtils;
import com.iexceed.appzillonbanking.interfaceAdapter.service.InterfaceAdapter;


import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
public class ApprovePushbackService {

	@Autowired
	private ApplicationMasterRepository applicationMasterRepository;

	@Autowired
	private TbUacoKendraDtlsRepository tbUacoKendraDtlsRepository;

	@Autowired
	private TbUaobCustomerDtlsRepo tbUaobCustomerDtlsRepo;

	@Autowired
	private ApplicationWorkflowRepository applicationWorkflowRepository;

	@Autowired
	private InterfaceAdapter interfaceAdapter;

	@Autowired
	private WorkflowDefinitionRepository workflowDefinitionRepository;
	
	@Autowired
	private CollectionReportRepository collectionReportRepository;
	
	

	private static final String CASHIER_ROLE = "CASHIER";
	private static final String BM_ROLE = "BM";
	private static final String STD_MEETING_INTF = "StandardMeetingDayCollection";
	private static final String REPEAT_COL_INTF = "RepeatCollection";
	private static final String NON_MEETING_INTF = "NonMeetingCollection";
	private static final String INDVIDUAL_MEMBER_COL_INTF = "PostIndividualMemberCollection";
	private static final String HEADER_LW_TXT = "header";
	private static final String ERR_NODE = "error";
	private static final String ERR_DTLS_NODE = "errorDetails";
	private static final String FAILED = "FAILED";
	private static final String API_FAIL = "API_FAIL";
	private static final String APPROVED = "APPROVED";
	private static final String INPROGRESS = "INPROGRESS";
	private static final String COLL_ID = "collectionId";
	private static final String RECORD_EXIST_PATTERN = "This transaction .* is already completed, cannot input again";
	private static final String TILL_STATUS_PATTERN = "TILL status is closed";
	private static final String INTERFACE_APPROVE = "APPROVEAPPLICATION";
	private static final String INTERFACE_PUSHBACK = "PUSHBACKAPPLICATION";
	private static final String STAGE_ID_BM = "SENDFORAPPROVAL";
	private static final String STAGE_ID_CASHIER = "CASHHANDOVER";
	private static final String WORKFLOW_ID_BM = "BMPROCESS";
	private static final String WORKFLOW_ID_CASHIER = "CASHIERPROCESS";

	private static final Logger logger = LogManager.getLogger(ApprovePushbackService.class);

	public Mono<Response> approveApplication(ApprovePushbackApplnRequest approvePushbackApplnRequest, Header header) {

		Response response = new Response();
		ResponseHeader responseHeader = new ResponseHeader();
		ResponseBody responseBody = new ResponseBody();

		try {
			String appId = approvePushbackApplnRequest.getAppId();
			// String kmUserId = approvePushbackApplnRequest.getRequestObj().getKmId();
			// String applicationId =
			// approvePushbackApplnRequest.getRequestObj().getApplicationId();
			String versionNum = approvePushbackApplnRequest.getRequestObj().getVersionNo();
			String applicationId=approvePushbackApplnRequest.getRequestObj().getApplicationId();
			List<String> kendraIds = Arrays
					.asList(approvePushbackApplnRequest.getRequestObj().getKendraIds().split("~"));
			String kmUserRole = approvePushbackApplnRequest.getRequestObj().getKmUserRole();

			List<ApplicationMaster> applnMastLst = applicationMasterRepository
					.findByAppIdAndApplicationIdAndVersionNumAndKendraIdInAndApplicationTypeNotAndApplicationStatusNotOrderByVersionNumDesc(
							appId,applicationId,versionNum, kendraIds, "DEATHINTIMATION", APPROVED);
			logger.debug("Approve Application Master List::{}", applnMastLst);
			if (!applnMastLst.isEmpty()) {
				List<WorkflowDefinition> wfDefnLst = workflowDefinitionRepository.findByAppIdAndCurrentRole(appId,
						kmUserRole);
				logger.debug("Application Workflow Defn List::{}", wfDefnLst);

				Optional<WorkflowDefinition> matchedWorkflow = validateAndFetchNextWFDtls(wfDefnLst, kmUserRole);
				if (BM_ROLE.equalsIgnoreCase(kmUserRole)) {
					// Invoke API call for final Collection Submission
					return this.processStdMeetingCollectionForApplnList(applnMastLst, header, matchedWorkflow, approvePushbackApplnRequest);
				} else if (CASHIER_ROLE.equalsIgnoreCase(kmUserRole)) {
					matchedWorkflow.ifPresent(wfDefn -> {
						logger.debug("Matched Workflow::{}", wfDefn);
						for (ApplicationMaster applnMaster : applnMastLst) {
							applnMaster.setCurrentStage(wfDefn.getNextStageId());
							applnMaster.setApplicationStatus(wfDefn.getNextWFStatus());
						}
						applicationMasterRepository.saveAll(applnMastLst);
						updateApplicationWorkflow(applnMastLst, approvePushbackApplnRequest,
								approvePushbackApplnRequest.getRequestObj(), wfDefn, null);
					});
					CommonUtils.generateHeaderForSuccess(responseHeader);
					responseHeader.setResponseMessage("Application Approved Succesfully by Cashier.");
					return generateResponse(responseHeader, responseBody, response);
				} else {
					CommonUtils.generateHeaderForGenericError(responseHeader);
					responseHeader.setErrorCode("RL-NA");
					return generateResponse(responseHeader, responseBody, response);
				}
			} else {
				CommonUtils.generateHeaderForNoResult(responseHeader);
				return generateResponse(responseHeader, responseBody, response);
			}
		} catch (Exception ex) {
			logger.error(CommonConstants.EXCEP_OCCURED, ex);
			CommonUtils.generateHeaderForGenericError(responseHeader);
			return generateResponse(responseHeader, responseBody, response);
		}
	}

	public Response pushbackApplication(ApprovePushbackApplnRequest approvePushbackApplnRequest, Header header) {
		Response response = new Response();
		ResponseHeader responseHeader = new ResponseHeader();
		ResponseBody responseBody = new ResponseBody();
		try {
			String appId = approvePushbackApplnRequest.getAppId();
			// String kmUserId = approvePushbackApplnRequest.getRequestObj().getKmId();
			// String applicationId =
			// approvePushbackApplnRequest.getRequestObj().getApplicationId();
			String versionNum = approvePushbackApplnRequest.getRequestObj().getVersionNo();
			String remarks = approvePushbackApplnRequest.getRequestObj().getRemarks();
			List<String> kendraIds = Arrays
					.asList(approvePushbackApplnRequest.getRequestObj().getKendraIds().split("~"));

			String kmUserRole = approvePushbackApplnRequest.getRequestObj().getKmUserRole();

			List<ApplicationMaster> applnMastLst = applicationMasterRepository
					.findByAppIdAndVersionNumAndKendraIdInAndApplicationTypeNotAndApplicationStatusNotOrderByVersionNumDesc(appId, versionNum,
							kendraIds, "DEATHINTIMATION", APPROVED);
			logger.debug("Application Master List::{}", applnMastLst);
			if (!applnMastLst.isEmpty()) {
				List<WorkflowDefinition> wfDefnLst = workflowDefinitionRepository.findByAppIdAndCurrentRole(appId,
						kmUserRole);
				logger.debug("Application Workflow Defn List::{}", wfDefnLst);
				Optional<WorkflowDefinition> matchedWorkflow = wfDefnLst.stream()
						.filter(wfDefn -> (CASHIER_ROLE.equalsIgnoreCase(kmUserRole)
								&& "CASHIERPROCESS".equalsIgnoreCase(wfDefn.getWorkFlowId())
								&& "CASHHANDOVER".equalsIgnoreCase(wfDefn.getFromStageId())
								&& wfDefn.getStageSeqNum() == 2)
								|| (BM_ROLE.equalsIgnoreCase(kmUserRole)
										&& "BMPROCESS".equalsIgnoreCase(wfDefn.getWorkFlowId())
										&& "SENDFORAPPROVAL".equalsIgnoreCase(wfDefn.getFromStageId())
										&& wfDefn.getStageSeqNum() == 2))
						.findFirst();
				matchedWorkflow.ifPresent(wfDefn -> {
					logger.debug("Matched Workflow::{}", wfDefn);
					for (ApplicationMaster applnMaster : applnMastLst) {
						applnMaster.setCurrentStage(wfDefn.getNextStageId());
						applnMaster.setApplicationStatus(wfDefn.getNextWFStatus());
					}
					logger.debug("Final Update ApplicationMaster List::{}", applnMastLst);
					applicationMasterRepository.saveAll(applnMastLst);
					updateApplicationWorkflow(applnMastLst, approvePushbackApplnRequest,
							approvePushbackApplnRequest.getRequestObj(), wfDefn, remarks);
				});
				CommonUtils.generateHeaderForSuccess(responseHeader);
				responseHeader.setResponseMessage("Application Rejected Succesfully.");
				response.setResponseBody(responseBody);
				response.setResponseHeader(responseHeader);
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

	private Optional<WorkflowDefinition> validateAndFetchNextWFDtls(List<WorkflowDefinition> wfDefnLst,
			String kmUserRole) {
		return wfDefnLst.stream()
				.filter(wfDefn -> (CASHIER_ROLE.equalsIgnoreCase(kmUserRole)
						&& "CASHIERPROCESS".equalsIgnoreCase(wfDefn.getWorkFlowId())
						&& "CASHHANDOVER".equalsIgnoreCase(wfDefn.getFromStageId()) && wfDefn.getStageSeqNum() == 1)
						|| (BM_ROLE.equalsIgnoreCase(kmUserRole) && "BMPROCESS".equalsIgnoreCase(wfDefn.getWorkFlowId())
								&& "SENDFORAPPROVAL".equalsIgnoreCase(wfDefn.getFromStageId())
								&& wfDefn.getStageSeqNum() == 1))
				.findFirst();
	}

	/*
	 * private void updateApplicationWorkflow(ApprovePushbackApplnRequest
	 * approvePushbackApplnRequest, ApprovePushbackApplnRequestFields
	 * approvePushbackApplnReqFields, WorkflowDefinition wfDefn, String remarks) {
	 *
	 * Optional<ApplicationWorkflow> applnWFOpt = applicationWorkflowRepository
	 * .findTopByAppIdAndApplicationIdAndVersionNumOrderByWorkflowSeqNumDesc(
	 * approvePushbackApplnRequest.getAppId(),
	 * approvePushbackApplnReqFields.getApplicationId(),
	 * Integer.parseInt(approvePushbackApplnReqFields.getVersionNo())); if
	 * (applnWFOpt.isPresent()) { remarks =
	 * CommonUtils.checkStringNullOrEmpty(remarks) ? applnWFOpt.get().getRemarks() :
	 * remarks; LocalDateTime createDtTime = LocalDateTime.now(); if (null !=
	 * approvePushbackApplnRequest.getRequestObj().getCreateTs()) {
	 * DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
	 * createDtTime =
	 * LocalDateTime.parse(approvePushbackApplnRequest.getRequestObj().getCreateTs()
	 * , dtf); } ApplicationWorkflow applnWF =
	 * ApplicationWorkflow.builder().appId(approvePushbackApplnRequest.getAppId())
	 * .applicationId(approvePushbackApplnReqFields.getApplicationId())
	 * .versionNum(Integer.parseInt(approvePushbackApplnReqFields.getVersionNo()))
	 * .workflowSeqNum(applnWFOpt.get().getWorkflowSeqNum() + 1)
	 * .applicationStatus(wfDefn.getNextWFStatus()).createdBy(
	 * approvePushbackApplnReqFields.getKmId())
	 * .createdUserName(approvePushbackApplnRequest.getRequestObj().getKmUserName())
	 * .createTs(createDtTime)
	 * .currentRole(approvePushbackApplnReqFields.getKmUserRole())
	 * .nextWorkFlowStage(wfDefn.getNextStageId()).remarks(remarks).build();
	 * applicationWorkflowRepository.save(applnWF); } }
	 */

	private void updateApplicationWorkflow(List<ApplicationMaster> applnMastLst,
			ApprovePushbackApplnRequest approvePushbackApplnRequest,
			ApprovePushbackApplnRequestFields approvePushbackApplnReqFields, WorkflowDefinition wfDefn,
			String remarks) {

		List<ApplicationWorkflow> applnWorkflows = new ArrayList<>();

		for (ApplicationMaster applnMaster : applnMastLst) {
			Optional<ApplicationWorkflow> applnWFOpt = applicationWorkflowRepository
					.findTopByAppIdAndApplicationIdAndVersionNumOrderByWorkflowSeqNumDesc(applnMaster.getAppId(),
							applnMaster.getApplicationId(), Integer.parseInt(applnMaster.getVersionNum()));

			if (applnWFOpt.isPresent()) {
				remarks = CommonUtils.checkStringNullOrEmpty(remarks) ? applnWFOpt.get().getRemarks() : remarks;

				LocalDateTime createDtTime = LocalDateTime.now();
				if (applnMaster.getCreateTs() != null) {
					DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
					createDtTime = LocalDateTime.parse(approvePushbackApplnRequest.getRequestObj().getCreateTs(), dtf);
				}

				ApplicationWorkflow applnWF = ApplicationWorkflow.builder().appId(applnMaster.getAppId())
						.applicationId(applnMaster.getApplicationId())
						.versionNum(Integer.parseInt(applnMaster.getVersionNum()))
						.workflowSeqNum(applnWFOpt.get().getWorkflowSeqNum() + 1)
						.applicationStatus(wfDefn.getNextWFStatus()).createdBy(approvePushbackApplnReqFields.getKmId())
						.createdUserName(approvePushbackApplnRequest.getRequestObj().getKmUserName())
						.createTs(createDtTime).currentRole(approvePushbackApplnReqFields.getKmUserRole())
						.nextWorkFlowStage(wfDefn.getNextStageId()).remarks(remarks).build();

				applnWorkflows.add(applnWF);
				try {
					updateMisTable(applnWF, applnMaster.getApplicationId(), applnMaster.getKendraId(),
							applnMaster.getBranchCode(), approvePushbackApplnRequest);
					logger.debug("Mis table update complete");
				} catch(Exception e) {
				    logger.error("Failed to update MIS table for applicationId: {}", applnMaster.getApplicationId(), e);
				}
			}
		}
		if (!applnWorkflows.isEmpty()) {
			applicationWorkflowRepository.saveAll(applnWorkflows);
		}
	}

	@Transactional(rollbackFor = Exception.class)
	public void updateMisTable(ApplicationWorkflow applnWF, String applicationId, String kendraId, String branchCode,
			ApprovePushbackApplnRequest request) throws Exception {

		logger.debug("Updating MIS table for applicationId: {}, kendraId: {}, branchCode: {}", applicationId, kendraId,
				branchCode);

		int verNum = applnWF.getVersionNum();
		logger.debug("Version number is : " + verNum);
		CollectionReport collectionReport = collectionReportRepository
				.findByApplicationIdAndKendraIdAndBranchIdAndVersionNum(applicationId, kendraId, branchCode, verNum).orElseThrow(() -> {
					logger.warn("No CollectionReport found for applicationId: {}, kendraId: {}, branchCode: {}",
							applicationId, kendraId, branchCode);
					return new IllegalStateException("CollectionReport not found");
				});

		String payload = collectionReport.getPayload();
		if (payload == null || payload.isBlank()) {
			logger.error("Payload is null or empty for applicationId: {}", applicationId);
			return;
		}

		ObjectMapper objectMapper = new ObjectMapper();
		CollectionsData collectionsData;
		try {
			collectionsData = objectMapper.readValue(payload, CollectionsData.class);
		} catch (JsonProcessingException e) {
			logger.error("Failed to parse payload JSON for applicationId: {}", applicationId, e);
			throw e;
		}

		List<ApplicationWFDetails> wfDetailsList = Optional.ofNullable(collectionsData.getApplnWFDtls())
				.orElseGet(ArrayList::new);

		WorkflowInfo workflowInfo = resolveWorkflowInfo(request);

		ApplicationWFDetails wfDetails = ApplicationWFDetails.builder().appId(applnWF.getAppId())
				.applnId(applnWF.getApplicationId()).verNo(String.valueOf(applnWF.getVersionNum()))
				.kendraId(collectionsData.getKendraId()).seqNo(String.valueOf(applnWF.getWorkflowSeqNum()))
				.createdBy(applnWF.getCreatedBy()).appStatus(applnWF.getApplicationStatus())
				.usrRole(applnWF.getCurrentRole()).nextWFStage(applnWF.getNextWorkFlowStage())
				.remarks(applnWF.getRemarks()).workflowId(workflowInfo.workflowId())
				.fromStageId(workflowInfo.fromStageId()).stageSeqNo(String.valueOf(workflowInfo.stageSeqNo()))
				.createTs(applnWF.getCreateTs() != null ? applnWF.getCreateTs().toString() : null)
				.kmUserName(applnWF.getCreatedUserName()).build();

		wfDetailsList.add(wfDetails);
		collectionsData.setApplnWFDtls(wfDetailsList);

		try {
			String updatedPayload = objectMapper.writeValueAsString(collectionsData);
			collectionReport.setPayload(updatedPayload);
			collectionReport.setUpdatedTs(LocalDateTime.now());
			collectionReportRepository.save(collectionReport);
			logger.debug("Successfully updated MIS payload for applicationId: {}", applicationId);
		} catch (JsonProcessingException | DataAccessException e) {
			logger.error("Failed to update CollectionReport for applicationId: {}", applicationId, e);
			throw e;
		}
	}

	private record WorkflowInfo(String workflowId, String fromStageId, int stageSeqNo) {
	}

	private WorkflowInfo resolveWorkflowInfo(ApprovePushbackApplnRequest request) {
		String role = request.getRequestObj().getKmUserRole();
		String interfaceName = request.getInterfaceName();

		String workflowId = switch (role.toUpperCase()) {
		case BM_ROLE -> WORKFLOW_ID_BM;
		case CASHIER_ROLE -> WORKFLOW_ID_CASHIER;
		default -> throw new IllegalArgumentException("Unsupported role: " + role);
		};
		String fromStageId = switch (role.toUpperCase()) {
		case BM_ROLE -> STAGE_ID_BM;
		case CASHIER_ROLE -> STAGE_ID_CASHIER;
		default -> throw new IllegalArgumentException("Unsupported role: " + role);
		};

		int stageSeqNo = switch (interfaceName.toUpperCase()) {
		case INTERFACE_APPROVE -> 1;
		case INTERFACE_PUSHBACK -> 2;
		default -> throw new IllegalArgumentException("Unsupported interface: " + interfaceName);
		};

		return new WorkflowInfo(workflowId, fromStageId, stageSeqNo);
	}

	/**
	 * Method to process list of ApplicationMaster for invoking kendraWise
	 * StandardMeetingDayCollection API.
	 *
	 * @param applicationMasters
	 * @param header
	 * @param matchedWorkflow
	 * @param approvePushbackApplnRequest
	 * @return
	 */
	public Mono<Response> processStdMeetingCollectionForApplnList(List<ApplicationMaster> applicationMasters,
			Header header, Optional<WorkflowDefinition> matchedWorkflow, ApprovePushbackApplnRequest approvePushbackApplnRequest) {
		return Flux.fromIterable(applicationMasters).flatMap(applnMaster -> {

			String intfName = getInterfaceName(applnMaster.getApplicationId());

			TbUacoKendraDtlsId tbUacoKendraDtlsId = TbUacoKendraDtlsId.builder().appId(applnMaster.getAppId())
					.applicationId(applnMaster.getApplicationId()).versionNum(applnMaster.getVersionNum())
					.kendraId(applnMaster.getKendraId()).build();
			Optional<TbUacoKendraDtls> tbUacoKendraDtlsOpt = tbUacoKendraDtlsRepository.findById(tbUacoKendraDtlsId);
			logger.debug("tbUacoKendraDtlsOpt::{}", tbUacoKendraDtlsOpt);

			List<TbUaobCustDtls> tbUaobCustDtlsLst = tbUaobCustomerDtlsRepo
					.findByAppIdAndApplicationIdAndKendraIdAndVersionNo(applnMaster.getAppId(),
							applnMaster.getApplicationId(), applnMaster.getKendraId(), applnMaster.getVersionNum());
			logger.debug("tbUaobCustDtlsLst::{}", tbUaobCustDtlsLst);

			// Added the check to invoke the fetch API for Repeat Collection and filter out
			// only those customers pending to pay
			if (REPEAT_COL_INTF.equalsIgnoreCase(intfName)) {
				String selKendraId = applnMaster.getKendraId();
				String selBranchId = applnMaster.getBranchCode();
				FetchRptAndNonMeetingColReqFields fetchRptAndNonMeetingColReqFields = FetchRptAndNonMeetingColReqFields
						.builder().kendraId(selKendraId).branchId(selBranchId).build();

				FetchRptAndNonMeetingColReq colRequest = FetchRptAndNonMeetingColReq.builder().appId(header.getAppId())
						.interfaceName("GetRepeatCollection").userId(header.getUserId())
						.requestObj(fetchRptAndNonMeetingColReqFields).build();

				logger.debug("Inside fetchRepeatCollection request::{}", colRequest);
				Mono<Object> fetchRptColMono = interfaceAdapter.callExternalService(header, colRequest,
						"GetRepeatCollection", true);
				return fetchRptColMono.flatMap(fetchRptCol -> {
					logger.debug("fetchRepeatCollection response::{}", fetchRptCol);
					List<CustomerDetails> rptCollectionCustList = this.frameRptNonMeetingColResponse(fetchRptCol);
					List<StdMeetingDayPosCustomerFields> stdCustList = processCustomerDetails(rptCollectionCustList,
							tbUaobCustDtlsLst, tbUacoKendraDtlsOpt);
					logger.debug("Before sortedStdMeetingDayList::{}", stdCustList);

					List<StdMeetingDayCustomerFields> stdMeetingDayCustList = new ArrayList<>();
					stdCustList.stream().forEach(field -> {
						StdMeetingDayCustomerFields stdMeetingDayCustomerFields = StdMeetingDayCustomerFields.builder()
								.customerId(field.getCustomerId()).cusCollectionAmt(field.getCusCollectionAmt())
								.cusFine(field.getCusFine()).cusFlag(field.getCusFlag())
								.cusAttendance(field.getCusAttendance()).cusUpiFlag(field.getCusUpiFlag())
								.loanDetails(field.getLoanDetails()).build();
						stdMeetingDayCustList.add(stdMeetingDayCustomerFields);
					});
					Mono<Object> apiResponse = invokeRepeatCollectionAPI(stdMeetingDayCustList, applnMaster,
							tbUacoKendraDtlsOpt, intfName, header);
					return apiResponse.flatMap(val -> {
						logger.debug("API response for repeat collection from for applicationId::{}, is::{}", applnMaster.getApplicationId(),
								val);
						this.validateStdMeetingColAPIResp(applnMaster, val);
						updateWorkflowForT24ErrCode(matchedWorkflow, approvePushbackApplnRequest, applnMaster, val);
						return Mono.just(applnMaster);
					}).onErrorResume(ex -> {
						applnMaster.setApplicationStatus(INPROGRESS);
						applnMaster.setCurrentStage(FAILED);
						logger.error("Error invoking API for ApplicationMaster ID: {}, error trace: {}",
								applnMaster.getApplicationId(), ex);
						return Mono.just(applnMaster);
					});
				});
			} else {
				StdMeetingRequest stdMeetingRequest = frameStdMeetingDayColRequest(tbUaobCustDtlsLst, applnMaster,
						header, tbUacoKendraDtlsOpt.get(), intfName);
				logger.debug("Invoking meetingday collection for standard meeting for applicationId::{} with request::{}",
						applnMaster.getApplicationId(), stdMeetingRequest);
				Mono<Object> apiResponse = interfaceAdapter.callExternalService(header, stdMeetingRequest,
						stdMeetingRequest.getInterfaceName(), true);
				return apiResponse.flatMap(val -> {					
					logger.debug("API response from for applicationId::{}, is::{}", applnMaster.getApplicationId(),
							val);
					this.validateStdMeetingColAPIResp(applnMaster, val);
					updateWorkflowForT24ErrCode(matchedWorkflow, approvePushbackApplnRequest, applnMaster, val);
					return Mono.just(applnMaster);
				}).onErrorResume(ex -> {
					applnMaster.setApplicationStatus(INPROGRESS);
					applnMaster.setCurrentStage(FAILED);
					logger.error("Error invoking API for ApplicationMaster ID: {}, error trace: {}",
							applnMaster.getApplicationId(), ex);
					return Mono.just(applnMaster);
				});
			}
		}).collectList().flatMap(applnMasterList -> {
			applicationMasterRepository.saveAll(applnMasterList);
			ResponseHeader responseHeader = new ResponseHeader();
			ResponseBody responseBody = new ResponseBody();
			boolean isFailedCollection = applnMasterList.stream()
					.anyMatch(val -> FAILED.equalsIgnoreCase(val.getCurrentStage()));

			boolean hasAPIErr = applnMasterList.stream().anyMatch(applnMaster -> null != applnMaster.getErrorType()
					&& API_FAIL.equalsIgnoreCase(applnMaster.getErrorType()));
			if (!isFailedCollection && !hasAPIErr) {
				CommonUtils.generateHeaderForSuccess(responseHeader);
				responseHeader.setResponseMessage("Application Approved Succesfully by BM.");
				matchedWorkflow.ifPresent(wfDefn -> {
					logger.debug("Matched Workflow::{}", wfDefn);
					updateApplicationWorkflow(applnMasterList, approvePushbackApplnRequest,
							approvePushbackApplnRequest.getRequestObj(), wfDefn, null);
				});
			} else if (hasAPIErr) {
				logger.debug("API Error observed, Returning the failure message to front-end");
				String errorMessage = applnMasterList.stream()
						.filter(applnMaster -> null != applnMaster.getErrorType()
								&& API_FAIL.equalsIgnoreCase(applnMaster.getErrorType()))
						.map(ApplicationMaster::getErrorMessage).findFirst().orElse("Unknown error occurred");
				String errorCode = applnMasterList.stream()
						.filter(applnMaster -> null != applnMaster.getErrorType()
								&& API_FAIL.equalsIgnoreCase(applnMaster.getErrorType()))
						.map(ApplicationMaster::getErrorCode).findFirst().orElse("API-ERR");
				CommonUtils.generateHeaderForFailure(responseHeader, errorMessage);
				responseHeader.setErrorCode(errorCode);
			} else {
				CommonUtils.generateHeaderForGenericError(responseHeader);
				responseHeader.setResponseMessage("Failed to process the application.");
			}
			responseBody.setResponseObj("");
			Response response = Response.builder().responseBody(responseBody).responseHeader(responseHeader).build();
			return Mono.just(response);
		});
	}

	private void updateWorkflowForT24ErrCode(Optional<WorkflowDefinition> matchedWorkflow,
	        ApprovePushbackApplnRequest approvePushbackApplnRequest,
	        ApplicationMaster applnMaster,
	        Object val) {

	    logger.debug("Inside updateWorkflowForT24ErrCode method");
	    logger.debug("Raw API response object :: {}", val);

	    String extApiResponse;
	    try {
	        extApiResponse = new ObjectMapper().writeValueAsString(val);
	        logger.debug("Serialized API response JSON :: {}", extApiResponse);
	    } catch (Exception e) {
	        logger.error("Failed to serialize API response object", e);
	        return;
	    }

	    if (CommonUtils.checkStringNullOrEmpty(extApiResponse)) {
	        logger.debug("API response is empty or null, skipping processing");
	        return;
	    }

	    try {
	        JSONObject obj = new JSONObject(extApiResponse);
	        String errCode = "";
	        String errMsg = "";

	        if (obj.has("error")) {
	            JSONObject error = obj.getJSONObject("error");
	            JSONArray errorDetails = error.optJSONArray("errorDetails");

	            if (errorDetails != null && errorDetails.length() > 0) {
	                JSONObject firstErr = errorDetails.getJSONObject(0);
	                errCode = firstErr.optString("code", "");
	                errMsg = firstErr.optString("message", "");
	                logger.debug("Extracted errCode :: {}, errMessage :: {}", errCode, errMsg);
	            } else {
	                logger.debug("No errorDetails found in response");
	            }

	            if ("TGVCP-002".equalsIgnoreCase(errCode)) {
	                logger.debug("errCode {} matched condition, updating workflow...", errCode);

	                matchedWorkflow.ifPresentOrElse(wfDefn -> {
	                    logger.debug("Matched Workflow :: {}", wfDefn);
	                    updateApplicationWorkflow(Collections.singletonList(applnMaster),
	                            approvePushbackApplnRequest,
	                            approvePushbackApplnRequest.getRequestObj(),
	                            wfDefn,
	                            null);
	                }, () -> logger.debug("No matched workflow found for applicationId :: {}", applnMaster.getApplicationId()));
	            } else {
	                logger.debug("errCode {} did not match workflow condition", errCode);
	            }
	        } else {
	            logger.debug("No 'error' object present in response JSON");
	        }

	    } catch (Exception e) {
	        logger.error("Exception while processing API response in updateWorkflowForT24ErrCode", e);
	    }
	}


	private String getInterfaceName(String applicationId) {
		String intfName = STD_MEETING_INTF;
		if (applicationId.startsWith("R")) {
			intfName = REPEAT_COL_INTF;
		} else if (applicationId.startsWith("N")) {
			intfName = NON_MEETING_INTF;
		}
		return intfName;
	}

	/**
	 * Method to generate API Request for the StandardMeetingDay Collection
	 *
	 * @param tbUaobCustDtlsLst
	 * @param applicationMaster
	 * @param header
	 * @param tbUacoKendraDtls
	 * @param intfName
	 * @return
	 */
	private StdMeetingRequest frameStdMeetingDayColRequest(List<TbUaobCustDtls> tbUaobCustDtlsLst,
			ApplicationMaster applicationMaster, Header header, TbUacoKendraDtls tbUacoKendraDtls, String intfName) {

		StdMeetingDayBody stdMeetingDayBody = new StdMeetingDayBody();
		StdMeetingRequest stdMeetingRequest = null;
		try {
			List<StdMeetingDayPosCustomerFields> stdCustomerFieldsLst = new ArrayList<>();
			StdMeetingDayAmtFields stdDayAmtFields = new StdMeetingDayAmtFields();
			tbUaobCustDtlsLst.stream().forEach(tbUaobCustDtls -> {
				String payload = tbUaobCustDtls.getPayload();
				CollectionGroupDtls colGroupDtls;
				try {
					colGroupDtls = new ObjectMapper().readValue(payload, CollectionGroupDtls.class);
					List<CollectionMemberDtls> sortedMemberList = fetchSortedMemberList(colGroupDtls.getMembers());
					logger.debug("Sorted Member List::{}", sortedMemberList);
					colGroupDtls.setMembers(sortedMemberList);
					logger.debug("Member List after sorting::{}", colGroupDtls.getMembers());
					colGroupDtls.getMembers().stream().forEach(custDtls -> {
						logger.debug("Customer Details::{}", custDtls);
						StdMeetingDayPosCustomerFields customerFields = new StdMeetingDayPosCustomerFields();
						customerFields.setCustomerId(custDtls.getId());
						customerFields.setCusCollectionAmt(String.valueOf(custDtls.getCollAmount()));
						customerFields.setCusAttendance(custDtls.getAttend() != null ? custDtls.getAttend() : "P");
						customerFields.setCusFine("0");
						String paymentFlg = "CASH";
						if (!"CASH".equalsIgnoreCase(custDtls.getPaymentFlg()) && custDtls.getTotalUPI() > 0) {
							paymentFlg = "UNICL";
						}
						customerFields.setCusUpiFlag(paymentFlg);
						stdDayAmtFields.setTotalCash(stdDayAmtFields.getTotalCash() + custDtls.getTotalCash());
						stdDayAmtFields.setTotalUPI(stdDayAmtFields.getTotalUPI() + custDtls.getTotalUPI());
						List<StdMeetingLoanDtls> stdMeetingLoanDtlsLst = new ArrayList<>();
						int colAmtWithAdv = custDtls.getTotalAdv() + custDtls.getCollAmount();
						BigDecimal totalLoanDue = custDtls.getLoans().stream()
								.map(stdLoan -> new BigDecimal(stdLoan.getDueAmt()))
								.reduce(BigDecimal.ZERO, BigDecimal::add);
						String flag = "FULL";
						logger.debug("Comparing colAmtWithAdv::{} with loanTotalDue::{}", colAmtWithAdv, totalLoanDue);
						if (new BigDecimal(colAmtWithAdv).compareTo(totalLoanDue) < 0) {
						logger.debug("First Case Flag will be parital");
							logger.debug(
									"Flag is set as partial for the customer since colAmtWithAdv::{}, loanTotalDue::{}",
									colAmtWithAdv, custDtls.getTotalDue());
							flag = "PARTIAL";
						}
						else if (colAmtWithAdv == 0 && totalLoanDue.compareTo(BigDecimal.ZERO)==0)
						{
							logger.debug("First Case Flag will be full");
							logger.debug(
									"Flag is set as partial for the customer since collectionAmount::{}, getTotalAdv::{}",
									custDtls.getCollAmount(), custDtls.getTotalDue());
							flag = "FULL";
						}
						else if(custDtls.getCollAmount() ==0 && custDtls.getTotalAdv()>0)
						{
							logger.debug("2nd Case Flag will be Full");
							logger.debug(
									"Flag is set as full for the customer since collectionAmount::{}, getTotalAdv::{}",
									custDtls.getCollAmount(), custDtls.getTotalAdv());
							flag = "FULL";
						}

						logger.debug("Final Flag Value is "+ flag);

						logger.debug(
								"Customer Id::{}, Collection Amt:{}, AdvanceAmount:{},  Loan Total Due:{}, CustFlg:{}",
								custDtls.getId(), custDtls.getCollAmount(), custDtls.getTotalAdv(), totalLoanDue, flag);

						customerFields.setCusFlag(flag);
						if (!"FULL".equalsIgnoreCase(flag)) {
							logger.debug(
									"Customer Flag is not FULL, hence setting the loan details node, collectionAmt::{}",
									custDtls.getCollAmount());
							if (null != custDtls.getLoans() && !custDtls.getLoans().isEmpty()) {
								custDtls.getLoans().forEach(custLoanDtls -> {
									StdMeetingLoanDtls stdLoanDtls = StdMeetingLoanDtls.builder()
											.loanId(custLoanDtls.getId())
											.loanDue(String.valueOf(custLoanDtls.getDueAmt()))
											.loanCollectionAmt(String
													.valueOf(custLoanDtls.getCashAmt() + custLoanDtls.getUpiAmt()))
											.build();
									stdMeetingLoanDtlsLst.add(stdLoanDtls);
								});

								if (custDtls.getCollAmount() != 0 || custDtls.getTotalAdv()>0) {
									logger.debug(
											"Customer Flag is partial, hence setting the loan details node, loanNode::{}",
											stdMeetingLoanDtlsLst);
											 customerFields.setLoanDetails(stdMeetingLoanDtlsLst);
								}
							}
						}
						String collId = "";
						String serviceType = getServiceType(tbUacoKendraDtls, applicationMaster.getApplicationId());
						JSONObject kendraDtlsPayload = new JSONObject(tbUacoKendraDtls.getPayload());
						logger.debug("KendraDtls Payload::{}", kendraDtlsPayload);
						if ("IndividualMember".equalsIgnoreCase(serviceType)) {
							collId = "C-" + custDtls.getId() + "-1-" + kendraDtlsPayload.getString("meetingDate");
							if (kendraDtlsPayload.has("collId")
									&& !CommonUtils.checkStringNullOrEmpty(kendraDtlsPayload.getString("collId"))) {
								String collIdStr = kendraDtlsPayload.getString("collId");
								collId = "C-" + custDtls.getId() + "-1-" + collIdStr.split("-")[3];
								kendraDtlsPayload.put(COLL_ID, collId);
							}
						}
						/*
						 * else if("MultipleMember".equalsIgnoreCase(serviceType)) {
						 * kendraDtlsPayload.put(COLL_ID, kendraDtlsPayload.getString("collId")); }
						 */
						else {
							kendraDtlsPayload.put(COLL_ID, kendraDtlsPayload.getString("collId"));
						}
						tbUacoKendraDtls.setPayload(kendraDtlsPayload.toString(0));
						customerFields.setPos(custDtls.getPos());
						stdCustomerFieldsLst.add(customerFields);
					});
				} catch (Exception ex) {
					logger.error(CommonConstants.EXCEP_OCCURED, ex);
				}
			});
			logger.debug("Before sortedStdMeetingDayList::{}", stdCustomerFieldsLst);
			List<StdMeetingDayPosCustomerFields> sortedStdMeetingDayList = stdCustomerFieldsLst.stream()
					.sorted(Comparator.comparing(StdMeetingDayPosCustomerFields::getPos)).toList();

			List<StdMeetingDayCustomerFields> stdMeetingDayCustList = new ArrayList<>();
			sortedStdMeetingDayList.stream().forEach(field -> {
				StdMeetingDayCustomerFields stdMeetingDayCustomerFields = StdMeetingDayCustomerFields.builder()
						.customerId(field.getCustomerId()).cusCollectionAmt(field.getCusCollectionAmt())
						.cusFine(field.getCusFine()).cusFlag(field.getCusFlag()).cusAttendance(field.getCusAttendance())
						.cusUpiFlag(field.getCusUpiFlag()).loanDetails(field.getLoanDetails()).build();
				stdMeetingDayCustList.add(stdMeetingDayCustomerFields);
			});
			int totalColAmt = stdMeetingDayCustList.stream().mapToInt(
					stdMeetingDayCustomerFields -> Integer.parseInt(stdMeetingDayCustomerFields.getCusCollectionAmt()))
					.sum();
			stdMeetingDayBody.setCustomerDetails(stdMeetingDayCustList);
			stdMeetingDayBody.setKendraUpiTotal(String.valueOf(stdDayAmtFields.getTotalUPI()));
			stdMeetingDayBody.setKendraCashTotal(String.valueOf(stdDayAmtFields.getTotalCash()));
			stdMeetingDayBody.setFeeCollAmt("");
			stdMeetingDayBody.setTotalDisbAmt("");
			stdMeetingDayBody.setTotalFineColl("");
			stdMeetingDayBody.setNetCollection(String.valueOf(totalColAmt));
			stdMeetingDayBody.setNetCollectionUPI(String.valueOf(stdDayAmtFields.getTotalUPI()));
			stdMeetingDayBody.setNetCollectionCash(String.valueOf(stdDayAmtFields.getTotalCash()));
			stdMeetingDayBody.setInitiatedBy(applicationMaster.getCreatedBy());
			stdMeetingDayBody.setVerifiedBy("");
			stdMeetingDayBody.setAuthorizedBy(header.getUserId());
			try {
			stdMeetingDayBody.setReferenceId(applicationMaster.getApplicationId());
			} catch (Exception e) {}

			String collectionId = "";
			if (null != tbUacoKendraDtls && new JSONObject(tbUacoKendraDtls.getPayload()).has(COLL_ID)) {
				collectionId = new JSONObject(tbUacoKendraDtls.getPayload()).getString(COLL_ID);
				if (collectionId.startsWith("C")) {
					intfName = INDVIDUAL_MEMBER_COL_INTF;
				}
			}
			logger.debug("Final Id field for posting collection::{}", collectionId);
			ObjectNode emptyHeader = new ObjectMapper().createObjectNode();
			StdMeetingDayCustomerDtls stdMeetingDayCustomerDtls = StdMeetingDayCustomerDtls.builder()
					.body(stdMeetingDayBody).id(collectionId).header(emptyHeader)
					.branchId(applicationMaster.getBranchCode()).build();
			stdMeetingRequest = StdMeetingRequest.builder().appId(header.getAppId()).userId(header.getUserId())
					.interfaceName(intfName).requestObj(stdMeetingDayCustomerDtls).build();

		} catch (Exception ex) {
			logger.error(CommonConstants.EXCEP_OCCURED, ex);
		}
		logger.debug("Final stdMeetingRequest::{}", stdMeetingRequest);
		return stdMeetingRequest;
	}

	private String getServiceType(TbUacoKendraDtls tbUacoKendraDtls, String applicationId) {
		String serviceType = "ALL";
		String intfName = getInterfaceName(applicationId);
		if (null != tbUacoKendraDtls
				&& (REPEAT_COL_INTF.equalsIgnoreCase(intfName) || NON_MEETING_INTF.equalsIgnoreCase(intfName))) {
			String kendraPayloadStr = tbUacoKendraDtls.getPayload();
			if (!CommonUtils.checkStringNullOrEmpty(kendraPayloadStr) && kendraPayloadStr.startsWith("{")
					&& kendraPayloadStr.endsWith("}")) {
				JSONObject kendraPayloadObj = new JSONObject(kendraPayloadStr);
				if (kendraPayloadObj.has("type") && "INDIVIDUAL".equalsIgnoreCase(kendraPayloadObj.getString("type"))) {
					serviceType = "IndividualMember";
				} /*
					 * else if (kendraPayloadObj.has("type") &&
					 * "MULTIPLE".equalsIgnoreCase(kendraPayloadObj.getString("type"))) {
					 * serviceType = "MultipleMember"; }
					 */
			}
		}
		return serviceType;
	}

	private List<CollectionMemberDtls> fetchSortedMemberList(List<CollectionMemberDtls> members) {
		return members.stream().sorted((member1, member2) -> {
			if (member1.getPos() == 0 && member2.getPos() != 0) {
				return 1;
			} else if (member1.getPos() != 0 && member2.getPos() == 0) {
				return -1;
			} else {
				return Integer.compare(member1.getPos(), member2.getPos());
			}
		}).toList();
	}

	/**
	 * Method to check the Standard Meeting Day collection API response for the
	 * status
	 *
	 * @param applicationMaster
	 * @param val
	 */
	private void validateStdMeetingColAPIResp(ApplicationMaster applicationMaster, Object val) {
		String extApiResponse = val.toString();
		logger.debug("API response::{}", extApiResponse);
		if (val instanceof HashMap<?, ?> || val instanceof ArrayList<?>) {
			try {
				extApiResponse = new ObjectMapper().writeValueAsString(val);
				if (!CommonUtils.checkStringNullOrEmpty(extApiResponse)) {
					JSONObject responseJSON = new JSONObject(extApiResponse);
					logger.debug("API response in JSON Format::{}", responseJSON);
					if (responseJSON.has(HEADER_LW_TXT) && responseJSON.getJSONObject(HEADER_LW_TXT).has("status")
							&& "success"
									.equalsIgnoreCase(responseJSON.getJSONObject(HEADER_LW_TXT).getString("status"))) {
						logger.debug("Success response from the API");
						applicationMaster.setApplicationStatus(APPROVED);
						applicationMaster.setCurrentStage(APPROVED);
					} else if (responseJSON.has(ERR_NODE) && responseJSON.get(ERR_NODE) instanceof String) {
						logger.debug("Socket exception error response from the API");
						applicationMaster.setApplicationStatus(INPROGRESS);
						applicationMaster.setCurrentStage(FAILED);
						applicationMaster.setErrorCode("SOCKET_ERR");
						applicationMaster.setErrorType(API_FAIL);
						applicationMaster.setErrorMessage(responseJSON.get(ERR_NODE).toString());
					} else if (responseJSON.has(ERR_NODE) && responseJSON.get(ERR_NODE) instanceof JSONObject
							&& responseJSON.getJSONObject(ERR_NODE).has(ERR_DTLS_NODE)) {
						logger.debug("Failure response from the API with error.");
						if (responseJSON.getJSONObject(ERR_NODE).get(ERR_DTLS_NODE) instanceof JSONArray) {
							JSONArray errorDtls = responseJSON.getJSONObject(ERR_NODE).getJSONArray(ERR_DTLS_NODE);
							String errorCode = errorDtls.getJSONObject(0).optString("code");
							String message = errorDtls.getJSONObject(0).optString("message");
							if ("TGVCP-002".equalsIgnoreCase(errorCode)) {
								logger.debug("Failure response from the API with errorCode.");
								Pattern p = Pattern.compile(RECORD_EXIST_PATTERN);
								Matcher m = p.matcher(message);
								Pattern tillClosedPattern = Pattern.compile(TILL_STATUS_PATTERN);
								Matcher tillClosedMatcher = tillClosedPattern.matcher(message);
								/*if (!(m.find() || tillClosedMatcher.find())) {
									//applicationMaster.setApplicationStatus(APPROVED);
									//applicationMaster.setCurrentStage(APPROVED);
									
								} else {*/
									applicationMaster.setApplicationStatus(APPROVED);
									applicationMaster.setCurrentStage(APPROVED);
									applicationMaster.setErrorType(API_FAIL);
									applicationMaster.setErrorMessage(errorCode + " - " + message);
									applicationMaster.setErrorCode(errorCode);
							//	}
								/*if ("CANNOT ACCESS RECORD IN ANOTHER COMPANY".equalsIgnoreCase(message)) {
									applicationMaster.setApplicationStatus(INPROGRESS);
									applicationMaster.setCurrentStage(FAILED);
									applicationMaster.setErrorType(API_FAIL);
									applicationMaster.setErrorMessage(errorCode + " - " + message);
									applicationMaster.setErrorCode(errorCode);
								} else {
									applicationMaster.setApplicationStatus(APPROVED);
									applicationMaster.setCurrentStage(APPROVED);
								}*/
							} else {
								applicationMaster.setApplicationStatus(INPROGRESS);
								applicationMaster.setCurrentStage(FAILED);
								applicationMaster.setErrorType(API_FAIL);
								applicationMaster.setErrorMessage(errorCode + " - " + message);
								applicationMaster.setErrorCode(errorCode);
							}
						}
					} else {
						logger.debug("Failure response status from the API.");
						applicationMaster.setCurrentStage(FAILED);
						applicationMaster.setApplicationStatus(INPROGRESS);
					}
				} else {
					applicationMaster.setCurrentStage(FAILED);
					applicationMaster.setApplicationStatus(INPROGRESS);
				}
			} catch (Exception e) {
				applicationMaster.setCurrentStage(FAILED);
				applicationMaster.setApplicationStatus(INPROGRESS);
				logger.error(CommonConstants.EXCEP_OCCURED, e);
			}
		}
	}

	private Mono<Response> generateResponse(ResponseHeader responseHeader, ResponseBody responseBody,
			Response response) {
		responseBody.setResponseObj("");
		response.setResponseBody(responseBody);
		response.setResponseHeader(responseHeader);
		return Mono.just(response);
	}

	public static List<StdMeetingDayPosCustomerFields> processCustomerDetails(List<CustomerDetails> customerDtlsList,
			List<TbUaobCustDtls> tbUaobCustDtlsLst, Optional<TbUacoKendraDtls> tbUacoKendraDtlsOpt) {

		List<StdMeetingDayPosCustomerFields> stdCustomerFieldsLst = new ArrayList<>();
		try {
			ObjectMapper objectMapper = new ObjectMapper();
			Map<String, JsonNode> customerDataMap = tbUaobCustDtlsLst.stream().flatMap(tbCust -> {
				try {
					JsonNode members = objectMapper.readTree(tbCust.getPayload()).path("members");
					return toCustomerNodeStream(members);
				} catch (Exception e) {
					logger.error("Exception::", e);
					return Stream.empty();
				}
			}).filter(Objects::nonNull).collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (a, b) -> a));

			for (CustomerDetails cust : customerDtlsList) {
				JsonNode member = customerDataMap.get(cust.getCustomerId());
				if (member == null)
					continue;

				StdMeetingDayPosCustomerFields customerFields = new StdMeetingDayPosCustomerFields();
				customerFields.setCustomerId(cust.getCustomerId());
				customerFields.setCusCollectionAmt(member.path("collAmount").asText());
				customerFields.setCusAttendance(member.path("attend").asText("P"));
				customerFields.setCusFine("0");

				// Determine Payment Flag
				String paymentFlg = member.path("totalUPI").asInt() > 0 ? "UNICL" : "CASH";
				customerFields.setCusUpiFlag(paymentFlg);

				// Calculate total amounts
				int totalAdv = member.path("totalAdv").asInt();
				int collAmount = member.path("collAmount").asInt();
				int colAmtWithAdv = totalAdv + collAmount;

				// Loan processing
				List<StdMeetingLoanDtls> stdMeetingLoanDtlsLst = new ArrayList<>();
				BigDecimal totalLoanDue = BigDecimal.ZERO;
				for (JsonNode loan : member.path("loans")) {
					totalLoanDue = totalLoanDue.add(new BigDecimal(loan.path("dueAmt").asInt()));
				}
				String flag = colAmtWithAdv >= totalLoanDue.intValue() ? "FULL" : "PARTIAL";
				customerFields.setCusFlag(flag);

				if ("PARTIAL".equals(flag)) {
					for (JsonNode loan : member.path("loans")) {
						StdMeetingLoanDtls stdMeetingLoanDtls = StdMeetingLoanDtls.builder()
								.loanId(loan.path("id").asText()).loanDue(loan.path("dueAmt").asText())
								.loanCollectionAmt(
										String.valueOf(loan.path("cashAmt").asInt() + loan.path("upiAmt").asInt()))
								.build();
						stdMeetingLoanDtlsLst.add(stdMeetingLoanDtls);
					}
					customerFields.setLoanDetails(stdMeetingLoanDtlsLst);
				}

				String serviceType = "ALL";
				if (tbUacoKendraDtlsOpt.isPresent()) {
					String kendraPayloadStr = tbUacoKendraDtlsOpt.get().getPayload();
					if (!CommonUtils.checkStringNullOrEmpty(kendraPayloadStr) && kendraPayloadStr.startsWith("{")
							&& kendraPayloadStr.endsWith("}")) {
						JSONObject kendraPayloadObj = new JSONObject(kendraPayloadStr);
						if (kendraPayloadObj.has("type")
								&& "INDIVIDUAL".equalsIgnoreCase(kendraPayloadObj.getString("type"))) {
							serviceType = "IndividualMember";
						}
					}
					JSONObject kendraDtlsPayload = new JSONObject(tbUacoKendraDtlsOpt.get().getPayload());
					logger.debug("KendraDtls Payload::{}", kendraDtlsPayload);
					if ("IndividualMember".equalsIgnoreCase(serviceType)) {
						String collId = "C-" + cust.getCustomerId() + "-1-"
								+ kendraDtlsPayload.getString("meetingDate");
						if (kendraDtlsPayload.has("collId")
								&& !CommonUtils.checkStringNullOrEmpty(kendraDtlsPayload.getString("collId"))) {
							String collIdStr = kendraDtlsPayload.getString("collId");
							collId = "C-" + cust.getCustomerId() + "-1-" + collIdStr.split("-")[3];
						}
						kendraDtlsPayload.put(COLL_ID, collId);
					} else {
						kendraDtlsPayload.put(COLL_ID, kendraDtlsPayload.getString("collId"));
					}
					tbUacoKendraDtlsOpt.get().setPayload(kendraDtlsPayload.toString(0));
				}
				customerFields.setPos(member.path("pos").asInt());
				stdCustomerFieldsLst.add(customerFields);
			}
		} catch (Exception ex) {
			logger.error("Exception::", ex);
		}
		return stdCustomerFieldsLst;
	}

	private List<CustomerDetails> frameRptNonMeetingColResponse(Object val) {

		ResponseHeader respHeader = new ResponseHeader();
		ResponseBody respBody = new ResponseBody();
		List<CustomerDetails> customerDetailsList = new ArrayList<>();
		if (val instanceof HashMap<?, ?> || val instanceof ArrayList<?>) {
			try {
				String extApiResp = new ObjectMapper().writeValueAsString(val);
				if (extApiResp.startsWith("{") && extApiResp.endsWith("}")) {
					JSONObject respJSON = new JSONObject(extApiResp);
					if (respJSON.has("body") && !"[]".equalsIgnoreCase(respJSON.get("body").toString())) {
						customerDetailsList = new ObjectMapper().readValue(
								respJSON.getJSONArray("body").getJSONObject(0).getJSONArray("result").getJSONObject(0)
										.getJSONArray("customerDetails").toString(),
								new TypeReference<List<CustomerDetails>>() {
								});
					} else {
						CommonUtils.generateHeaderForNoResult(respHeader);
					}
					respBody.setResponseObj(respJSON.toString());
				}
			} catch (Exception ex) {
				logger.error("exception at fetchRepeatCollection: ", ex);
				CommonUtils.generateHeaderForFailure(respHeader, CommonConstants.EXCEPTION_MSG);
			}
		}
		return customerDetailsList;
	}

	private Mono<Object> invokeRepeatCollectionAPI(List<StdMeetingDayCustomerFields> stdMeetingDayCustList,
			ApplicationMaster applnMaster, Optional<TbUacoKendraDtls> tbUacoKendraDtlsOpt, String intfName,
			Header header) {
		StdMeetingDayBody stdMeetingDayBody = new StdMeetingDayBody();
		StdMeetingRequest stdMeetingRequest = null;
		StdMeetingDayAmtFields stdDayAmtFields = new StdMeetingDayAmtFields();

		int totalColAmt = stdMeetingDayCustList.stream().mapToInt(
				stdMeetingDayCustomerFields -> Integer.parseInt(stdMeetingDayCustomerFields.getCusCollectionAmt()))
				.sum();
		stdMeetingDayBody.setCustomerDetails(stdMeetingDayCustList);
		stdMeetingDayBody.setKendraUpiTotal(String.valueOf(stdDayAmtFields.getTotalUPI()));
		stdMeetingDayBody.setKendraCashTotal(String.valueOf(stdDayAmtFields.getTotalCash()));
		stdMeetingDayBody.setFeeCollAmt("");
		stdMeetingDayBody.setTotalDisbAmt("");
		stdMeetingDayBody.setTotalFineColl("");
		stdMeetingDayBody.setNetCollection(String.valueOf(totalColAmt));
		stdMeetingDayBody.setNetCollectionUPI(String.valueOf(stdDayAmtFields.getTotalUPI()));
		stdMeetingDayBody.setNetCollectionCash(String.valueOf(stdDayAmtFields.getTotalCash()));
		stdMeetingDayBody.setInitiatedBy(applnMaster.getCreatedBy());
		stdMeetingDayBody.setVerifiedBy("");
		stdMeetingDayBody.setAuthorizedBy(header.getUserId());
		try {
			stdMeetingDayBody.setReferenceId(applnMaster.getApplicationId());
		}
		catch(Exception e) {}
		

		String collectionId = "";
		String interfaceName = intfName;
		if (tbUacoKendraDtlsOpt.isPresent() && new JSONObject(tbUacoKendraDtlsOpt.get().getPayload()).has(COLL_ID)) {
			collectionId = new JSONObject(tbUacoKendraDtlsOpt.get().getPayload()).getString(COLL_ID);
			if (collectionId.startsWith("C")) {
				interfaceName = INDVIDUAL_MEMBER_COL_INTF;
			}
		}
		logger.debug("Final Id field for posting collection::{}", collectionId);
		ObjectNode emptyHeader = new ObjectMapper().createObjectNode();
		StdMeetingDayCustomerDtls stdMeetingDayCustomerDtls = StdMeetingDayCustomerDtls.builder()
				.body(stdMeetingDayBody).id(collectionId).header(emptyHeader).branchId(applnMaster.getBranchCode())
				.build();
		stdMeetingRequest = StdMeetingRequest.builder().appId(header.getAppId()).userId(header.getUserId())
				.interfaceName(interfaceName).requestObj(stdMeetingDayCustomerDtls).build();

		logger.debug("Invoking repeat bulk collection for applicationId::{} with request::{}",
				applnMaster.getApplicationId(), stdMeetingRequest);
		return interfaceAdapter.callExternalService(header, stdMeetingRequest, stdMeetingRequest.getInterfaceName(),
				true);
	}

	private static java.util.stream.Stream<Map.Entry<String, JsonNode>> toCustomerNodeStream(JsonNode members) {
		List<Map.Entry<String, JsonNode>> entries = new ArrayList<>();
		for (JsonNode member : members) {
			entries.add(Map.entry(member.path("id").asText(), member));
		}
		return entries.stream();
	}
}
