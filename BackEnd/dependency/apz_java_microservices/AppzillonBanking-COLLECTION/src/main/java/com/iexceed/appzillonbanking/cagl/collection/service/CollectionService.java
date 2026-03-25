package com.iexceed.appzillonbanking.cagl.collection.service;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Function;
import java.util.stream.Collectors;

import com.fasterxml.jackson.databind.JsonNode;
import com.iexceed.appzillonbanking.cagl.collection.domain.ab.*;
import com.iexceed.appzillonbanking.cagl.collection.payload.*;
import com.iexceed.appzillonbanking.cagl.collection.repository.ab.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.iexceed.appzillonbanking.cagl.collection.constants.CommonConstants;
import com.iexceed.appzillonbanking.cagl.collection.domain.ab.ApplicationMaster;
import com.iexceed.appzillonbanking.cagl.collection.domain.ab.ApplicationWorkflow;
import com.iexceed.appzillonbanking.cagl.collection.domain.ab.CollectionReport;
import com.iexceed.appzillonbanking.cagl.collection.domain.ab.TbUacoDepositPointsDtls;
import com.iexceed.appzillonbanking.cagl.collection.domain.ab.TbUacoKendraDtls;
import com.iexceed.appzillonbanking.cagl.collection.domain.ab.TbUaobCustDtls;
import com.iexceed.appzillonbanking.cagl.collection.domain.ab.WorkflowDefinition;
import com.iexceed.appzillonbanking.cagl.collection.util.CollectionUtil;
import com.iexceed.appzillonbanking.core.payload.Header;
import com.iexceed.appzillonbanking.core.payload.Response;
import com.iexceed.appzillonbanking.core.payload.ResponseBody;
import com.iexceed.appzillonbanking.core.payload.ResponseHeader;
import com.iexceed.appzillonbanking.core.utils.CommonUtils;
import com.iexceed.appzillonbanking.interfaceAdapter.service.InterfaceAdapter;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;


@Service
public class CollectionService {

	@Autowired
	private ApplicationMasterRepository applicationMasterRepository;

	@Autowired
	private TbUacoKendraDtlsRepository tbUacoKendraDtlsRepository;

	@Autowired
	private TbUaobCustomerDtlsRepo tbUaobCustomerDtlsRepo;

	@Autowired
	private ApplicationWorkflowRepository applicationWorkflowRepository;

	@Autowired
	private WorkflowDefinitionRepository workflowDefinitionRepository;

	@Autowired
	private TbUacoDepositPointsRepository tbUacoDepositPointsRepository;

	@Autowired
	private InterfaceAdapter interfaceAdapter;
	
	@Autowired
	private CollectionReportRepository collectionReportRepository;

	@Autowired
	private TbUalnLoanDtlsRepository tbUalnLoanDtlsRepository;

	@Autowired
	private TbUacoInterimAppMasterRepository tbUacoInterimApplicationMasterRepository;
	
	@Autowired
	private TbAsmiUserRoleRepo tbAsmiUserRoleRepo;
	
	@Autowired
	private TbAbnfTaskNotifRepo tbAbnfTaskNotifRepo;
	
	

	private static final String APPLN_TYPE = "COLLECTION";
	private static final String REP_APPLN_TYPE = "REPEAT_COLLECTION";
	private static final String NON_MTNG_APPLN_TYPE = "NONMEETING_COLLECTION";
	private static final String FETCH_KENDRA_INTF = "FetchKendraInfo";
	private static final String GET_RPT_COL_INTF = "GetRepeatCollection";
	private static final String GET_NON_MTNG_COL_INTF = "GetNonMeetingCollection";
	private static final String BM_ROLE = "BM";
	private static final String APPROVED = "APPROVED";

	private static final Logger logger = LogManager.getLogger(CollectionService.class);

 public Response createApplication(CollectionRequest collectionRequest, Header header) {
	logger.debug("Inside createApplication request::{}", collectionRequest);

	Response response;
	try {
		List<CollectionsData> kendraDtlsLst = collectionRequest.getRequestObj().getKendraDtls();
		logger.debug("Inside kendraDtlsLst request::{}", kendraDtlsLst);

		for (CollectionsData kendraData : kendraDtlsLst) {
			logger.debug("kendraData request::{}", kendraData);

			// 1) Version mismatch correction
			fixVersionMismatchIfNeeded(kendraData);

			// 2) Validate collId/collDate against meetingDate
			if (!isCollectionDateValid(kendraData)) {
				return failureResponse("collId or collDate is different from meeting date");
			}

			// 3) Build and save ApplicationMaster
			ApplicationDetails applnDtls = kendraData.getApplnDtls();
			logger.debug("applnDtls request::{}", applnDtls);

			String applnDt = applnDtls.getApplnDate();
			String applnType = getApplicationType(applnDtls.getApplnId());
			String userRole = Optional.ofNullable(collectionRequest.getKmUserRole())
					.filter(role -> !role.trim().isEmpty())
					.orElse("KM");
			String addInfo = userRole + "~" + collectionRequest.getUserId();

			ApplicationMaster applnMaster = buildApplicationMaster(applnDtls, applnType, addInfo, applnDt);
			logger.debug("applnMaster::{}", applnMaster);
			applicationMasterRepository.save(applnMaster);

			// 4) Delete version=0 masters if current version != 0
			deleteZeroVersionApplicationMasterIfNeeded(applnDtls, applnDt);

			// 5) Save kendra details
			TbUacoKendraDtls tbUacoKendraDtls = buildKendraDtls(kendraData, applnDtls);
			logger.debug("tbUacoKendraDtls::{}", tbUacoKendraDtls);
			tbUacoKendraDtlsRepository.save(tbUacoKendraDtls);

			// 6) Save customer details
			List<TbUaobCustDtls> tbUaobCustDtlsLst = buildCustomerDtls(kendraData, applnDtls, applnType);
			logger.debug("tbUaobCustDtlsLst::{}", tbUaobCustDtlsLst);
			tbUaobCustomerDtlsRepo.saveAll(tbUaobCustDtlsLst);

			// 7) Save workflow
			List<ApplicationWorkflow> applnWFLst = buildWorkflows(kendraData, applnDtls);
			logger.debug("ApplnWFLst::{}", applnWFLst);
			logger.error("ApplnWFLst::{}", applnWFLst);
			applicationWorkflowRepository.saveAll(applnWFLst);
		}

		// 8) Cash deposit points
		storeCashDepositDtls(collectionRequest);

		response = successResponse("Application Created Successfully");
	} catch (Exception ex) {
		logger.error(CommonConstants.EXCEP_OCCURED, ex);
		response = failureResponse(CommonConstants.EXCEPTION_MSG);
	}

	// Post-processing (outside main try/catch as in original)
	handleMisSave(collectionRequest);
	handleNotifSave(collectionRequest);

	logger.debug("Inside createApplication response::{}", response);
	return response;
}

	/* ------------------------- helpers ------------------------- */

	private void fixVersionMismatchIfNeeded(CollectionsData kendraData) {
		if (kendraData.getApplnWFDtls() != null && kendraData.getApplnWFDtls().size() == 1) {
						String applnVerNo = kendraData.getApplnDtls().getVerNo();
						String wfVerNo = kendraData.getApplnWFDtls().get(0).getVerNo();
						if ("0".equals(applnVerNo) && !"0".equals(wfVerNo)) {
							applnVerNo = "1";
							kendraData.getApplnDtls().setVerNo("1");
						}
						if (!applnVerNo.equals(wfVerNo)) {
							kendraData.getApplnWFDtls().get(0)
									.setVerNo(applnVerNo);
						}
		}
	}

	private boolean isCollectionDateValid(CollectionsData kendraData) {
		String collId = kendraData.getColldtls().getCollId();
		logger.debug("Original collId: {}", collId);

		String[] parts = collId.split("-");
		String lastPart = parts[parts.length - 1];
		logger.debug("Extracted lastPart from collId: {}", lastPart);

		String collDate = kendraData.getColldtls().getCollDate();
		logger.debug("collDate from request: {}", collDate);

		String meetingDate = kendraData.getMeetingDate();
		logger.debug("Original meetingDate: {}", meetingDate);

		String normalizedMeetingDate = meetingDate.replace("-", "");
		logger.debug("Normalized meetingDate (YYYYMMDD): {}", normalizedMeetingDate);

		boolean ok = lastPart.equals(normalizedMeetingDate) && collDate.equals(normalizedMeetingDate);
		if (!ok) {
			logger.error("Mismatch detected: lastPart={} OR collDate={} does not match meetingDate={}",
					lastPart, collDate, normalizedMeetingDate);
		}
		return ok;
	}

	private ApplicationMaster buildApplicationMaster(
			ApplicationDetails applnDtls,
			String applnType,
			String addInfo,
			String applnDt
	) {
		boolean isVerZero = "0".equals(applnDtls.getVerNo());

		if (isVerZero) {
			logger.error(" applnDtls = {}  , applnId = {} , verNo = {}",
					applnDtls, applnDtls.getApplnId(), applnDtls.getVerNo());
			logger.debug("version num is 0");

			ApplicationMaster applnMaster = ApplicationMaster.builder()
					.appId(applnDtls.getAppId())
					.applicationId(applnDtls.getApplnId())
					.versionNum(applnDtls.getVerNo())
					.kendraId(applnDtls.getKendraId())
					.applicationDate(LocalDate.parse(applnDt))
					.createdBy(applnDtls.getCreatedBy())
					.applicationType(applnType)
					.applicationStatus("")
					.branchCode(applnDtls.getBrnCode())
					.currentStage("")
					.kmId(applnDtls.getKmId())
					.leader(applnDtls.getLeader())
					.kendraName(applnDtls.getKendraName())
					.amount(new BigDecimal(applnDtls.getAmount()))
					.applicationRefNo(applnDtls.getRefNo())
					.addInfo(addInfo)
					.build();

			logger.debug("version num is 0 and data to be saved is :: {} ", applnMaster);
			return applnMaster;
		}

		logger.error(" applnDtls = {}  ", applnDtls);
		logger.debug("version num is not 0");

		String ver = normalizeVersionString(applnDtls.getVerNo());

		ApplicationMaster applnMaster = ApplicationMaster.builder()
				.appId(applnDtls.getAppId())
				.applicationId(applnDtls.getApplnId())
				.versionNum(ver)
				.kendraId(applnDtls.getKendraId())
				.applicationDate(LocalDate.parse(applnDt))
				.createdBy(applnDtls.getCreatedBy())
				.applicationType(applnType)
				.applicationStatus("INPROGRESS")
				.branchCode(applnDtls.getBrnCode())
				.currentStage("CASHHANDOVER")
				.kmId(applnDtls.getKmId())
				.leader(applnDtls.getLeader())
				.kendraName(applnDtls.getKendraName())
				.amount(new BigDecimal(applnDtls.getAmount()))
				.applicationRefNo(applnDtls.getRefNo())
				.addInfo(addInfo)
				.build();

		logger.debug("version num is not 0 and data to be saved is :: {} ", applnMaster);
		return applnMaster;
	}

	private void deleteZeroVersionApplicationMasterIfNeeded(ApplicationDetails applnDtls, String applnDt) {
		if ("0".equals(applnDtls.getVerNo())) {
			return;
		}

		List<ApplicationMaster> appMasterWithZero =
				applicationMasterRepository.findByApplicationIdAndApplicationDateAndVersionNum(
						applnDtls.getApplnId(), LocalDate.parse(applnDt), "0");

		if (appMasterWithZero != null && !appMasterWithZero.isEmpty()) {
			for (ApplicationMaster appMaster : appMasterWithZero) {
				logger.error(" deleting appMaster = {}  ", appMaster);
				applicationMasterRepository.delete(appMaster);
			}
			logger.debug("Deleted {} application master record(s) with version number 0 for applicationId: {}",
					appMasterWithZero.size(), applnDtls.getApplnId());
		}
	}

	private TbUacoKendraDtls buildKendraDtls(CollectionsData kendraData, ApplicationDetails applnDtls) {
		String collId = kendraData.getColldtls().getCollId();
		String collDate = kendraData.getColldtls().getCollDate();

		JSONObject kendraDtlsJSON = new JSONObject();
		kendraDtlsJSON.put("lat", kendraData.getLat());
		kendraDtlsJSON.put("long", kendraData.getLongitude());
		kendraDtlsJSON.put("updLoc", kendraData.getUpdLoc());
		kendraDtlsJSON.put("meetingDate", kendraData.getMeetingDate());
		kendraDtlsJSON.put("meetingDay", kendraData.getMeetingDay());
		kendraDtlsJSON.put("collId", collId);
		kendraDtlsJSON.put("colDate", collDate);
		kendraDtlsJSON.put("type", kendraData.getType());
		kendraDtlsJSON.put("totCollAmt", kendraData.getColldtls().getTotCollAmt());
		kendraDtlsJSON.put("totalAdv", kendraData.getColldtls().getTotalAdv());
		kendraDtlsJSON.put("totalDue", kendraData.getColldtls().getTotalDue());
		kendraDtlsJSON.put("netDue", kendraData.getColldtls().getNetDue());

		return TbUacoKendraDtls.builder()
				.appId(applnDtls.getAppId())
				.applicationId(applnDtls.getApplnId())
				.versionNum(normalizeVersionString(applnDtls.getVerNo()))
				.kendraId(kendraData.getKendraId())
				.kendraName(kendraData.getKendraName())
				.startTime(kendraData.getStartTime())
				.payload(kendraDtlsJSON.toString())
				.build();
	}

