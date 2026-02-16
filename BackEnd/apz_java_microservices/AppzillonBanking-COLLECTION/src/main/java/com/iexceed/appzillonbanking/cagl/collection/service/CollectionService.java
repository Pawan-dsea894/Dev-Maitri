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
import com.iexceed.appzillonbanking.cagl.collection.payload.*;
import com.iexceed.appzillonbanking.cagl.collection.repository.ab.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
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
		Response response;
		try {
			String addInfo = "";
		    List<String> skippedKendraIds = new ArrayList<>();
			Optional<WorkflowDefinition> applnWFDefnOpt = Optional.empty();
			logger.debug("Inside createApplication request::{}", collectionRequest);
			List<CollectionsData> kendraDtlsLst = collectionRequest.getRequestObj().getKendraDtls();
			logger.debug("Inside kendraDtlsLst request::{}", kendraDtlsLst);
			for (CollectionsData kendraData : kendraDtlsLst) {
				logger.debug("kendraData request::{}", kendraData);
//				String kendraId = kendraData.getKendraId();
//				String meetingDate = kendraData.getMeetingDate();
//				JSONObject kendraObj = new JSONObject(kendraData.toString());
//				String curApplnId = kendraObj.optJSONObject("colldtls").optString("applnId");
//				if (!curApplnId.startsWith("R")) {
//					logger.debug("kendraId and meetingDate : {} : {} ", kendraId, meetingDate);
//					boolean isDuplicate = applicationDedupe(kendraId, LocalDate.parse(meetingDate));
//					if (isDuplicate) {
//						logger.debug("Duplicate found → skipping kendraId: {}, meetingDate: {}", kendraId, meetingDate);
//						skippedKendraIds.add(kendraId);
//						continue;
//					}
//				}
				// Insert data to ApplicationMaster
				ApplicationDetails applnDtls = kendraData.getApplnDtls();
				logger.debug("applnDtls request::{}", applnDtls);
				String applnDt = applnDtls.getApplnDate();

				String applnType = getApplicationType(applnDtls.getApplnId());
				// code change to insert additional data for DEO in application master table.
				String userRole = Optional.ofNullable(collectionRequest.getKmUserRole())
						.filter(role -> !role.trim().isEmpty()).orElse("KM");

				addInfo = userRole + "~" + collectionRequest.getUserId();

				ApplicationMaster applnMaster = ApplicationMaster.builder().appId(applnDtls.getAppId())
						.applicationId(applnDtls.getApplnId())
						.versionNum((applnDtls.getVerNo() == null || applnDtls.getVerNo().trim().isEmpty()) ? "1"
								: applnDtls.getVerNo())
						.kendraId(applnDtls.getKendraId()).applicationDate(LocalDate.parse(applnDt))
						.createdBy(applnDtls.getCreatedBy()).applicationType(applnType).applicationStatus("INPROGRESS")
						.branchCode(applnDtls.getBrnCode()).currentStage("CASHHANDOVER").kmId(applnDtls.getKmId())
						.leader(applnDtls.getLeader()).kendraName(applnDtls.getKendraName())
						.amount(new BigDecimal(applnDtls.getAmount())).applicationRefNo(applnDtls.getRefNo())
						.addInfo(addInfo).build();
				logger.debug("applnMaster::", applnMaster);
				applicationMasterRepository.save(applnMaster);

				// Insert data to TbUacoKendraDtls
				JSONObject kendraDtlsJSON = new JSONObject();
				kendraDtlsJSON.put("lat", kendraData.getLat());
				kendraDtlsJSON.put("long", kendraData.getLongitude());
				kendraDtlsJSON.put("updLoc", kendraData.getUpdLoc());
				kendraDtlsJSON.put("meetingDate", kendraData.getMeetingDate());
				kendraDtlsJSON.put("meetingDay", kendraData.getMeetingDay());
				kendraDtlsJSON.put("collId", kendraData.getColldtls().getCollId());
				kendraDtlsJSON.put("colDate", kendraData.getColldtls().getCollDate());
				kendraDtlsJSON.put("type", kendraData.getType());

				kendraDtlsJSON.put("totCollAmt", kendraData.getColldtls().getTotCollAmt());
				kendraDtlsJSON.put("totalAdv", kendraData.getColldtls().getTotalAdv());
				kendraDtlsJSON.put("totalDue", kendraData.getColldtls().getTotalDue());
				kendraDtlsJSON.put("netDue", kendraData.getColldtls().getNetDue());

				TbUacoKendraDtls tbUacoKendraDtls = TbUacoKendraDtls.builder().appId(applnDtls.getAppId())
						.applicationId(applnDtls.getApplnId())
						.versionNum((applnDtls.getVerNo() == null || applnDtls.getVerNo().trim().isEmpty()) ? "1"
								: applnDtls.getVerNo())
						.kendraId(kendraData.getKendraId()).kendraName(kendraData.getKendraName())
						.startTime(kendraData.getStartTime()).payload(kendraDtlsJSON.toString()).build();
				logger.debug("tbUacoKendraDtls::", tbUacoKendraDtls);
				tbUacoKendraDtlsRepository.save(tbUacoKendraDtls);

				// Insert list of data to TbUaobCustDtls
				List<TbUaobCustDtls> tbUaobCustDtlsLst = new ArrayList<>();
				List<CollectionGroupDtls> collectionGrpDtlLst = kendraData.getColldtls().getCollectionGroupDtls();
				AtomicInteger curMemberPos = new AtomicInteger(1);
				for (CollectionGroupDtls collGroupDtls : collectionGrpDtlLst) {

					// Code change added to store the member position for final API submission
					List<CollectionMemberDtls> updatedMembers = collGroupDtls.getMembers().stream().map(member -> {
						member.setPos(curMemberPos.getAndIncrement());
						return member;
					}).toList();
					collGroupDtls.setMembers(updatedMembers);

					String custDtlId = CollectionUtil.generateCustDtlId(collGroupDtls.getId(), kendraData.getKendraId(),
							kendraData.getMeetingDate(), applnType);
					String payload = new ObjectMapper().writeValueAsString(collGroupDtls);
					TbUaobCustDtls tbUaobCustDtls = TbUaobCustDtls.builder().custDtlId(custDtlId)
							.appId(applnDtls.getAppId()).applicationId(applnDtls.getApplnId())
							.versionNo((applnDtls.getVerNo() == null || applnDtls.getVerNo().trim().isEmpty()) ? "1"
									: applnDtls.getVerNo())
							.kendraId(kendraData.getKendraId()).groupId(collGroupDtls.getId()).kycDetails(null)
							.bankDtls(null).payload(payload).build();
					tbUaobCustDtlsLst.add(tbUaobCustDtls);
				}
				logger.debug("tbUaobCustDtlsLst::{}", tbUaobCustDtlsLst);
				tbUaobCustomerDtlsRepo.saveAll(tbUaobCustDtlsLst);

				// Insert data to ApplicationWorkflow
				List<ApplicationWFDetails> applnWFDtls = kendraData.getApplnWFDtls();
				List<ApplicationWorkflow> applnWFLst = new ArrayList<>();
				if (applnWFDtls != null && !applnWFDtls.isEmpty()) {
					for (int i = 0; i < applnWFDtls.size(); i++) {
						ApplicationWFDetails applnWFDetails = applnWFDtls.get(i);

						if (!CommonUtils.checkStringNullOrEmpty(applnWFDetails.getWorkflowId())
								&& !CommonUtils.checkStringNullOrEmpty(applnWFDetails.getFromStageId())
								&& !CommonUtils.checkStringNullOrEmpty(applnWFDetails.getStageSeqNo())) {
							applnWFDefnOpt = workflowDefinitionRepository
									.findByAppIdAndStageSeqNumAndFromStageIdAndWorkFlowId(applnWFDetails.getAppId(),
											Integer.parseInt(applnWFDetails.getStageSeqNo()),
											applnWFDetails.getFromStageId(), applnWFDetails.getWorkflowId());
							logger.debug("applnWFDefnOpt::{}", applnWFDefnOpt);
							if (applnWFDefnOpt.isPresent()) {
								LocalDateTime createDtTime = LocalDateTime.now();
								ApplicationWorkflow applnWF = ApplicationWorkflow.builder()
										.appId(applnWFDetails.getAppId()).applicationId(applnWFDetails.getApplnId())
										.versionNum((applnWFDetails.getVerNo() == null
												|| applnWFDetails.getVerNo().trim().isEmpty()) ? 1
														: Integer.valueOf(applnWFDetails.getVerNo()))
										.workflowSeqNum(i + 1).createdBy(applnWFDetails.getCreatedBy())
										.createdUserName(applnWFDetails.getKmUserName()).createTs(createDtTime)
										.applicationStatus(applnWFDefnOpt.get().getNextWFStatus())
										.remarks(applnWFDetails.getRemarks()).currentRole(applnWFDetails.getUsrRole())
										.nextWorkFlowStage(applnWFDefnOpt.get().getNextStageId()).build();
								logger.debug("ApplnWF1::{}", applnWF);
								applnWFLst.add(applnWF);
							}
						} else {
							LocalDateTime createDtTime = LocalDateTime.now();
							if (applnWFDetails.getCreateTs() != null) {
								DateTimeFormatter dtf = DateTimeFormatter.ofPattern(CollectionUtil.DT_TIME_FORMAT);
								createDtTime = LocalDateTime.parse(applnWFDetails.getCreateTs(), dtf);
							}
							ApplicationWorkflow applnWF = ApplicationWorkflow.builder().appId(applnWFDetails.getAppId())
									.applicationId(applnWFDetails.getApplnId())
									.versionNum((applnWFDetails.getVerNo() == null
											|| applnWFDetails.getVerNo().trim().isEmpty()) ? 1
													: Integer.valueOf(applnWFDetails.getVerNo()))
									.workflowSeqNum(i + 1).createdBy(applnWFDetails.getCreatedBy())
									.createdUserName(applnWFDetails.getKmUserName()).createTs(createDtTime)
									.applicationStatus(applnWFDetails.getAppStatus())
									.remarks(applnWFDetails.getRemarks()).currentRole(applnWFDetails.getUsrRole())
									.nextWorkFlowStage(applnWFDetails.getNextWFStage()).build();
							applnWFLst.add(applnWF);
							logger.debug("ApplnWF::{}", applnWF);
						}
					}
				} else {
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

					ApplicationWorkflow defaultApplnWorkflow = ApplicationWorkflow.builder().appId(applnDtls.getAppId())
							.applicationId(applnDtls.getApplnId())
							.versionNum((applnDtls.getVerNo() == null || applnDtls.getVerNo().trim().isEmpty()) ? 1
									: Integer.valueOf(applnDtls.getVerNo()))
							.workflowSeqNum(1).createdBy(applnDtls.getCreatedBy()).createdUserName("")
							.createTs(createDtTime).applicationStatus("INPROGRESS")
							.remarks("Cash collected from current kendra").currentRole("KM")
							.nextWorkFlowStage("CASHHANDOVER").build();
					applnWFLst.add(defaultApplnWorkflow);
					logger.debug("DefaultApplnWF::{}", defaultApplnWorkflow);
				}

				logger.debug("ApplnWFLst::{}", applnWFLst);
				applicationWorkflowRepository.saveAll(applnWFLst);
			}
			/**
			 * Add entry to cash deposit points if request contains the required fields as
			 * part of collection.
			 */
			storeCashDepositDtls(collectionRequest);
		    if (!skippedKendraIds.isEmpty()) {
		        logger.info("Total skipped due to duplicates: {} → {}", skippedKendraIds.size(), skippedKendraIds);
		    } else {
		        logger.info("No duplicates found, all kendraData processed successfully.");
		    }
			ResponseHeader respHeader = ResponseHeader.builder()
					.responseCode(com.iexceed.appzillonbanking.core.constants.CommonConstants.SUCCESS)
					.responseMessage("Application Created Successfully").build();

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
		try {
			saveMisData(collectionRequest);
		} catch (Exception e) {
			logger.error("Error while saving MIS data: {}", e.getMessage(), e);
		}
		logger.debug("Inside createApplication response::{}", response);
		return response;
	}

	@Transactional(rollbackFor = Exception.class)
	public void saveMisData(CollectionRequest collectionRequest) {
		logger.debug("Collection request received: {}", collectionRequest);
		ObjectMapper objectMapper = new ObjectMapper();

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
				int versionNum = 1;

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

					if (maxSeqObj != null && maxSeqObj.getVerNo() != null) {
						try {
							versionNum = Integer.parseInt(maxSeqObj.getVerNo());
						} catch (NumberFormatException e) {
							logger.warn("Invalid verNo '{}' for Kendra ID {}", maxSeqObj.getVerNo(),
									kendraData.getKendraId());
						}
					}
				}
				logger.debug("Vaersion num is : " + versionNum);
				String payloadJson = objectMapper.writeValueAsString(kendraData);
				logger.debug("Payload JSON for Kendra ID: {} is {}", kendraData.getKendraId(), payloadJson);

				CollectionReport report = CollectionReport.builder().kendraId(kendraData.getKendraId())
						.branchId(kendraData.getBranchId()).applicationId(kendraData.getApplnDtls().getApplnId())
						.payload(payloadJson).createdTs(LocalDateTime.now()).versionNum(versionNum).build();

				reportsToSave.add(report);

			} catch (JsonProcessingException e) {
				logger.error("Failed to serialize KendraData to JSON for Kendra ID: {}", kendraData.getKendraId(), e);
				throw new RuntimeException("JSON serialization error", e);
			} catch (Exception ex) {
				logger.error("Failed to prepare CollectionReport for Kendra ID: {}", kendraData.getKendraId(), ex);
				throw new RuntimeException("Preparing MIS data failed", ex);
			}
		}

		if (!reportsToSave.isEmpty()) {
			logger.debug("Saving {} CollectionReport records in bulk", reportsToSave.size());
			collectionReportRepository.saveAll(reportsToSave);
		}
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
		FetchCollectionResponse fetchColResp = FetchCollectionResponse.builder().kmId(kmId)
				.kmName(kendraDetailsList.get(0).getKmName()).kendraCount(kendraDetailsList.size())
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
							.findByAppIdAndApplicationIdAndKendraIdAndVersionNo(applnMst.getAppId(),
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

		Optional<ApplicationMaster> applnMaster = applicationMasterRepository
				.findByKendraIdAndApplicationDateAndAddInfoContaining(kendraId, LocalDate.parse(meetingDate),
						oppositeRole);

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
			List<ApplicationMaster> applications =
					applicationMasterRepository.findByKendraIdInAndApplicationDateAndProductCodeAndApplicationStatus(
							kendraId, meetingDate, productCode, "DISBURSED");
			if (applications.isEmpty()) {
				respBody.setResponseObj("No application found for given criteria");
				CommonUtils.generateHeaderForFailure(respHeader, "APPLICATION_NOT_FOUND");
				response.setResponseHeader(respHeader);
				response.setResponseBody(respBody);
				return response;
			}

			Map<String, List<String>> kendraPayloadMap = new HashMap<>();
			ObjectMapper mapper = new ObjectMapper();

			for (ApplicationMaster app : applications) {
				String currKendraId = app.getKendraId();
				String applicationId = app.getApplicationId();
				String payload = tbUalnLoanDtlsRepository.findPayloadByApplicationId(applicationId);

				if (payload != null) {
					try {
						JsonNode payloadJson = mapper.readTree(payload);

						// Check disburseMode == CASH
						JsonNode disburseModeNode = payloadJson.get("disburseMode");
						if (disburseModeNode != null &&
								"CASH".equalsIgnoreCase(disburseModeNode.get("idDesc").asText())) {
							kendraPayloadMap
									.computeIfAbsent(currKendraId, k -> new ArrayList<>())
									.add(payload);
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
			CommonUtils.generateHeaderForFailure(respHeader,  "INTERNAL_SERVER_ERROR");
			response.setResponseHeader(respHeader);
			response.setResponseBody(respBody);
			return response;
		}
	}

}