	private List<TbUaobCustDtls> buildCustomerDtls(CollectionsData kendraData, ApplicationDetails applnDtls, String applnType)
			throws Exception {

		List<TbUaobCustDtls> tbUaobCustDtlsLst = new ArrayList<>();
		List<CollectionGroupDtls> collectionGrpDtlLst = kendraData.getColldtls().getCollectionGroupDtls();
		AtomicInteger curMemberPos = new AtomicInteger(1);

		for (CollectionGroupDtls collGroupDtls : collectionGrpDtlLst) {

			collGroupDtls.getMembers().forEach(member ->
					logger.debug("Before Save - MemberId: {}, Reason: {}", member.getId(), member.getReason()));

			List<CollectionMemberDtls> updatedMembers = collGroupDtls.getMembers().stream()
					.map(member -> CollectionMemberDtls.builder()
							.id(member.getId())
							.name(member.getName())
							.depname(member.getDepname())
							.totalDue(member.getTotalDue())
							.totalAdv(member.getTotalAdv())
							.netDue(member.getNetDue())
							.collAmount(member.getCollAmount())
							.attend(member.getAttend())
							.parFlg(member.getParFlg())
							.parAmt(member.getParAmt())
							.advAmt(member.getAdvAmt())
							.totalCash(member.getTotalCash())
							.totalUPI(member.getTotalUPI())
							.outstandingAmt(member.getOutstandingAmt())
							.advAdj(member.getAdvAdj())
							.paymentFlg(member.getPaymentFlg())
							.reason(member.getReason())
							.loans(member.getLoans())
							.mahiCollection(member.getMahiCollection())
							.pos(curMemberPos.getAndIncrement())
							.build()
					)
					.toList();

			collGroupDtls.setMembers(updatedMembers);

			String custDtlId = CollectionUtil.generateCustDtlId(
					collGroupDtls.getId(),
					kendraData.getKendraId(),
					kendraData.getMeetingDate(),
					applnType
			);

			String payload = new ObjectMapper().writeValueAsString(collGroupDtls);

			TbUaobCustDtls tbUaobCustDtls = TbUaobCustDtls.builder()
					.custDtlId(custDtlId)
					.appId(applnDtls.getAppId())
					.applicationId(applnDtls.getApplnId())
					.versionNo(normalizeVersionString(applnDtls.getVerNo()))
					.kendraId(kendraData.getKendraId())
					.groupId(collGroupDtls.getId())
					.kycDetails(null)
					.bankDtls(null)
					.payload(payload)
					.build();

			tbUaobCustDtlsLst.add(tbUaobCustDtls);
			logger.error("tbUaobCustDtlsLst {}" , tbUaobCustDtls);
		}

		return tbUaobCustDtlsLst;
	}

	private List<ApplicationWorkflow> buildWorkflows(CollectionsData kendraData, ApplicationDetails applnDtls) {
		List<ApplicationWFDetails> applnWFDtls = kendraData.getApplnWFDtls();
		List<ApplicationWorkflow> applnWFLst = new ArrayList<>();

		if (applnWFDtls != null && !applnWFDtls.isEmpty()) {
			for (int i = 0; i < applnWFDtls.size(); i++) {
				ApplicationWFDetails wf = applnWFDtls.get(i);

				boolean hasDefnKeys =
						!CommonUtils.checkStringNullOrEmpty(wf.getWorkflowId())
								&& !CommonUtils.checkStringNullOrEmpty(wf.getFromStageId())
								&& !CommonUtils.checkStringNullOrEmpty(wf.getStageSeqNo());

				if (hasDefnKeys) {
					Optional<WorkflowDefinition> wfDefnOpt =
							workflowDefinitionRepository.findByAppIdAndStageSeqNumAndFromStageIdAndWorkFlowId(
									wf.getAppId(),
									Integer.parseInt(wf.getStageSeqNo()),
									wf.getFromStageId(),
									wf.getWorkflowId()
							);

					logger.debug("applnWFDefnOpt::{}", wfDefnOpt);

					if (wfDefnOpt.isPresent()) {
						LocalDateTime createDtTime = LocalDateTime.now();
						ApplicationWorkflow applnWF = ApplicationWorkflow.builder()
								.appId(wf.getAppId())
								.applicationId(wf.getApplnId())
								.versionNum(normalizeVersionInt(wf.getVerNo()))
								.workflowSeqNum(i + 1)
								.createdBy(wf.getCreatedBy())
								.createdUserName(wf.getKmUserName())
								.createTs(createDtTime)
								.applicationStatus(wfDefnOpt.get().getNextWFStatus())
								.remarks(wf.getRemarks())
								.currentRole(wf.getUsrRole())
								.nextWorkFlowStage(wfDefnOpt.get().getNextStageId())
								.build();
						logger.debug("ApplnWF1::{}", applnWF);
						applnWFLst.add(applnWF);
					}
					continue;
				}

				// No definition keys -> use request fields, special handling for ver=0 (seq=0)
				LocalDateTime createDtTime = parseCreateTsOrNow(wf.getCreateTs(), CollectionUtil.DT_TIME_FORMAT);

				int seqNum = "0".equals(wf.getVerNo()) ? 0 : (i + 1);

				ApplicationWorkflow applnWF = ApplicationWorkflow.builder()
						.appId(wf.getAppId())
						.applicationId(wf.getApplnId())
						.versionNum(normalizeVersionInt(wf.getVerNo()))
						.workflowSeqNum(seqNum)
						.createdBy(wf.getCreatedBy())
						.createdUserName(wf.getKmUserName())
						.createTs(createDtTime)
						.applicationStatus(wf.getAppStatus())
						.remarks(wf.getRemarks())
						.currentRole(wf.getUsrRole())
						.nextWorkFlowStage(wf.getNextWFStage())
						.build();

				applnWFLst.add(applnWF);
				logger.debug("ApplnWF::{}", applnWF);
			}

			return applnWFLst;
		}

		// Default workflow when wf list is empty (original logic preserved)
		logger.debug("application detail is :: {} ", applnDtls.toString());

		LocalDateTime createDtTime = LocalDateTime.now();
		if (!CommonUtils.checkStringNullOrEmpty(applnDtls.getCreateTs())) {
			try {
				DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd:MM:yyyy HH:mm");
				createDtTime = LocalDateTime.parse(applnDtls.getCreateTs(), dtf);
			} catch (DateTimeParseException e) {
				logger.error("Invalid createTs format: {}, using current time. Error: {}",
						applnDtls.getCreateTs(), e.getMessage());
			}
		}

		ApplicationWorkflow defaultApplnWorkflow = ApplicationWorkflow.builder()
				.appId(applnDtls.getAppId())
				.applicationId(applnDtls.getApplnId())
				.versionNum(normalizeVersionInt(applnDtls.getVerNo()))
				.workflowSeqNum(1)
				.createdBy(applnDtls.getCreatedBy())
				.createdUserName("")
				.createTs(createDtTime)
				.applicationStatus("INPROGRESS")
				.remarks("Cash collected from current kendra")
				.currentRole("KM")
				.nextWorkFlowStage("CASHHANDOVER")
				.build();

		applnWFLst.add(defaultApplnWorkflow);
		logger.debug("DefaultApplnWF::{}", defaultApplnWorkflow);
		return applnWFLst;
	}

	private LocalDateTime parseCreateTsOrNow(String createTs, String pattern) {
		LocalDateTime createDtTime = LocalDateTime.now();
		if (createTs != null) {
			DateTimeFormatter dtf = DateTimeFormatter.ofPattern(pattern);
			createDtTime = LocalDateTime.parse(createTs, dtf);
		}
		return createDtTime;
	}

	private String normalizeVersionString(String verNo) {
		return (verNo == null || verNo.trim().isEmpty()) ? "1" : verNo;
	}

	private int normalizeVersionInt(String verNo) {
		return (verNo == null || verNo.trim().isEmpty()) ? 1 : Integer.valueOf(verNo);
	}

	private Response successResponse(String msg) {
		ResponseHeader respHeader = ResponseHeader.builder()
				.responseCode(com.iexceed.appzillonbanking.core.constants.CommonConstants.SUCCESS)
				.responseMessage(msg)
				.build();
		ResponseBody respBody = ResponseBody.builder().responseObj("").build();
		return Response.builder().responseHeader(respHeader).responseBody(respBody).build();
	}

	private Response failureResponse(String msg) {
		ResponseHeader respHeader = ResponseHeader.builder()
				.responseCode(com.iexceed.appzillonbanking.core.constants.CommonConstants.FAILURE)
				.responseMessage(msg)
				.build();
		ResponseBody respBody = ResponseBody.builder().responseObj("").build();
		return Response.builder().responseHeader(respHeader).responseBody(respBody).build();
	}

	private void handleMisSave(CollectionRequest collectionRequest) {
		String version = Optional.ofNullable(collectionRequest.getRequestObj()
						.getKendraDtls()
						.get(0)
						.getApplnDtls()
						.getVerNo())
				.filter(v -> !v.trim().isEmpty())
				.orElse("1");

		if (!"0".equals(version)) {
			try {
				saveMisData(collectionRequest);
			} catch (Exception e) {
				logger.debug("Error while saving MIS data: {}", e.getMessage(), e);
			}
		} else {
			logger.debug("Skipping MIS save as application version is 0");
		}
	}

	private void handleNotifSave(CollectionRequest collectionRequest) {
		try {
			saveNotifDetails(collectionRequest);
		} catch (Exception e) {
			logger.debug("Error while saving notif data: {}", e.getMessage(), e);
		}
	}

	@Transactional(rollbackFor = Exception.class)
	private void saveNotifDetails(CollectionRequest collectionRequest) {

		logger.debug("Entered saveNotifDetails() with collectionRequest={}", collectionRequest);

		String fromUserRole = Optional.ofNullable(collectionRequest.getKmUserRole())
				.filter(role -> !role.trim().isEmpty()).orElse("KM");
		String fromUserId = collectionRequest.getUserId();

		logger.debug("Resolved fromUserRole={}, fromUserId={}", fromUserRole, fromUserId);

		List<TbAbnfTaskNotif> toInsert = new ArrayList<>();

		if (collectionRequest.getRequestObj() == null || collectionRequest.getRequestObj().getKendraDtls() == null) {

			logger.error("Invalid CollectionRequest: requestObj or kendraDtls is NULL");
			return;
		}

		logger.debug("Processing {} kendraDtls", collectionRequest.getRequestObj().getKendraDtls().size());

		for (CollectionsData kendraData : collectionRequest.getRequestObj().getKendraDtls()) {

			logger.debug("Processing kendraData={}", kendraData);

			if (kendraData == null || kendraData.getApplnDtls() == null) {
				logger.warn("Skipping kendraData={} (null or missing applnDtls)", kendraData);
				continue;
			}

			String branchId = kendraData.getBranchId();
			String kendraId = kendraData.getKendraId();
			LocalDate appDate = LocalDate.parse(kendraData.getMeetingDate());

			logger.debug("Extracted branchId={}, kendraId={}, appDate={}", branchId, kendraId, appDate);

			logger.debug("Checking existing notifications for branchId={}, fromUserId={}, kendraId={}, appDate={}",
					branchId, fromUserId, kendraId, appDate);

			List<TbAbnfTaskNotif> existingList = tbAbnfTaskNotifRepo
					.findByBranchIdAndFromUserIdAndKendraIdAndApplicationDate(branchId, fromUserId, kendraId, appDate);

			logger.debug("Fetched existingList size={} for kendraId={} and data is : {} ", existingList.size(), kendraId, existingList);

			if (!existingList.isEmpty()) {

				logger.debug("Updating updatedTs for {} existing records", existingList.size());

				existingList.forEach(notif -> {
					logger.trace("Updating notif kendraId ={} with new timestamp", notif.getKendraId());
					notif.setUpdatedTs(LocalDateTime.now());
				});

				tbAbnfTaskNotifRepo.saveAll(existingList);

				logger.debug("Updated {} notifications for branchId={}, kendraId={}", existingList.size(), branchId,
						kendraId);

			} else {

				logger.debug("No existing record found → creating new notification for kendraId={}", kendraId);

				TbAbnfTaskNotif notif = TbAbnfTaskNotif.builder().branchId(branchId).fromUserId(fromUserId).toUserId("")
						.kendraId(kendraId).fromUserRole(fromUserRole).applicationDate(appDate)
						.updatedTs(LocalDateTime.now()).toUserRole("CASHIER").task("Cashier Approval/Rejection Pending")
						.build();

				logger.debug("Prepared new notif={}", notif);

				toInsert.add(notif);
			}
		}

		if (!toInsert.isEmpty()) {
			logger.debug("Inserting {} new notifications", toInsert.size());
			tbAbnfTaskNotifRepo.saveAll(toInsert);
		} else {
			logger.debug("No new notifications to insert.");
		}

		logger.debug("Exiting saveNotifDetails() – Notification process complete.");
	}

	@Transactional(rollbackFor = Exception.class)
	public void saveMisData(CollectionRequest collectionRequest) {
		logger.debug("Collection request received: {}", collectionRequest);
		ObjectMapper objectMapper = new ObjectMapper();

		String usrRole = collectionRequest.getKmUserRole();
		if (collectionRequest == null || collectionRequest.getRequestObj() == null
				|| collectionRequest.getRequestObj().getKendraDtls() == null) {
			logger.warn("CollectionRequest or KendraDtls is null. Skipping saveMisData.");
			return;
		}

		List<CollectionReport> reportsToSave = new ArrayList<>();

		for (CollectionsData kendraData : collectionRequest.getRequestObj().getKendraDtls()) {
			if (kendraData == null || kendraData.getApplnDtls() == null) {
				logger.warn("Skipping null kendraData or missing application details.");
				continue;
			}

			try {
				int seqNum = 1;
				String status = "";
				String applnId = "";
				String createdBy = "";

				List<ApplicationWFDetails> aplnWfDtls = kendraData.getApplnWFDtls();
				if (aplnWfDtls != null && !aplnWfDtls.isEmpty()) {
					ApplicationWFDetails maxSeqObj = aplnWfDtls.stream().filter(Objects::nonNull)
							.max(Comparator.comparingInt(wf -> {
								try {
									return Integer.parseInt(wf.getSeqNo());
								} catch (NumberFormatException e) {
									return 0;
								}
							})).orElse(null);

					if (maxSeqObj != null && maxSeqObj.getSeqNo() != null) {
						try {
							seqNum = Integer.parseInt(maxSeqObj.getSeqNo());
							applnId = maxSeqObj.getApplnId();
							createdBy = maxSeqObj.getCreatedBy();
							String misStatus = maxSeqObj.getMisStatus();

							if (usrRole == null && misStatus == null) {
								status = "NA";
							} else {
								status = (usrRole != null ? usrRole : "NA") + " "
										+ (misStatus != null ? misStatus : "NA");
							}

							logger.debug("Derived status for Kendra ID {}: {}", kendraData.getKendraId(), status);
						} catch (NumberFormatException e) {
							logger.warn("Invalid seqNo '{}' for Kendra ID {}", maxSeqObj.getSeqNo(),
									kendraData.getKendraId());
							status = "INVALID_SEQ";
						}
					} else {
						status = "NO_WORKFLOW";
					}
				}
				
				logger.debug("Sequence num is : {}", seqNum);
				String applnType = getCollectionApplicationType(applnId);
				logger.debug("Application type for {}: {}", applnId, applnType);

				String payloadJson = objectMapper.writeValueAsString(kendraData);
				logger.debug("Payload JSON for Kendra ID {} prepared", kendraData.getKendraId());

				Optional<CollectionReport> existingOpt = collectionReportRepository
						.findFirstByApplicationIdAndKendraIdAndBranchIdOrderByCreatedTsAsc(
								kendraData.getApplnDtls().getApplnId(), kendraData.getKendraId(),
								kendraData.getBranchId());

				CollectionReport report;

				if (existingOpt.isPresent()) {
					CollectionReport existing = existingOpt.get();

					String originalCreatedBy = existing.getCreated_tsby() != null ? existing.getCreated_tsby()
							: createdBy;
					
					String userRole = getUserRole(createdBy);
					logger.debug("userRole is : {} " , userRole);
					report = CollectionReport.builder().applicationId(kendraData.getApplnDtls().getApplnId())
							.kendraId(kendraData.getKendraId()).branchId(kendraData.getBranchId()).payload(payloadJson)
							.createdTs(existingOpt.get().getCreatedTs()).updatedTs(LocalDateTime.now()).seqNo(seqNum).status(status)
							.collection_type(applnType).created_tsby(originalCreatedBy).updated_tsby(createdBy).user_role(userRole).build();

					logger.debug("Existing record → created_by={} updated_by={}", originalCreatedBy, createdBy);
				} else {
					String userRole = getUserRole(createdBy);
					logger.debug("userRole is : {} " , userRole);
					report = CollectionReport.builder().applicationId(kendraData.getApplnDtls().getApplnId())
							.kendraId(kendraData.getKendraId()).branchId(kendraData.getBranchId()).payload(payloadJson)
							.createdTs(LocalDateTime.now()).updatedTs(LocalDateTime.now()).seqNo(seqNum).status(status)
							.collection_type(applnType).created_tsby(createdBy).updated_tsby(createdBy).user_role(userRole).build();

					logger.debug("New record → created_by={} updated_by={}", createdBy, createdBy);
				}

				reportsToSave.add(report);

			} catch (JsonProcessingException e) {
				logger.debug("Failed to serialize KendraData for Kendra ID {}", kendraData.getKendraId(), e);
				throw new RuntimeException("JSON serialization error", e);
			} catch (Exception ex) {
				logger.debug("Failed to prepare CollectionReport for Kendra ID {}", kendraData.getKendraId(), ex);
				throw new RuntimeException("Preparing MIS data failed", ex);
			}
		}

		if (!reportsToSave.isEmpty()) {
			logger.debug("Saving {} CollectionReport records in bulk", reportsToSave.size());
			collectionReportRepository.saveAll(reportsToSave);
		}
	}

	private String getCollectionApplicationType(String applnId) {
		if (applnId == null || applnId.isBlank()) {
			logger.debug("Application ID is null or blank. Unable to determine collection type.");
			return "";
		}

		String applnType;
		if (applnId.startsWith("S")) {
			applnType = "S-COLLECTION";
		} else if (applnId.startsWith("R")) {
			applnType = "R-COLLECTION";
		} else if (applnId.startsWith("N")) {
			applnType = "N-K-COLLECTION";
		} else {
			logger.debug("Couldn't deduce the application type for ID: {}", applnId);
			applnType = "";
		}

		logger.debug("Determined application type [{}] for ID: {}", applnType, applnId);
		return applnType;
	}

	private String getApplicationType(String applnId) {
		String applnType = APPLN_TYPE;
		if (null != applnId && applnId.startsWith("R")) {
			applnType = REP_APPLN_TYPE;
		} else if (null != applnId && applnId.startsWith("N")) {
			applnType = NON_MTNG_APPLN_TYPE;
		}
		return applnType;
	}

	// fetch Application flow
	public Mono<Response> fetchApplication(FetchCollectionRequest collectionRequest, Header header) {
		Response response = new Response();
		ResponseHeader respHeader = new ResponseHeader();
		ResponseBody respBody = new ResponseBody();
		try {
			logger.debug("Inside fetchApplication request::{}", collectionRequest);
			String applnType = getApplicationType(collectionRequest.getRequestObj().getApplicationType());
			logger.debug("ApplnType request::{}", applnType);
			// Invoke call to fetch kendra Info
			Mono<Object> fetchKendraInfoMono = interfaceAdapter.callExternalService(header, collectionRequest,
					FETCH_KENDRA_INTF, true);
			logger.debug("fetchKendraInfoMono {}", fetchKendraInfoMono);

			return fetchKendraInfoMono.flatMap(val -> {
				logger.debug("fetchKendraInfoMono::{}", val);
				List<KendraDetailsDto> kendraList = this.getFetchKendraInfoResponse(val);
				logger.debug("kendraList::{}", kendraList);
				if (!kendraList.isEmpty()) {
					Map<String, List<KendraDetailsDto>> groupedKendraList = kendraList.stream()
							.filter(kendra -> kendra.getKmId() != null)
							.collect(Collectors.groupingBy(KendraDetailsDto::getKmId));

					logger.debug("GroupedKendraList::{}", groupedKendraList);
					if ("KM".equalsIgnoreCase(collectionRequest.getRequestObj().getKmUserRole())
							&& collectionRequest.getRequestObj().getKendraId() != null
							&& !collectionRequest.getRequestObj().getKendraId().isEmpty()) {

						String curKmId = collectionRequest.getUserId();
						String userRole = collectionRequest.getRequestObj().getKmUserRole();
						String meetingDate = collectionRequest.getRequestObj().getMeetingDate();
						String kendraId = collectionRequest.getRequestObj().getKendraId();

						List<String> kendraIds = Arrays.asList(kendraId.split("~"));
						logger.debug("KendraIds for KM : {}", kendraIds);

						List<ApplicationMaster> aplDataKm = applicationMasterRepository
								.findApplicationsByKendraAndDate(kendraIds, LocalDate.parse(meetingDate));

						logger.debug("aplDataKm for KM : {}", aplDataKm);

						for (ApplicationMaster am : aplDataKm) {

							String oldKmId = am.getKmId();

							if (!curKmId.equals(oldKmId)) {

								String addInfo = userRole + "~" + oldKmId;
								am.setAddInfo(addInfo);

								am.setKmId(curKmId);
								am.setCreatedBy(curKmId);
							}

							logger.debug("Data obj to be saved : {}", am);
							applicationMasterRepository.save(am);
						}
					}

					if ("CASHIER".equalsIgnoreCase(collectionRequest.getRequestObj().getKmUserRole())
							|| "BM".equalsIgnoreCase(collectionRequest.getRequestObj().getKmUserRole())) {

						List<ApplicationMaster> applnMasterList;

						if (NON_MTNG_APPLN_TYPE.equalsIgnoreCase(applnType)) {
							applnMasterList = applicationMasterRepository
									.findByBranchCodeAndApplicationTypeOrderByVersionNumDesc(
											collectionRequest.getRequestObj().getBranchId(), applnType);
						} else {
							LocalDate lcStartDt = LocalDate.parse(collectionRequest.getRequestObj().getMeetingDate());
							logger.debug("lcStartDt::{}", lcStartDt);

							applnMasterList = applicationMasterRepository
									.findByBranchCodeAndApplicationDateAndApplicationTypeOrderByVersionNumDesc(
											collectionRequest.getRequestObj().getBranchId(), lcStartDt, applnType);
						}

						logger.debug("Fetched {} records for Branch {} before filtering latest versions.",
								applnMasterList.size(), collectionRequest.getRequestObj().getBranchId());

						Map<String, ApplicationMaster> latestByAppId = applnMasterList.stream().collect(
								Collectors.toMap(ApplicationMaster::getApplicationId, app -> app, (app1, app2) -> {
									int v1 = Integer.parseInt(app1.getVersionNum());
									int v2 = Integer.parseInt(app2.getVersionNum());

									ApplicationMaster latest = v1 >= v2 ? app1 : app2;

									logger.debug(
											"Duplicate found for ApplicationID {} -> Keeping version {}, Ignoring version {}",
											app1.getApplicationId(), latest.getVersionNum(),
											(latest == app1 ? app2.getVersionNum() : app1.getVersionNum()));

									return latest;
								}));

						List<ApplicationMaster> filteredList = new ArrayList<>(latestByAppId.values());

						logger.debug("After filtering, {} records remain for Branch {} (latest versions only).",
								filteredList.size(), collectionRequest.getRequestObj().getBranchId());

						logger.debug("Final chosen application versions for Branch {} -> {}",
								collectionRequest.getRequestObj().getBranchId(),
								filteredList
										.stream().map(app -> String.format("AppID:%s Version:%s",
												app.getApplicationId(), app.getVersionNum()))
										.collect(Collectors.joining(", ")));

						for (ApplicationMaster app : filteredList) {
							String kmId = app.getKmId();

							if (!groupedKendraList.containsKey(kmId)) {
								List<KendraDetailsDto> kendraDtoList = filteredList.stream()
										.filter(a -> kmId.equals(a.getKmId())).map(a -> {
											KendraDetailsDto dto = new KendraDetailsDto();
											dto.setKendraId(Integer.parseInt(a.getKendraId()));
											dto.setKendraName(a.getKendraName());
											dto.setKmId(a.getKmId());
											dto.setKmName(a.getCreatedBy());
											return dto;
										}).collect(Collectors.toList());

								groupedKendraList.put(kmId, kendraDtoList);
								logger.debug("Patched groupedKendraList with KMID {}: {}", kmId, kendraDtoList);
							} else {
								logger.debug("KMID {} already exists in groupedKendraList, skipping", kmId);
							}
						}
						logger.debug("updated groupedKendraList for cashier/bm is : {} ", groupedKendraList);
					}
					
					if ("KM".equalsIgnoreCase(collectionRequest.getRequestObj().getKmUserRole())) {
						String kmId = collectionRequest.getUserId();

						if (!groupedKendraList.containsKey(kmId)) {
							List<ApplicationMaster> applnMasterListForKm;

							if (NON_MTNG_APPLN_TYPE.equalsIgnoreCase(applnType)) {
								applnMasterListForKm = applicationMasterRepository
										.findByBranchCodeAndApplicationTypeAndKmIdOrderByVersionNumDesc(
												collectionRequest.getRequestObj().getBranchId(), applnType, kmId);
							} else {
								LocalDate lcStartDt = LocalDate
										.parse(collectionRequest.getRequestObj().getMeetingDate());
								applnMasterListForKm = applicationMasterRepository
										.findByBranchCodeAndApplicationDateAndApplicationTypeAndKmIdOrderByVersionNumDesc(
												collectionRequest.getRequestObj().getBranchId(), lcStartDt, applnType,
												kmId);
							}

							logger.debug("Fetched {} records for KMID {} before filtering latest versions.",
									applnMasterListForKm.size(), kmId);

							Map<String, ApplicationMaster> latestByAppId = applnMasterListForKm.stream().collect(
									Collectors.toMap(ApplicationMaster::getApplicationId, app -> app, (app1, app2) -> {
										int v1 = Integer.parseInt(app1.getVersionNum());
										int v2 = Integer.parseInt(app2.getVersionNum());

										ApplicationMaster latest = v1 >= v2 ? app1 : app2;

										logger.debug(
												"Duplicate found for ApplicationID {} -> Keeping version {}, Ignoring version {}",
												app1.getApplicationId(), latest.getVersionNum(),
												(latest == app1 ? app2.getVersionNum() : app1.getVersionNum()));

										return latest;
									}));

							List<ApplicationMaster> filteredList = new ArrayList<>(latestByAppId.values());

							logger.debug("After filtering, {} records remain for KMID {} (latest versions only).",
									filteredList.size(), kmId);

							logger.debug("Final chosen application versions for KMID {} -> {}", kmId,
									filteredList
											.stream().map(app -> String.format("AppID:%s Version:%s",
													app.getApplicationId(), app.getVersionNum()))
											.collect(Collectors.joining(", ")));

							if (!filteredList.isEmpty()) {
								List<KendraDetailsDto> dtoList = filteredList.stream().map(app -> {
									KendraDetailsDto dto = new KendraDetailsDto();
									dto.setKendraId(Integer.parseInt(app.getKendraId()));
									dto.setKendraName(app.getKendraName());
									dto.setKmId(app.getKmId());
									dto.setKmName(app.getCreatedBy());
									return dto;
								}).collect(Collectors.toList());

								groupedKendraList.put(kmId, dtoList);
								logger.debug("Patched groupedKendraList with KMID {}: {}", kmId, dtoList);
							} else {
								logger.debug("No application master records found for KMID {} after filtering.", kmId);
							}
						} else {
							logger.debug("KMID {} already exists in groupedKendraList. Skipping patch.", kmId);
						}
						logger.debug("updated groupedKendraList for km is : {} ", groupedKendraList);

					}

					// Fetch distinct kmIds from kendraList
					List<String> distinctKmIds = new ArrayList<>(groupedKendraList.keySet());
					logger.debug("distinctKmIds::{}", distinctKmIds);
					List<ApplicationMaster> applnMasterList;
					logger.debug("applnType::{}", applnType);
					if (NON_MTNG_APPLN_TYPE.equalsIgnoreCase(applnType)) {
						applnMasterList = applicationMasterRepository
								.findByBranchCodeAndApplicationTypeAndKmIdInOrderByVersionNumDesc(
										collectionRequest.getRequestObj().getBranchId(), applnType, distinctKmIds);
					} else {
						LocalDate lcStartDt = LocalDate.parse(collectionRequest.getRequestObj().getMeetingDate());
						logger.debug("lcStartDt::{}", lcStartDt);
						applnMasterList = applicationMasterRepository
								.findByBranchCodeAndApplicationDateAndApplicationTypeAndKmIdInOrderByVersionNumDesc(
										collectionRequest.getRequestObj().getBranchId(), lcStartDt, applnType,
										distinctKmIds);
					}
					logger.debug("applnMasterList in fetchApplication::{}", applnMasterList);
					// KM - DEO role fix
					if ("KM".equalsIgnoreCase(collectionRequest.getRequestObj().getKmUserRole())) {

						applnMasterList = applnMasterList.stream()
								.filter(app -> app.getAddInfo() == null
										|| !app.getAddInfo().contains("DEO"))
								.collect(Collectors.toList());

						logger.debug("After DEO exclusion for KM, applnMasterList size :: {}",
								applnMasterList.size());
					}
					if ("KM".equalsIgnoreCase(collectionRequest.getRequestObj().getKmUserRole())) {
						Map<String, List<KendraDetailsDto>> kmKendraMap = applnMasterList.stream().collect(Collectors
								.toMap(ApplicationMaster::getKendraId, app -> app, (existing, replacement) -> {
									int v1 = Integer.parseInt(existing.getVersionNum());
									int v2 = Integer.parseInt(replacement.getVersionNum());
									return v2 > v1 ? replacement : existing;
								})).values().stream()
								.collect(Collectors.groupingBy(ApplicationMaster::getKmId, Collectors.mapping(app -> {
									KendraDetailsDto dto = new KendraDetailsDto();
									dto.setKendraId(Integer.parseInt(app.getKendraId()));
									dto.setKendraName(app.getKendraName());
									dto.setKmId(app.getKmId());
									dto.setKmName(app.getCreatedBy());
									return dto;
								}, Collectors.toList())));

						logger.debug("===== kmKendraMap (deduped by versionNum) =====");
						kmKendraMap.forEach((kmId, dtoList) -> logger.debug("KMID {} -> {}", kmId,
								dtoList.stream().map(KendraDetailsDto::getKendraId).collect(Collectors.toList())));

						kmKendraMap.forEach((kmId, correctDtoList) -> {
							List<KendraDetailsDto> existingList = groupedKendraList.getOrDefault(kmId,
									new ArrayList<>());

							Set<Integer> correctKendraIds = correctDtoList.stream().map(KendraDetailsDto::getKendraId)
									.collect(Collectors.toSet());

							List<String> movedLogs = new ArrayList<>();
							groupedKendraList.forEach((otherKmId, list) -> {
								if (!otherKmId.equals(kmId)) {
									List<Integer> toRemove = list.stream().map(KendraDetailsDto::getKendraId)
											.filter(correctKendraIds::contains).collect(Collectors.toList());

									if (!toRemove.isEmpty()) {
										list.removeIf(dto -> correctKendraIds.contains(dto.getKendraId()));
										movedLogs.add("Moved Kendras " + toRemove + " from KMID " + otherKmId
												+ " -> KMID " + kmId);
									}
								}
							});

							Set<Integer> existingKendraIds = existingList.stream().map(KendraDetailsDto::getKendraId)
									.collect(Collectors.toSet());

							List<KendraDetailsDto> newOnes = correctDtoList.stream()
									.filter(dto -> !existingKendraIds.contains(dto.getKendraId()))
									.collect(Collectors.toList());

							if (!newOnes.isEmpty()) {
								existingList.addAll(newOnes);
								groupedKendraList.put(kmId, existingList);
								movedLogs.add("Added new Kendras " + newOnes.stream().map(KendraDetailsDto::getKendraId)
										.collect(Collectors.toList()) + " to KMID " + kmId);
							}

							if (movedLogs.isEmpty()) {
								logger.debug("KMID {} -> No changes (already correct)", kmId);
							} else {
								movedLogs.forEach(logger::debug);
							}
						});

					}
					if ("CASHIER".equalsIgnoreCase(collectionRequest.getRequestObj().getKmUserRole())
							|| "BM".equalsIgnoreCase(collectionRequest.getRequestObj().getKmUserRole())) {

						Map<String, List<KendraDetailsDto>> deoKendraMap = applnMasterList.stream()
								.filter(app -> app.getAddInfo() != null && app.getAddInfo().contains("DEO"))
								.collect(Collectors.toMap(ApplicationMaster::getKendraId, app -> app,
										(existing, replacement) -> {
											int v1 = Integer.parseInt(existing.getVersionNum());
											int v2 = Integer.parseInt(replacement.getVersionNum());
											return v2 > v1 ? replacement : existing;
										}))
								.values().stream()
								.collect(Collectors.groupingBy(ApplicationMaster::getKmId, Collectors.mapping(app -> {
									KendraDetailsDto dto = new KendraDetailsDto();
									dto.setKendraId(Integer.parseInt(app.getKendraId()));
									dto.setKendraName(app.getKendraName());
									dto.setKmId(app.getKmId());
									dto.setKmName(app.getCreatedBy());
									return dto;
								}, Collectors.toList())));

						logger.debug("===== cashier/bm KendraMap =====");
						logger.debug("cashier/bm deoKendraMap::{}", deoKendraMap);

						deoKendraMap.forEach((kmId, deoList) -> {
							List<KendraDetailsDto> existingList = groupedKendraList.getOrDefault(kmId,
									new ArrayList<>());

							Set<Integer> existingKendraIds = existingList.stream().map(KendraDetailsDto::getKendraId)
									.collect(Collectors.toSet());

							List<KendraDetailsDto> newOnes = deoList.stream()
									.filter(dto -> !existingKendraIds.contains(dto.getKendraId()))
									.collect(Collectors.toList());
							if (!newOnes.isEmpty()) {
								existingList.addAll(newOnes);
								groupedKendraList.put(kmId, existingList);
							} else {
								logger.debug("No new Kendras found for KMID {}", kmId);
							}
						});

						logger.debug("===== cashier/bm groupedKendraList after DEO processing =====");
						logger.debug("cashier/bm groupedKendraList updated ::{}", groupedKendraList);

						applnMasterList.forEach(app -> {
							int kendraId = Integer.parseInt(app.getKendraId());
							String correctKmId = app.getKmId();

							groupedKendraList.forEach((kmId, list) -> {
								if (!kmId.equals(correctKmId)) {
									boolean removed = list.removeIf(dto -> dto.getKendraId() == kendraId);
									if (removed) {
										logger.debug("Corrected: Removed Kendra {} from KMID {}", kendraId, kmId);
									}
								}
							});

							List<KendraDetailsDto> correctList = groupedKendraList.getOrDefault(correctKmId,
									new ArrayList<>());
							boolean alreadyPresent = correctList.stream()
									.anyMatch(dto -> dto.getKendraId() == kendraId);

							if (!alreadyPresent) {
								KendraDetailsDto dto = new KendraDetailsDto();
								dto.setKendraId(kendraId);
								dto.setKendraName(app.getKendraName());
								dto.setKmId(correctKmId);
								dto.setKmName(app.getCreatedBy());

								correctList.add(dto);
								groupedKendraList.put(correctKmId, correctList);

								logger.debug("Corrected: Added Kendra {} under KMID {}", kendraId, correctKmId);
							}
						});

						logger.debug("===== cashier/bm final corrected groupedKendraList =====");
						logger.debug("Final groupedKendraList ::{}", groupedKendraList);
					}

					if ("DEO".equalsIgnoreCase(collectionRequest.getRequestObj().getKmUserRole())) {
						Map<String, List<KendraDetailsDto>> deoKendraMap = applnMasterList.stream()
								.filter(app -> app.getAddInfo() != null && app.getAddInfo().contains("DEO")
										&& !app.getAddInfo().contains("KM"))
								.collect(Collectors.toMap(ApplicationMaster::getKendraId, app -> app,
										(existing, replacement) -> {
											int v1 = Integer.parseInt(existing.getVersionNum());
											int v2 = Integer.parseInt(replacement.getVersionNum());
											return v2 > v1 ? replacement : existing;
										}))
								.values().stream()
								.collect(Collectors.groupingBy(ApplicationMaster::getKmId, Collectors.mapping(app -> {
									KendraDetailsDto dto = new KendraDetailsDto();
									dto.setKendraId(Integer.parseInt(app.getKendraId()));
									dto.setKendraName(app.getKendraName());
									dto.setKmId(app.getKmId());
									dto.setKmName(app.getCreatedBy());
									return dto;
								}, Collectors.toList())));

						logger.debug("===== deoKendraMap =====");
						logger.debug("deoKendraMap::{}", deoKendraMap);

						deoKendraMap.forEach((kmId, deoList) -> {

							List<KendraDetailsDto> existingList = groupedKendraList.getOrDefault(kmId,
									new ArrayList<>());

							Set<Integer> existingKendraIds = existingList.stream().map(KendraDetailsDto::getKendraId)
									.collect(Collectors.toSet());

							List<KendraDetailsDto> newOnes = deoList.stream()
									.filter(dto -> !existingKendraIds.contains(dto.getKendraId()))
									.collect(Collectors.toList());
							if (!newOnes.isEmpty()) {
								existingList.addAll(newOnes);
								groupedKendraList.put(kmId, existingList);
							} else {
								logger.debug("No new Kendras found for KMID {}", kmId);
							}
						});
						logger.debug("===== deo updated  groupedKendraList=====");
						logger.debug("groupedKendraList updated for deo ::{}", groupedKendraList);
					}

					Map<String, Map<String, List<ApplicationMaster>>> applnMasterMap = applnMasterList.stream()
							.collect(Collectors.groupingBy(ApplicationMaster::getKmId,
									Collectors.groupingBy(ApplicationMaster::getKendraId, Collectors.collectingAndThen(
											Collectors.toMap(ApplicationMaster::getApplicationId, Function.identity(),
													(a, b) -> Integer.parseInt(b.getVersionNum()) > Integer
															.parseInt(a.getVersionNum()) ? b : a),
											m -> new ArrayList<>(m.values())))));

					logger.debug("applnMasterMap in case of DEO/KM::{}", applnMasterMap);

					List<FetchCollectionResponse> collectionResp = new ArrayList<>();
					groupedKendraList.forEach((kmId, kendraDetailsList) -> {
						logger.debug("kmId>>>>::{}", kmId);
						logger.debug("kendraDetailsList<<<<>>::{}", kendraDetailsList);
						List<FetchCollectionKendraDtls> fetchCollectionKendraDtls = new ArrayList<>();

						kendraDetailsList.forEach(kendraDetails -> {
							logger.debug("kendraDetails<<<<++>>::{}", kendraDetails);
							List<CashDepositPoints> cashDepositPointsLst = new ArrayList<>();

							/*
							 * ApplicationMaster applnMst = Optional.ofNullable(applnMasterMap.get(kmId))
							 * .map(kendraMap ->
							 * kendraMap.get(String.valueOf(kendraDetails.getKendraId()).trim()))
							 * .orElse(null);
							 */

							logger.debug("kmId::{}", kmId);

							logger.debug("distinct kmId ::{}", kmId);
							List<ApplicationMaster> applnMstList = Optional.ofNullable(applnMasterMap.get(kmId))
									.map(kendraMap -> kendraMap.get(String.valueOf(kendraDetails.getKendraId()).trim()))
									.orElse(Collections.emptyList());

							logger.debug("applnMst::{}", applnMstList);

							logger.debug("applnMst for kendraId:{} is ::{}", kendraDetails.getKendraId(), applnMstList);
							for (ApplicationMaster applnMst : applnMstList) {
								fetchApplicationMasterAndOtherDtls(applnMst, kendraDetails, fetchCollectionKendraDtls,
										cashDepositPointsLst);
							}

						});
						logger.debug("fetchCollectionKendraDtls", fetchCollectionKendraDtls);
						logger.debug("kendraDetailsList", kendraDetailsList);
						logger.debug("kmid", kmId);
						logger.debug("collectionResp", collectionResp);
						generateCollectionDetails(fetchCollectionKendraDtls, kendraDetailsList, kmId, collectionResp);
					});
					String respStr = "";
					try {
						FetchCollectionResponseWrapper fetchCollectionResponseWrapper = FetchCollectionResponseWrapper
								.builder().collectionData(collectionResp).build();
						logger.debug("fetchCollectionResponseWrapper::{}", fetchCollectionResponseWrapper);
						respStr = new ObjectMapper().writeValueAsString(fetchCollectionResponseWrapper);
						logger.debug("respStr", respStr);
					} catch (Exception ex) {
						logger.error(CommonConstants.EXCEP_OCCURED, ex);
					}
					logger.debug("Final Collection Resp::{}", respStr);
					respBody.setResponseObj(respStr);
					CommonUtils.generateHeaderForSuccess(respHeader);
				} else {
					respBody.setResponseObj("");
					CommonUtils.generateHeaderForNoResult(respHeader);
				}
				response.setResponseBody(respBody);
				response.setResponseHeader(respHeader);
				return Mono.just(response);
			});
		} catch (Exception e) {
			logger.error("exception at createApplication: ", e);
			respBody.setResponseObj("");
			CommonUtils.generateHeaderForFailure(respHeader, CommonConstants.EXCEPTION_MSG);
			response.setResponseBody(respBody);
			response.setResponseHeader(respHeader);
			return Mono.just(response);
		}
	}

	private void fetchApplicationMasterAndOtherDtls(ApplicationMaster applnMst, KendraDetailsDto kendraDetails,
			List<FetchCollectionKendraDtls> fetchCollectionKendraDtls, List<CashDepositPoints> cashDepositPointsLst) {
		logger.debug("Final applnMst::{}", applnMst);
		logger.debug("Final kendraDetails::{}", kendraDetails);
		logger.debug("Final fetchCollectionKendraDtls::{}", fetchCollectionKendraDtls);
		logger.debug("Final cashDepositPointsLst::{}", cashDepositPointsLst);
		if (applnMst != null) {
			ApplicationDetails applnDetails = getApplicationDetailsResponse(applnMst);
			logger.debug("Final applnDetails::{}", applnDetails);
			// Fetch CashDeposit Data based on the applicationMaster records.
			logger.debug("cashDepositPointsLst::{}", cashDepositPointsLst);
			logger.debug("applnDetails{}", applnDetails);
			fetchCashDepositDtls(cashDepositPointsLst, applnDetails);
			// Fetch TbUacoKendraDtls

			logger.debug("applnMst::{}", applnMst);
			logger.debug("kendraDetails{}", kendraDetails);
			logger.debug("applnDetails::{}", applnDetails);
			logger.debug("fetchCollectionKendraDtls{}", fetchCollectionKendraDtls);
			logger.debug("cashDepositPointsLst{}", cashDepositPointsLst);
			getKendraDtlsInfo(applnMst, kendraDetails, applnDetails, fetchCollectionKendraDtls, cashDepositPointsLst);
		}
	}

	private void generateCollectionDetails(List<FetchCollectionKendraDtls> fetchCollectionKendraDtls,
			List<KendraDetailsDto> kendraDetailsList, String kmId, List<FetchCollectionResponse> collectionResp) {

		logger.debug("Final fetchCollectionKendraDtls::{}", fetchCollectionKendraDtls);
		logger.debug("Final kendraDetailsList::{}", kendraDetailsList);
		logger.debug("Final kmId::{}", kmId);
		logger.debug("Final collectionResp::{}", collectionResp);
		if (fetchCollectionKendraDtls == null) {
		    fetchCollectionKendraDtls = new ArrayList<>();
		}

		if (kendraDetailsList == null) {
		    kendraDetailsList = new ArrayList<>();
		}

		int totalKendraColAmt = fetchCollectionKendraDtls.stream().mapToInt(k -> k.getColldtls().getTotCollAmt()).sum();
		int totalKendraDueAmt = fetchCollectionKendraDtls.stream().mapToInt(k -> k.getColldtls().getTotalDue()).sum();
		int totalKendraNetDueAmt = fetchCollectionKendraDtls.stream().mapToInt(k -> k.getColldtls().getNetDue()).sum();
		int totalKendraAdvAmt = fetchCollectionKendraDtls.stream().mapToInt(k -> k.getColldtls().getTotalAdv()).sum();
		int totalKendraCashAmt = fetchCollectionKendraDtls.stream()
				.flatMap(kendraDtls -> kendraDtls.getColldtls().getCollectionGroupDtls().stream())
				.flatMap(groupDtls -> groupDtls.getMembers().stream()).mapToInt(CollectionMemberDtls::getTotalCash)
				.sum();

		int totalMemberAdvAmt = fetchCollectionKendraDtls.stream()
				.flatMap(kendraDtls -> kendraDtls.getColldtls().getCollectionGroupDtls().stream())
				.flatMap(groupDtls -> groupDtls.getMembers().stream()).mapToInt(CollectionMemberDtls::getAdvAmt)
				.sum();

		int totalMemberAdvAdjAmt = fetchCollectionKendraDtls.stream()
				.flatMap(kendraDtls -> kendraDtls.getColldtls().getCollectionGroupDtls().stream())
				.flatMap(groupDtls -> groupDtls.getMembers().stream()).mapToInt(CollectionMemberDtls::getAdvAdj)
				.sum();

		int totalKendraUPIAmt = fetchCollectionKendraDtls.stream()
				.flatMap(kendraDtls -> kendraDtls.getColldtls().getCollectionGroupDtls().stream())
				.flatMap(groupDtls -> groupDtls.getMembers().stream()).mapToInt(CollectionMemberDtls::getTotalUPI)
				.sum();

		CollectionAmountDtls actualColAmtDtls = CollectionAmountDtls.builder().total(totalKendraColAmt)
				.cashCol(totalKendraCashAmt) // SUM of all toatalCollAmt from colDtls for all kendra's.
				.upiCol(totalKendraUPIAmt).advAdj(totalMemberAdvAdjAmt).build();
		CollectionAmountDtls advColAmtDtls = CollectionAmountDtls.builder().total(totalMemberAdvAmt)
				.cashCol(totalMemberAdvAmt).upiCol(0).build();

		int totalOtherColAmt = 0;
		CollectionAmountDtls othColAmtDtls = CollectionAmountDtls.builder().total(totalOtherColAmt).cashCol(0).upiCol(0)
				.build();
		int totalUpFrontCharges = 0;
		CollectionInflowDtls inflowDtls = CollectionInflowDtls.builder().actualCol(actualColAmtDtls)
				.advCol(advColAmtDtls).othCol(othColAmtDtls).upfrontChgs(totalUpFrontCharges).build();
		int totalOutflow = 0;
		CollectionOutflowDtls outflowDtls = CollectionOutflowDtls.builder().total(totalOutflow).build();

		//int kendraCollWithoutAdv = totalKendraColAmt - totalMemberAdvAmt;
		//int totalInflow = kendraCollWithoutAdv + totalKendraAdvAmt + totalOtherColAmt + totalUpFrontCharges;
		FetchCollectionInfo fetchCollectionInfo = FetchCollectionInfo.builder().totalDue(totalKendraDueAmt)
				.netDue(totalKendraNetDueAmt)// Sum netDue for all kendra's
				.opnAdvBal(totalMemberAdvAdjAmt).inflow(inflowDtls).outflow(outflowDtls)
				.netCol(totalKendraCashAmt + totalKendraUPIAmt + totalOutflow).clsAdv(totalKendraAdvAmt).build();
		logger.debug("Final fetchCollectionInfo::{}", fetchCollectionInfo);
		/**
		 * Code change added to check if all the records of Application Master are
		 * approved, Then set the status as 'APPROVED'
		 */
		boolean allApproved = fetchCollectionKendraDtls.stream()
				.allMatch(kendraDtls -> "APPROVED".equals(kendraDtls.getApplnDtls().getAppStatus()));
		logger.debug("Final allApproved::{}", allApproved);

		String colStatus = "APPROVAL PENDING";
		if (allApproved) {
			colStatus = "APPROVED BY BM";
		}
		String kmName = (!kendraDetailsList.isEmpty() && kendraDetailsList.get(0).getKmName() != null)
		        ? kendraDetailsList.get(0).getKmName()
		        : "";
		FetchCollectionResponse fetchColResp = FetchCollectionResponse.builder().kmId(kmId)
				.kmName(kmName).kendraCount(kendraDetailsList.size())
				.totalColAmt(totalKendraColAmt) // Sum total collection for all kendra's
				.collInfo(fetchCollectionInfo).kendraDtls(fetchCollectionKendraDtls).collStatus(colStatus).build();
		logger.debug("Final fetchColResp::{}", fetchColResp);
		collectionResp.add(fetchColResp);
		logger.debug("Final11 collectionResp::{}", collectionResp);
	}

	/**
	 * Method to fetch the kendraDtls Info node.,
	 *
	 * @param applnMst
	 * @param kendraDetails
	 * @param applnDetails
	 * @param fetchCollectionKendraDtls
	 * @param cashDepositPointsLst
	 */
	private void getKendraDtlsInfo(ApplicationMaster applnMst, KendraDetailsDto kendraDetails,
			ApplicationDetails applnDetails, List<FetchCollectionKendraDtls> fetchCollectionKendraDtls,
			List<CashDepositPoints> cashDepositPointsLst) {

		logger.debug("applnMst::{}", applnMst);
		logger.debug("kendraDetails::{}", kendraDetails);
		logger.debug("applnDetails::{}", applnDetails);
		logger.debug("fetchCollectionKendraDtls::{}", fetchCollectionKendraDtls);
		logger.debug("cashDepositPointsLst::{}", cashDepositPointsLst);

		// Fetch ApplicationWorkflow and details
		List<ApplicationWorkflow> applnWFLst = applicationWorkflowRepository
				.findByAppIdAndApplicationIdOrderByWorkflowSeqNumDesc(applnMst.getAppId(), applnMst.getApplicationId());
		logger.debug("applnWFLst::{}", applnWFLst);
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern(CollectionUtil.DT_TIME_FORMAT_TZ);
		List<ApplicationWFDetails> applnWFDtlsLst = applnWFLst.stream()
				.map(workflow -> ApplicationWFDetails.builder().appId(workflow.getAppId())
						.applnId(workflow.getApplicationId()).verNo(String.valueOf(workflow.getVersionNum()))
						.seqNo(String.valueOf(workflow.getWorkflowSeqNum())).createdBy(workflow.getCreatedBy())
						.kmUserName(workflow.getCreatedUserName())
						.appStatus(workflow.getApplicationStatus())
						.createTs(workflow.getCreateTs() != null ? workflow.getCreateTs().format(formatter)
								: LocalDateTime.now().format(formatter))
						.usrRole(workflow.getCurrentRole()).nextWFStage(workflow.getNextWorkFlowStage())
						.remarks(workflow.getRemarks()).build())
				.toList();
		 logger.debug("applnWFDtlsLst::{}", applnWFDtlsLst);
		tbUacoKendraDtlsRepository
				.findTopByAppIdAndApplicationIdAndKendraIdOrderByVersionNumDesc(applnMst.getAppId(),
						applnMst.getApplicationId(), String.valueOf(kendraDetails.getKendraId()))
				.ifPresent(tbUacoKendraDtls -> {
					String payload = tbUacoKendraDtls.getPayload();
					JSONObject payloadJSON = new JSONObject(payload);

					// Fetch Customer Details
					List<TbUaobCustDtls> custDtlsList = tbUaobCustomerDtlsRepo
							.findByAppAndApplicationAndKendraOrderedByMemberPos(applnMst.getAppId(),
									applnMst.getApplicationId(), applnMst.getKendraId(), applnMst.getVersionNum());

					logger.debug("custDtlsList::{}", custDtlsList);
					List<CollectionGroupDtls> collectionGroupDtlsList = custDtlsList.stream()
							.map(custDtls -> new Gson().fromJson(custDtls.getPayload(), CollectionGroupDtls.class))
							.toList();
					logger.debug("collectionGroupDtlsList::{}", collectionGroupDtlsList);

					CollectionDtls collectionDtls = CollectionDtls.builder()
							.collId(CollectionUtil.getStringValue(payloadJSON, "collId"))
							.collDate(CollectionUtil.getStringValue(payloadJSON, "colDate"))
							.totCollAmt(CollectionUtil.getIntValue(payloadJSON, "totCollAmt"))
							.totalDue(CollectionUtil.getIntValue(payloadJSON, "totalDue"))
							.netDue(CollectionUtil.getIntValue(payloadJSON, "netDue"))
							.totalAdv(CollectionUtil.getIntValue(payloadJSON, "totalAdv"))
							.collectionGroupDtls(collectionGroupDtlsList).build();
					logger.debug("Fetch collectionDtls::{}", collectionDtls);

					// Build FetchCollectionKendraDtls
					FetchCollectionKendraDtls kendraInfo = FetchCollectionKendraDtls.builder()
							.id(String.valueOf(kendraDetails.getKendraId())).name(kendraDetails.getKendraName())
							.startTime(tbUacoKendraDtls.getStartTime())
							.meetingDate(CollectionUtil.getStringValue(payloadJSON, "meetingDate"))
							.meetingDay(CollectionUtil.getStringValue(payloadJSON, "meetingDay"))
							.lat(CollectionUtil.getStringValue(payloadJSON, "lat"))
							.type(CollectionUtil.getStringValue(payloadJSON, "type"))
							.longitude(CollectionUtil.getStringValue(payloadJSON, "long")).colldtls(collectionDtls)
							.branchId(applnMst.getBranchCode()).applnDtls(applnDetails).applnWFDtls(applnWFDtlsLst)
							.cashDepositPoints(cashDepositPointsLst).build();
					logger.debug("Fetch Collection Kendra Dtls::{}", kendraInfo);
					fetchCollectionKendraDtls.add(kendraInfo);
					logger.debug("fetchCollectionKendraDtls::{}", fetchCollectionKendraDtls);
				});
	}

	/**
	 * Method to generate ApplicationDetails
	 *
	 * @param applnMst
	 * @return
	 */
	private ApplicationDetails getApplicationDetailsResponse(ApplicationMaster applnMst) {
		String formattedDate = applnMst.getApplicationDate().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
		String createTsStr = null;
		if (null != applnMst.getCreateTs()) {
			createTsStr = new SimpleDateFormat(CollectionUtil.DT_TIME_FORMAT).format(applnMst.getCreateTs());
		}
		return ApplicationDetails.builder().appId(applnMst.getAppId()).applnId(applnMst.getApplicationId())
				.verNo(applnMst.getVersionNum()).kendraId(applnMst.getKendraId()).brnCode(applnMst.getBranchCode())
				.applnDate(formattedDate).createTs(createTsStr).createdBy(applnMst.getCreatedBy())
				.appType(applnMst.getApplicationType()).appStatus(applnMst.getApplicationStatus())
				.currStage(applnMst.getCurrentStage()).kmId(applnMst.getKmId()).leader(applnMst.getLeader())
				.kendraName(applnMst.getKendraName()).amount(applnMst.getAmount().toString())
				.refNo(applnMst.getApplicationRefNo()).build();
	}

	/**
	 * Method validate the response of Kendra Info.
	 *
	 * @param val
	 * @return
	 */
	private List<KendraDetailsDto> getFetchKendraInfoResponse(Object val) {
		List<KendraDetailsDto> kendraList = new ArrayList<>();
		try {
			if (val instanceof HashMap<?, ?> || val instanceof ArrayList<?>) {
				String extApiResp = new ObjectMapper().writeValueAsString(val);
				if (extApiResp.startsWith("{") && extApiResp.endsWith("}")) {
					JSONObject extRespJSON = new JSONObject(extApiResp);
					if (com.iexceed.appzillonbanking.core.constants.CommonConstants.SUCCESS
							.equalsIgnoreCase(extRespJSON.getJSONObject(CommonConstants.API_RESPONSE)
									.getJSONObject(CommonConstants.RESP_HEADER).getString(CommonConstants.RESP_CODE))) {
						String responseStr = extRespJSON.getJSONObject(CommonConstants.API_RESPONSE)
								.getJSONObject(CommonConstants.RESP_BODY).getString(CommonConstants.RESP_OBJ);

						KendraDetailsDto[] arr = new ObjectMapper().readValue(responseStr, KendraDetailsDto[].class);
						kendraList = Arrays.asList(arr);
					}
				}
			}
		} catch (Exception e) {
			logger.error(CommonConstants.EXCEP_OCCURED, e);
		}
		logger.debug("getFetchKendraInfoResponse::{}", kendraList);
		return kendraList;
	}

	/**
	 * Method to store CashDepositDetails in TbUacoDepositPointsDtls table
	 *
	 * @param collectionRequest
	 */
	private void storeCashDepositDtls(CollectionRequest collectionRequest) {
		if (null != collectionRequest.getRequestObj().getCashDepositPoints()
				&& !collectionRequest.getRequestObj().getCashDepositPoints().isEmpty()) {
			List<TbUacoDepositPointsDtls> tbUacoDepositPointsDtlsLst = new ArrayList<>();
			for (CashDepositPoints cashDepositPoints : collectionRequest.getRequestObj().getCashDepositPoints()) {
				String uploadedFilePath = uploadBase64AsFile(cashDepositPoints);
				TbUacoDepositPointsDtls tbUacoDepositPointsDtls = TbUacoDepositPointsDtls.builder()
						.appId(cashDepositPoints.getAppId()).applicationId(cashDepositPoints.getApplicationId())
						.versionNo(cashDepositPoints.getVersionNo()).kmId(cashDepositPoints.getKmId())
						.refNo(cashDepositPoints.getDepPointRefNo()).createTs(new Timestamp(new Date().getTime()))
						.filePath(uploadedFilePath).amount(cashDepositPoints.getDepAmt())
						.depositPointName(cashDepositPoints.getDepPoint()).build();
				tbUacoDepositPointsDtlsLst.add(tbUacoDepositPointsDtls);
			}
			tbUacoDepositPointsRepository.saveAll(tbUacoDepositPointsDtlsLst);
		}
	}

	/**
	 * Method to upload the cash Deposit Certificate.
	 *
	 * @param cashDepositPoints
	 * @return
	 */
	private String uploadBase64AsFile(CashDepositPoints cashDepositPoints) {
		String absoluteFilePath = "";
		try {
			if (!CommonUtils.checkStringNullOrEmpty(cashDepositPoints.getBase64FilePath())) {
				String filePath = CommonUtils.getCommonProperties("fileUploadPath");
				logger.debug("filePath::{}", filePath);
				StringBuilder sb = new StringBuilder(cashDepositPoints.getApplicationId()).append("_")
						.append(cashDepositPoints.getDepPointRefNo()).append(".")
						.append(cashDepositPoints.getFileType());
				String fileName = sb.toString();
				logger.debug("uploadBase64AsFile fileName::{}", fileName);
				byte[] decodedBytes = Base64.getDecoder().decode(cashDepositPoints.getBase64FilePath());
				absoluteFilePath = filePath + fileName;
				File outputFile = new File(absoluteFilePath);
				try (FileOutputStream fos = new FileOutputStream(outputFile)) {
					fos.write(decodedBytes);
				}
			}
		} catch (Exception ex) {
			logger.error(CommonConstants.EXCEP_OCCURED, ex);
		}
		logger.debug("uploadBase64AsFile method response::{}", absoluteFilePath);
		return absoluteFilePath;
	}

	/**
	 * Method to store list of Cash Deposit Points based on the ApplicationMaster
	 * list.
	 *
	 * @param cashDepositPointsLst
	 * @param applnDetails
	 */
	private void fetchCashDepositDtls(List<CashDepositPoints> cashDepositPointsLst, ApplicationDetails applnDetails) {
		logger.debug("cashDepositPointsLst::{}", cashDepositPointsLst);
		logger.debug("applnDetails::{}", applnDetails);

		String applicationId = applnDetails.getApplnId();
		List<TbUacoDepositPointsDtls> tbUacoDepositPointsDtlsLst = tbUacoDepositPointsRepository
				.findByAppIdAndApplicationIdAndVersionNoOrderByVersionNoDesc(applnDetails.getAppId(), applicationId,
						applnDetails.getVerNo());
		logger.debug("tbUacoDepositPointsDtlsLst::{}", tbUacoDepositPointsDtlsLst);
		if (!tbUacoDepositPointsDtlsLst.isEmpty()) {
			tbUacoDepositPointsDtlsLst.stream().forEach(tbUacoDepositPointsDtls -> {
				CashDepositPoints cashDepositPoints = CashDepositPoints.builder()
						.appId(tbUacoDepositPointsDtls.getAppId())
						.applicationId(tbUacoDepositPointsDtls.getApplicationId())
						.versionNo(tbUacoDepositPointsDtls.getVersionNo()).kmId(tbUacoDepositPointsDtls.getKmId())
						.depPointRefNo(tbUacoDepositPointsDtls.getRefNo())
						.createTs(tbUacoDepositPointsDtls.getCreateTs().toString())
						.base64FilePath(tbUacoDepositPointsDtls.getFilePath())
						.depAmt(tbUacoDepositPointsDtls.getAmount())
						.depPoint(tbUacoDepositPointsDtls.getDepositPointName()).build();
				logger.debug("cashDepositPoints::{}", cashDepositPoints);
				cashDepositPointsLst.add(cashDepositPoints);
			});
		}
	}

	/**
	 * Method to fetch the cash deposit receipt from file path.
	 *
	 * @param collectionRequest
	 * @param header
	 * @return
	 */
	public Response getFileData(FetchCollectionRequest collectionRequest, Header header) {
		Response response = new Response();
		ResponseHeader respHeader = new ResponseHeader();
		ResponseBody respBody = new ResponseBody();
		try {
			logger.debug("Inside getFileData request::{}", collectionRequest);
			Optional<TbUacoDepositPointsDtls> tbUacoDepositPointsDtlsOpt = tbUacoDepositPointsRepository
					.findTopByAppIdAndRefNoAndVersionNoOrderByVersionNoDesc(collectionRequest.getAppId(),
							collectionRequest.getRequestObj().getRefNo(),
							collectionRequest.getRequestObj().getVersionNo());
			if (tbUacoDepositPointsDtlsOpt.isPresent()) {
				String filePath = tbUacoDepositPointsDtlsOpt.get().getFilePath();
				File file = new File(filePath);
				if (file.exists()) {
					byte[] fileContent;
					try (FileInputStream fis = new FileInputStream(file)) {
						fileContent = new byte[(int) file.length()];
						int readBytes = fis.read(fileContent);
						if (readBytes == -1) {
							logger.error("Failed to read file content.");
						}
						String base64 = Base64.getEncoder().encodeToString(fileContent);
						respBody.setResponseObj(new JSONObject().put("base64", base64).toString());
						CommonUtils.generateHeaderForSuccess(respHeader);
					}
				} else {
					respBody.setResponseObj("");
					CommonUtils.generateHeaderForNoResult(respHeader);
				}
			} else {
				respBody.setResponseObj("");
				CommonUtils.generateHeaderForNoResult(respHeader);
			}
		} catch (Exception e) {
			logger.error("exception at getFileData: ", e);
			respBody.setResponseObj("");
			CommonUtils.generateHeaderForFailure(respHeader, CommonConstants.EXCEPTION_MSG);
		}
		response.setResponseBody(respBody);
		response.setResponseHeader(respHeader);
		return response;
	}

	/**
	 * Method to fetch repeat Collection data
	 *
	 * @param colRequest
	 * @param header
	 * @return
	 */
	public Mono<Response> fetchRepeatCollection(FetchRptAndNonMeetingColReq colRequest, Header header) {
		Response response = new Response();
		ResponseHeader respHeader = new ResponseHeader();
		ResponseBody respBody = new ResponseBody();
		try {
			logger.debug("Inside fetchRepeatCollection request::{}", colRequest);
			if(!CommonUtils.checkStringNullOrEmpty(colRequest.getRequestObj().getKendraId()) && colRequest.getRequestObj().getKendraId().contains("~")) {
				String[] kendras = colRequest.getRequestObj().getKendraId().split("~");
				List<String> kendraLst = Arrays.asList(kendras);
				return Flux.fromIterable(kendraLst).flatMap(kendraData -> {

				    FetchRptAndNonMeetingColReqFields fetchRptAndNonMeetingColReqFields = FetchRptAndNonMeetingColReqFields
				            .builder().kendraId(kendraData).branchId(colRequest.getRequestObj().getBranchId()).build();

				    FetchRptAndNonMeetingColReq individualColReq = FetchRptAndNonMeetingColReq.builder()
				            .appId(header.getAppId())
				            .interfaceName(GET_RPT_COL_INTF)
				            .userId(header.getUserId())
				            .requestObj(fetchRptAndNonMeetingColReqFields)
				            .build();

					Mono<Object> fetchRptColMono = interfaceAdapter.callExternalService(header, individualColReq,
							GET_RPT_COL_INTF, true);

				    Map<String, String> respMap = new HashMap<>();
				    return fetchRptColMono.flatMap(fetchRptCol -> {
				        logger.debug("fetchRepeatCollection response::{}", fetchRptCol);
				        Response resp = this.frameRptColResponse(fetchRptCol, respHeader, respBody);
				        JSONObject responseJSON = new JSONObject();
				        if("0".equalsIgnoreCase(resp.getResponseHeader().getResponseCode())) {
				        	responseJSON.put("id", kendraData);
				        	responseJSON.put("data", resp.getResponseBody().getResponseObj());
				        }
				        respMap.put(kendraData, responseJSON.toString());
			        	return Mono.just(respMap);
				    });
				}).collectList().flatMap(kendraListResp -> {
				    ResponseHeader responseHeader = new ResponseHeader();
				    ResponseBody responseBody = new ResponseBody();
				    logger.debug("Final Repeat Response List::{}", kendraListResp);
				    try {
				        JSONArray jsonArray = new JSONArray();
				    	for (Map<String, String> map : kendraListResp) {
				            for (Map.Entry<String, String> entry : map.entrySet()) {
				                JSONObject jsonObject = new JSONObject();
				                jsonObject.put("key", entry.getKey());
								if (null != entry.getValue() && entry.getValue().startsWith("{")
										&& entry.getValue().endsWith("}")) {
									if ("{}".equalsIgnoreCase(entry.getValue())) {
										jsonObject.put("status", "1");
									} else {
										jsonObject.put("value", new JSONObject(entry.getValue()));
										jsonObject.put("status", "0");
									}
								}
				                jsonArray.put(jsonObject);
				            }
				        }
						responseBody.setResponseObj(jsonArray.toString());
					} catch (Exception ex) {
						logger.error("Exception Occurred::", ex);
					}
				    Response finalResponse = Response.builder().responseBody(responseBody).responseHeader(responseHeader).build();
				    return Mono.just(finalResponse);
				});
			} else {
				logger.debug("Inside fetchRepeatCollection request for single kendra::{}", colRequest);
				Mono<Object> fetchKendraInfoMono = interfaceAdapter.callExternalService(header, colRequest,
						GET_RPT_COL_INTF, true);
				return fetchKendraInfoMono.flatMap(val -> {
					logger.debug("fetchRepeatCollection::{}", val);
					return this.frameRptNonMeetingColResponse(val, respHeader, respBody);
				});
			}
		} catch (Exception e) {
			logger.error("Exception at fetchRepeatCollection: ", e);
			CommonUtils.generateHeaderForFailure(respHeader, CommonConstants.EXCEPTION_MSG);
			response.setResponseBody(respBody);
			response.setResponseHeader(respHeader);
			return Mono.just(response);
		}
	}

	/**
	 * Method to fetch repeat Collection data
	 *
	 * @param colRequest
	 * @param header
	 * @return
	 */
	public Mono<Response> fetchNonMeetingCollection(FetchRptAndNonMeetingColReq colRequest, Header header) {
		Response response = new Response();
		ResponseHeader respHeader = new ResponseHeader();
		ResponseBody respBody = new ResponseBody();
		try {
			logger.debug("Inside fetchNonMeetingCollection request::{}", colRequest);
			Mono<Object> fetchKendraInfoMono = interfaceAdapter.callExternalService(header, colRequest,
					GET_NON_MTNG_COL_INTF, true);
			return fetchKendraInfoMono.flatMap(val -> {
				logger.debug("fetchNonMeetingCollection::{}", val);
				return this.frameRptNonMeetingColResponse(val, respHeader, respBody);
			});
		} catch (Exception e) {
			logger.error("exception at fetchNonMeetingCollection: ", e);
			CommonUtils.generateHeaderForFailure(respHeader, CommonConstants.EXCEPTION_MSG);
			response.setResponseBody(respBody);
			response.setResponseHeader(respHeader);
			return Mono.just(response);
		}
	}

	private Mono<Response> frameRptNonMeetingColResponse(Object val, ResponseHeader respHeader, ResponseBody respBody) {
		if (val instanceof HashMap<?, ?> || val instanceof ArrayList<?>) {
			try {
				String extApiResp = new ObjectMapper().writeValueAsString(val);
				if (extApiResp.startsWith("{") && extApiResp.endsWith("}")) {
					JSONObject respJSON = new JSONObject(extApiResp);
					if (respJSON.has("body") && !"[]".equalsIgnoreCase(respJSON.get("body").toString())) {
						CommonUtils.generateHeaderForSuccess(respHeader);
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
		Response response = Response.builder().responseHeader(respHeader).responseBody(respBody).build();
		return Mono.just(response);
	}

	private Response frameRptColResponse(Object val, ResponseHeader respHeader, ResponseBody respBody) {
		if (val instanceof HashMap<?, ?> || val instanceof ArrayList<?>) {
			try {
				String extApiResp = new ObjectMapper().writeValueAsString(val);
				if (extApiResp.startsWith("{") && extApiResp.endsWith("}")) {
					JSONObject respJSON = new JSONObject(extApiResp);
					if (respJSON.has("body") && !"[]".equalsIgnoreCase(respJSON.get("body").toString())) {
						CommonUtils.generateHeaderForSuccess(respHeader);
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
		return Response.builder().responseHeader(respHeader).responseBody(respBody).build();
	}

	/**
	 * Method to verify if the application is already submitted and available in
	 * ApplicationMaster
	 *
	 * @param verifyApplnRequest
	 * @return
	 */
	public Response verifyApplication(VerifyApplnRequest verifyApplnRequest) {
		ResponseHeader responseHeader = new ResponseHeader();
		ResponseBody responseBody = null;
		try {
			logger.debug("Inside verifyApplication request::{}", verifyApplnRequest);
			String kendraId = verifyApplnRequest.getRequestObj().getKendraId();
			String branchId = verifyApplnRequest.getRequestObj().getBranchId();
			String userId = verifyApplnRequest.getUserId();
			String applicationId = verifyApplnRequest.getRequestObj().getApplicationId();
			Optional<ApplicationMaster> tbApplnMasterOpt = applicationMasterRepository
					.findTopByAppIdAndCreatedByAndApplicationIdAndApplicationTypeAndKendraIdAndBranchCodeOrderByVersionNumDesc(
							verifyApplnRequest.getAppId(), userId,applicationId, APPLN_TYPE, kendraId, branchId);

			logger.debug("tbApplnMasterOpt::{}", tbApplnMasterOpt);

			if (tbApplnMasterOpt.isPresent()) {
				Optional<ApplicationWorkflow> applnWFOpt = applicationWorkflowRepository
						.findTopByAppIdAndApplicationIdAndApplicationStatusOrderByWorkflowSeqNumDesc(verifyApplnRequest.getAppId(),
								applicationId, APPROVED);
				if (applnWFOpt.isPresent() && BM_ROLE.equalsIgnoreCase(applnWFOpt.get().getCurrentRole())) {
					logger.debug("inside If Block applnWFOpt::{}", applnWFOpt);
					CommonUtils.generateHeaderForSuccess(responseHeader);
				} else {
					CommonUtils.generateHeaderForFailure(responseHeader, "Application already exists.");
				}
			} else {
				CommonUtils.generateHeaderForSuccess(responseHeader);
			}
			responseBody = ResponseBody.builder().responseObj("").build();
		} catch (Exception ex) {
			logger.error("Exception at verifyApplication:", ex);
			CommonUtils.generateHeaderForGenericError(responseHeader);
		}
		return Response.builder().responseBody(responseBody).responseHeader(responseHeader).build();
	}

	public Response applicationDedupeService(ApplicationDedupeRequest dedupeRequest) {
		String kmId = dedupeRequest.getRequestObj().getKmId();
		String roleId = dedupeRequest.getRequestObj().getRoleId();
		String meetingDate = dedupeRequest.getRequestObj().getMeetingDate();
		String kendraId = dedupeRequest.getRequestObj().getKendraId();
		logger.debug("Inside dedupeRequest => kmId: {}, roleId: {}, meetingDate: {}, kendraId: {}", kmId, roleId,
				meetingDate, kendraId);

		Map<String, Object> respMap = new HashMap<>();
		String oppositeRole = "KM".equalsIgnoreCase(roleId) ? "DEO" : "KM";
		Optional<ApplicationMaster> applnMaster = applicationMasterRepository.findByKendraIdAndApplicationDateAndAddInfoContaining(kendraId,
				LocalDate.parse(meetingDate), oppositeRole);
		logger.debug("Fetched ApplicationMaster record: {}", applnMaster);

		if (applnMaster.isPresent()) {
			ApplicationMaster master = applnMaster.get();
			String message = String.format("KendraId %s is already collected by %s with role %s", master.getKendraId(),
					master.getKmId(), oppositeRole);
			respMap.put("KendraId", master.getKendraId());
			respMap.put("Add_Info", master.getAddInfo());
			respMap.put("KmId", master.getKmId());
			respMap.put("StatusCode", 0);
			respMap.put("Message", message);
		} else {
			respMap.put("StatusCode", 1);
			respMap.put("Message", "Data is not present");
		}

		return buildResponse(respMap);
	}

	public Response applicationDedupeRepeatService(ApplicationDedupeRepeatRequest dedupeRepeatRequest) {
		String kmId = dedupeRepeatRequest.getRequestObj().getKmId();
		String roleId = dedupeRepeatRequest.getRequestObj().getRoleId();
		String meetingDate = dedupeRepeatRequest.getRequestObj().getMeetingDate();
		String kendraId = dedupeRepeatRequest.getRequestObj().getKendraId();
		String applicationId = dedupeRepeatRequest.getRequestObj().getApplicationId();
		logger.debug("Inside dedupeRepeatRequest => kmId: {}, roleId: {}, meetingDate: {}, kendraId: {}, applicationId: {}", kmId, roleId,
				meetingDate, kendraId, applicationId);

		Map<String, Object> respMap = new HashMap<>();
		String oppositeRole = "KM".equalsIgnoreCase(roleId) ? "DEO" : "KM";


		Optional<ApplicationMaster> applnMaster = applicationMasterRepository
					.findByApplicationIdAndApplicationDateAndAddInfoContaining(applicationId, LocalDate.parse(meetingDate), oppositeRole);
			logger.debug("Fetched ApplicationMaster record for repeat: {}", applnMaster);

		if (applnMaster.isPresent()) {
			ApplicationMaster master = applnMaster.get();
			String message = String.format("KendraId %s is already collected by %s with role %s", master.getKendraId(),
					master.getKmId(), oppositeRole);
			respMap.put("KendraId", master.getKendraId());
			respMap.put("Add_Info", master.getAddInfo());
			respMap.put("KmId", master.getKmId());
			respMap.put("StatusCode", 0);
			respMap.put("Message", message);
		} else {
			respMap.put("StatusCode", 1);
			respMap.put("Message", "Data is not present");
		}
		return buildResponse(respMap);
	}

	public Response multipleDedupeService(MultipleRepeatDedupeRequest mulRepeatDedupeRequest) {
		Response response = new Response();
		ResponseHeader respHeader = new ResponseHeader();
		ResponseBody respBody = new ResponseBody();

		try {
			Optional<ApplicationMaster> applnMstOpt = Optional.empty();
			try {
				applnMstOpt = applicationMasterRepository
						.findTopByApplicationIdAndKendraIdAndApplicationDateOrderByVersionNumDesc(
								mulRepeatDedupeRequest.getRequestObj().getApplicationId(),
								mulRepeatDedupeRequest.getRequestObj().getKendraId(),
								LocalDate.parse(mulRepeatDedupeRequest.getRequestObj().getMeetingDate()));
			} catch (Exception e) {
				logger.error("Error fetching ApplicationMaster", e);
			}

			List<ApplicationWorkflow> applnWFLst = new ArrayList<>();
			try {
				applnWFLst = applicationWorkflowRepository
						.findByApplicationId(mulRepeatDedupeRequest.getRequestObj().getApplicationId());
			} catch (Exception e) {
				logger.error("Error fetching ApplicationWorkflow", e);
			}

			JSONObject responseData = new JSONObject();
			if (applnMstOpt.isPresent()) {
				ApplicationMaster applnMst = applnMstOpt.get();
				DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

				ApplicationDetails applnDetails = ApplicationDetails.builder().appId(applnMst.getAppId())
						.applnId(applnMst.getApplicationId()).verNo(applnMst.getVersionNum())
						.kendraId(applnMst.getKendraId()).brnCode(applnMst.getBranchCode())
						.applnDate(
								applnMst.getApplicationDate() != null ? applnMst.getApplicationDate().toString() : "")
						.createTs(applnMst.getCreateTs() != null
								? applnMst.getCreateTs().toLocalDateTime().format(formatter)
								: LocalDateTime.now().format(formatter))
						.createdBy(applnMst.getCreatedBy()).appType(applnMst.getApplicationType())
						.appStatus(applnMst.getApplicationStatus()).currStage(applnMst.getCurrentStage())
						.kmId(applnMst.getKmId()).leader(applnMst.getLeader()).kendraName(applnMst.getKendraName())
						.amount(applnMst.getAmount() != null ? applnMst.getAmount().toString() : "0")
						.refNo(applnMst.getApplicationRefNo()).build();

				List<ApplicationWFDetails> applnWFDtlsLst = applnWFLst.stream()
						.map(workflow -> ApplicationWFDetails.builder().appId(workflow.getAppId())
								.applnId(workflow.getApplicationId()).verNo(String.valueOf(workflow.getVersionNum()))
								.seqNo(String.valueOf(workflow.getWorkflowSeqNum())).createdBy(workflow.getCreatedBy())
								.kmUserName(workflow.getCreatedUserName()).appStatus(workflow.getApplicationStatus())
								.createTs(workflow.getCreateTs() != null ? workflow.getCreateTs().format(formatter)
										: LocalDateTime.now().format(formatter))
								.usrRole(workflow.getCurrentRole()).nextWFStage(workflow.getNextWorkFlowStage())
								.remarks(workflow.getRemarks() != null ? workflow.getRemarks() : "").build())
						.toList();

				responseData.put("StatusCode", 0);
				responseData.put("Message", "Success");
				responseData.put("applicationMasterData", applnDetails);
				responseData.put("applicationWorkflowData", applnWFDtlsLst);

				respBody.setResponseObj(responseData.toString());
				CommonUtils.generateHeaderForSuccess(respHeader);

			} else {
				responseData.put("StatusCode", 1);
				responseData.put("Message", "Data is not present");
				responseData.put("applicationMasterData", JSONObject.NULL);
				responseData.put("applicationWorkflowData", new ArrayList<>());
				respBody.setResponseObj(responseData.toString());
				CommonUtils.generateHeaderForNoResult(respHeader);
			}

		} catch (Exception ex) {
			logger.error("Unexpected error in multipleDedupeService", ex);
			JSONObject errorData = new JSONObject();
			errorData.put("StatusCode", 2);
			errorData.put("Message", "Internal Server Error");
			errorData.put("applicationMasterData", JSONObject.NULL);
			errorData.put("applicationWorkflowData", new ArrayList<>());
			respBody.setResponseObj(errorData.toString());
			CommonUtils.generateHeaderForFailure(respHeader, "Unexpected error in multipleDedupeService");
		}

		response.setResponseBody(respBody);
		response.setResponseHeader(respHeader);
		return response;
	}

	private Response buildResponse(Map<String, Object> respMap) {
		Response response = new Response();
		ResponseHeader responseHeader = new ResponseHeader();
		ResponseBody respBody = new ResponseBody();

		try {
			ObjectMapper objectMapper = new ObjectMapper();
			String jsonString = objectMapper.writeValueAsString(respMap);
			respBody.setResponseObj(jsonString);
		} catch (JsonProcessingException e) {
			logger.error("Error serializing response map", e);
			respBody.setResponseObj("{}");
		}

		CommonUtils.generateHeaderForSuccess(responseHeader);
		response.setResponseHeader(responseHeader);
		response.setResponseBody(respBody);
		return response;
	}

	public Response fetchEmergencyLoanDetails(FetchEmergencyLoanRequest loanRequest, Header header) {
		List<String> kendraId = loanRequest.getRequestObj().getKendraId();
		String meetingDateStr = loanRequest.getRequestObj().getMeetingDate();
		String productCode = loanRequest.getRequestObj().getProductCode();

		Response response = new Response();
		ResponseHeader respHeader = new ResponseHeader();
		ResponseBody respBody = new ResponseBody();

		try {
			LocalDate meetingDate = LocalDate.parse(meetingDateStr);
			List<String> statuses = Arrays.asList("DISBURSED", "INITIATE");

			List<ApplicationMaster> applications = applicationMasterRepository
					.findByKendraIdInAndApplicationDateAndProductCodeAndApplicationStatusIn(kendraId, meetingDate,
							productCode, statuses);

			if (applications.isEmpty()) {
				respBody.setResponseObj("No application found for given criteria");
				CommonUtils.generateHeaderForFailure(respHeader, "APPLICATION_NOT_FOUND");
				response.setResponseHeader(respHeader);
				response.setResponseBody(respBody);
				return response;
			}

			List<String> validWorkflowAppIds = applicationWorkflowRepository.findApplicationIdsByDate(meetingDate);

			Map<String, List<String>> kendraPayloadMap = new HashMap<>();
			ObjectMapper mapper = new ObjectMapper();

			for (ApplicationMaster app : applications) {
				String currKendraId = app.getKendraId();
				String applicationId = app.getApplicationId();

				if (!validWorkflowAppIds.contains(applicationId)) {
					logger.debug("Skipping applicationId {} as it's not present in workflow table", applicationId);
					continue;
				}

				String payload = tbUalnLoanDtlsRepository.findPayloadByApplicationId(applicationId);

				if (payload != null) {
					try {
						JsonNode payloadJson = mapper.readTree(payload);

						// Check disburseMode == CASH
						JsonNode disburseModeNode = payloadJson.get("disburseMode");
						if (disburseModeNode != null
								&& "CASH".equalsIgnoreCase(disburseModeNode.get("idDesc").asText())) {
							kendraPayloadMap.computeIfAbsent(currKendraId, k -> new ArrayList<>()).add(payload);
						}
					} catch (Exception e) {
						logger.error("Error parsing payload for applicationId {}: {}", applicationId, e.getMessage());
					}
				}
			}

			if (kendraPayloadMap.isEmpty()) {
				respBody.setResponseObj("No valid CASH disbursed loans found for given ApplicationIds");
				CommonUtils.generateHeaderForFailure(respHeader, "NO_VALID_LOANS");
			} else {
				respBody.setResponseObj(new JSONObject(kendraPayloadMap).toString());
				CommonUtils.generateHeaderForSuccess(respHeader);
			}

			response.setResponseHeader(respHeader);
			response.setResponseBody(respBody);
			return response;

		} catch (Exception e) {
			logger.error("Unexpected error in fetchEmergencyLoanDetails: {}", e.getMessage(), e);
			respBody.setResponseObj("Internal Server Error: " + e.getMessage());
			CommonUtils.generateHeaderForFailure(respHeader, "INTERNAL_SERVER_ERROR");
			response.setResponseHeader(respHeader);
			response.setResponseBody(respBody);
			return response;
		}
	}

	public Response creatStatus(ApplicationStatus applicationStatus, Header header) {

		Response response = new Response();
		ResponseHeader respHeader = new ResponseHeader();
		ResponseBody respBody = new ResponseBody();
		String currentUserId = applicationStatus.getUserId();
		CollectionReport statusdata = null;
		ApplicationStatusFields statusfill = applicationStatus.getRequestObj();
		ObjectMapper mapper = new ObjectMapper();
		String userRole = getUserRole(currentUserId);
		logger.debug("Associated user role : {} ", userRole);
		String type = getCollectionApplicationType(statusfill.getApplicationId());
		String payloadJson = "{}";

		try {
			Object payloadObj = statusfill.getPayload();
			if (payloadObj != null) {
				payloadJson = mapper.writeValueAsString(payloadObj);
			}
		} catch (JsonProcessingException e) {
			logger.error("Error serializing payload: {}", e.getMessage(), e);
		}

		String status = statusfill.getStatus() != null ? statusfill.getStatus().trim() : "";

		try {

			Optional<CollectionReport> existingOpt = collectionReportRepository
					.findFirstByApplicationIdAndKendraIdAndBranchIdOrderByCreatedTsAsc(statusfill.getApplicationId(),
							statusfill.getKendraId(), statusfill.getBranchId());

			if (status.equalsIgnoreCase("start")) {
				statusdata = CollectionReport.builder().applicationId(statusfill.getApplicationId())
						.kendraId(statusfill.getKendraId()).branchId(statusfill.getBranchId())
						.seqNo(statusfill.getSeqNo()).status(status).payload(payloadJson).createdTs(LocalDateTime.now())
						.updatedTs(LocalDateTime.now()).created_tsby(currentUserId).updated_tsby(currentUserId)
						.user_role(userRole).collection_type(type).build();

				logger.debug(
						"Creating new START status record for applicationId={}, kendraId={}, branchId={}, seqNo={}, created_by={}",
						statusfill.getApplicationId(), statusfill.getKendraId(), statusfill.getBranchId(),
						statusfill.getSeqNo(), currentUserId);

			} else if (status.equalsIgnoreCase("close") && existingOpt.isPresent()) {
				CollectionReport existing = existingOpt.get();

				statusdata = CollectionReport.builder().applicationId(statusfill.getApplicationId())
						.kendraId(statusfill.getKendraId()).branchId(statusfill.getBranchId())
						.seqNo(statusfill.getSeqNo()).status(status).payload(payloadJson)
						.createdTs(existing.getCreatedTs()).updatedTs(LocalDateTime.now())
						.created_tsby(existing.getCreated_tsby()).updated_tsby(currentUserId).collection_type(type)
						.user_role(userRole).build();

				logger.debug(
						"Updating CLOSE status for applicationId={}, kendraId={}, branchId={}, seqNo={}, created_by={}, updated_by={}",
						existing.getApplicationId(), existing.getKendraId(), existing.getBranchId(),
						existing.getSeqNo(), existing.getCreated_tsby(), currentUserId);
			} else {
				logger.warn("No matching START record found or unsupported status: {}", status);
			}

			collectionReportRepository.save(statusdata);
			logger.debug("Data saved successfully");

			JSONObject responseData = new JSONObject();
			responseData.put("StatusCode", 0);
			responseData.put("Message", "Success");

			respHeader.setErrorCode("0");
			respBody.setResponseObj(responseData.toString());
			response.setResponseHeader(respHeader);
			response.setResponseBody(respBody);

		} catch (DataAccessException ex) {
			logger.error("Error while saving data: {}", ex.getMessage(), ex);

			JSONObject errorData = new JSONObject();
			errorData.put("StatusCode", 1);
			errorData.put("Message", "Error while saving data");

			respHeader.setErrorCode("1");
			respBody.setResponseObj(errorData.toString());
			response.setResponseHeader(respHeader);
			response.setResponseBody(respBody);
		} catch (Exception e) {
			logger.error("Unexpected error: {}", e.getMessage(), e);

			JSONObject errorData = new JSONObject();
			errorData.put("StatusCode", 2);
			errorData.put("Message", "Unexpected error occurred");

			respHeader.setErrorCode("2");
			respBody.setResponseObj(errorData.toString());
			response.setResponseHeader(respHeader);
			response.setResponseBody(respBody);
		}

		return response;
	}

	public String getUserRole(String userId) {
		try {
			logger.debug("Fetching roles for userId: {}", userId);

			List<UserRole> userRoleList = tbAsmiUserRoleRepo.findByUserId(userId);
			logger.debug("userRoleList: {}", userRoleList);

			List<String> roles = userRoleList.stream().map(UserRole::getRoleId).map(String::toUpperCase).toList();

			List<String> finalList = new ArrayList<>();

			if (roles.contains("BM"))
				finalList.add("BM");
			if (roles.contains("KM"))
				finalList.add("KM");

			roles.stream().filter(r -> !r.equals("BM") && !r.equals("KM")).forEach(finalList::add);

			return String.join("~", finalList);

		} catch (Exception e) {
			logger.error("Error while fetching user roles for userId: {}", userId, e);
			return "";
		}
	}

	public Response interimCreate(InterimApplicationMasterRequest interimApplicationMasterRequest, Header header) {

		logger.info("Start: interimCreate request received → {}", interimApplicationMasterRequest);

		Response response = new Response();
		ResponseHeader responseHeader = new ResponseHeader();
		ResponseBody responseBody = new ResponseBody();

		try {
			InterimApplicationMasterRequestFields requestFields = interimApplicationMasterRequest.getRequestObj();
			logger.debug("InterimApplicationMasterRequestFields: {}", requestFields);

			TbUacoInterimApplicationMaster entity = new TbUacoInterimApplicationMaster();

			entity.setAppId(requestFields.getAppId());
			entity.setAddInfo(requestFields.getAddInfo());
			entity.setApplicationDate(requestFields.getApplicationDate());
			entity.setApplicationId(requestFields.getApplicationId());
			entity.setApplicationStatus(requestFields.getApplicationStatus());
			entity.setBranchCode(requestFields.getBranchCode());
			entity.setCreatedBy(requestFields.getCreatedBy());
			entity.setCreateTs(requestFields.getCreateTs());
			entity.setCurrentStage(requestFields.getCurrentStage());
			entity.setKendraId(requestFields.getKendraId());
			entity.setKmid(requestFields.getKmid());
			entity.setKendraname(requestFields.getKendraname());
			entity.setLatestVersionNo(Integer.parseInt(requestFields.getLatestVersionNo()));
			entity.setRemarks(requestFields.getRemarks());
			entity.setPayload(requestFields.getPayload());

			tbUacoInterimApplicationMasterRepository.save(entity);

			logger.info("Interim application saved successfully in DB. ApplicationId: {}", requestFields.getApplicationId());

			CommonUtils.generateHeaderForSuccess(responseHeader);
			JSONObject successObj = new JSONObject();
			successObj.put("message", "Interim application saved successfully");
			successObj.put("applicationId", requestFields.getApplicationId());
			successObj.put("STATUS", 0);

			responseBody.setResponseObj(successObj.toString());

		} catch (Exception ex) {

			logger.error("Exception while saving interim application", ex);

			CommonUtils.generateHeaderForGenericError(responseHeader);

			JSONObject errorObj = new JSONObject();
			errorObj.put("error", "Failed to save interim application");
			errorObj.put("details", ex.getMessage());
			errorObj.put("STATUS", 1);

			responseBody.setResponseObj(errorObj.toString());
		}

		response.setResponseHeader(responseHeader);
		response.setResponseBody(responseBody);

		logger.info("End: interimCreate response → {}", response);
		return response;
	}

}
