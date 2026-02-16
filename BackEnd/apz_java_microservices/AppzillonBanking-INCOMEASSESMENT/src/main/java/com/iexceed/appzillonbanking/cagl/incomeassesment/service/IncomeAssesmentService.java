package com.iexceed.appzillonbanking.cagl.incomeassesment.service;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import com.iexceed.appzillonbanking.cagl.incomeassesment.domain.ab.*;
import com.iexceed.appzillonbanking.cagl.incomeassesment.payload.*;
import com.iexceed.appzillonbanking.cagl.incomeassesment.repository.ab.*;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.google.gson.Gson;
import com.iexceed.appzillonbanking.cagl.incomeassesment.core.AppStatus;
import com.iexceed.appzillonbanking.cagl.incomeassesment.core.CommonAssessmentService;
import com.iexceed.appzillonbanking.core.payload.Header;
import com.iexceed.appzillonbanking.core.payload.Response;
import com.iexceed.appzillonbanking.core.payload.ResponseBody;
import com.iexceed.appzillonbanking.core.payload.ResponseHeader;
import com.iexceed.appzillonbanking.core.payload.ResponseWrapper;
import com.iexceed.appzillonbanking.core.utils.CommonUtils;
import com.iexceed.appzillonbanking.core.utils.FallbackUtils;
import com.iexceed.appzillonbanking.interfaceAdapter.service.InterfaceAdapter;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.micrometer.common.util.StringUtils;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import jakarta.transaction.Transactional;
import reactor.core.publisher.Mono;

@Service
public class IncomeAssesmentService {

    private static final Logger logger = LogManager.getLogger(IncomeAssesmentService.class);

    @Autowired
    private ApplicationMasterRepository applicationMasterRepo;

    @Autowired
    private InterfaceAdapter interfaceAdapter;

    @Autowired
    private CommonAssessmentService commonService;

    @Autowired
    private TbUaobOccupationDtlsRepository tbUaobOccpationDtlRepo;

    @Autowired
    private TbUaobIncomeAssessmentRepository tbUaObIncomeAssessmentRepo;

    @Autowired
    private LockCustomerService lockCustService;

    @Autowired
    private OfficeDataRepository officeDataRepository;

    @Autowired
    private TBAsmiUserRepository tBAsmiUserRepository;

    @PersistenceContext
    private EntityManager entityManager;

    @Autowired
    private IncomeQuestionaryRepository incomeQuestionaryRepository;

    public static final String EXCEPTION_MSG = "Something went wrong, Please try again!!";
    public static final String EXCEPTION_OCCURED = "Exception occurred";

    public static final String INCOME_ASSESSMENT_UPDATE_INTERFACEID = "IncomeAssessmentDedupeUpdate";
    public static final String EARNING_MEMBER_UPDATE_INTERFACEID = "EarningMemberDedupeUpdate";

    public Response saveApplication(CreateAppRequest apiRequest) {
        Response response = new Response();
        ResponseHeader respHeader = new ResponseHeader();
        ResponseBody respBody = new ResponseBody();

        ApplicationMaster appMaster = new ApplicationMaster();
        logger.debug("inside createApplication:{} ", apiRequest);
        String versionNo = "1";
        try {
            logger.debug("Inside try block ");
            appMaster.setAppId(apiRequest.getAppId());
            String loanType = "I";
			String branchId = apiRequest.getRequestObj().getBranchCode();
			String userId = apiRequest.getRequestObj().getCreatedBy();
			String applicationId = CommonUtils.generateApplicationId(loanType, branchId,userId); 
           // String applicationId = CommonUtils.generateRandomNumStr();
            logger.debug("Generated Application Id:{} ", applicationId);
            appMaster.setApplicationId(applicationId);
            apiRequest.getRequestObj().setApplicationId(applicationId);
            appMaster.setVersionNum(versionNo);
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM/dd/yyyy");
            appMaster.setApplicationDate(LocalDate.parse(apiRequest.getRequestObj().getApplicationDate(), formatter));
            appMaster.setApplicationStatus(AppStatus.PENDING.toString());
            appMaster.setCreateTs(new Timestamp(System.currentTimeMillis()));
            appMaster.setCreatedBy(apiRequest.getRequestObj().getCreatedBy());
            appMaster.setApplicationType("INCOMEASSESSMENT");
            appMaster.setKycType(apiRequest.getRequestObj().getKycType());
            appMaster.setCurrentStage(apiRequest.getRequestObj().getCurrentStage());
            appMaster.setCurrentScreenId(apiRequest.getRequestObj().getCurrentScrId());
            appMaster.setProductCode(apiRequest.getRequestObj().getProductCode());
            appMaster.setProductGroupCode(apiRequest.getRequestObj().getProductGrpCode());
            appMaster.setBranchCode(apiRequest.getRequestObj().getBranchCode());
            appMaster.setCbCheck(apiRequest.getRequestObj().getCbCheck());
            appMaster.setCustomerId(apiRequest.getRequestObj().getCustomerId());
            appMaster.setKendraId(apiRequest.getRequestObj().getKendraId());
            appMaster.setKmId(apiRequest.getRequestObj().getKmId());
            appMaster.setKendraName(apiRequest.getRequestObj().getKendraName());
            appMaster.setCustomerName(apiRequest.getRequestObj().getCustomerName());
            appMaster.setLeader(apiRequest.getRequestObj().getLeader());
            appMaster.setLoanMode(apiRequest.getRequestObj().getLoanMode());
            appMaster.setApplicationRefNo(apiRequest.getRequestObj().getApplicationRefNo());

            Gson gson = new Gson();
            if (apiRequest.getRequestObj().getAddInfo() != null) {
                String addInfoVal = gson.toJson(apiRequest.getRequestObj().getAddInfo());
                appMaster.setAddInfo(addInfoVal);
            }
            saveIncomeDetails(apiRequest, versionNo,branchId);
            applicationMasterRepo.save(appMaster);
            respBody.setResponseObj("Application details saved successfully!!");

            JSONObject responeObject = new JSONObject();
            responeObject.put("applicationId", applicationId);
            responeObject.put("versionNum", versionNo);
            responeObject.put("status", "success");
            responeObject.put("message", "Application details saved successfully!!");
            respBody.setResponseObj(responeObject.toString());
            CommonUtils.generateHeaderForSuccess(respHeader);
            response.setResponseBody(respBody);
            response.setResponseHeader(respHeader);
        } catch (Exception e) {
            logger.error("exception at createApplication: ", e);
            respBody.setResponseObj("");
            CommonUtils.generateHeaderForFailure(respHeader, EXCEPTION_OCCURED);
            response.setResponseBody(respBody);
            response.setResponseHeader(respHeader);
        }
        return response;
    }

    private TbUaobIncomeAssessment saveIncomeDetails(CreateAppRequest req, String versionNumber,String branchCode) {
        Gson gson = new Gson();
        logger.debug("inside saveIncomeDetails:{} ", req);
        TbUaobIncomeAssessment incAssessment = new TbUaobIncomeAssessment();
        incAssessment.setIncAssessmentId(CommonUtils.generateRandomNumStr());
        incAssessment.setAppId(req.getAppId());
        incAssessment.setApplicationId(req.getRequestObj().getApplicationId());
        incAssessment.setVersionNum(versionNumber);
        incAssessment.setPayload(gson.toJson(req.getRequestObj().getIncAssessmentPayload()));
        incAssessment.setCreateTs(CommonUtils.currentTimeStamp());
        incAssessment.setUpdateTs(CommonUtils.currentTimeStamp());
        incAssessment.setCreatedBy(req.getRequestObj().getCreatedBy());
        incAssessment.setUpdatedBy(req.getRequestObj().getUpdatedBy());
        tbUaObIncomeAssessmentRepo.save(incAssessment);
        return incAssessment;
    }

    @CircuitBreaker(name = "fallbackupdateStatus", fallbackMethod = "fallbackupdateStatus")
    public Response updateStatus(CreateAppRequest updateRequest, Header header) {
        logger.debug("===Inside updateStatus====");
        logger.debug("Printing updateRequest : " + updateRequest);
        Response response = new Response();
        ResponseHeader responseHeader = new ResponseHeader();
        ResponseBody responseBody = new ResponseBody();
        UpdateAssessmentRequest updateAssessmentReq = new UpdateAssessmentRequest();
        String status = updateRequest.getRequestObj().getStatus();
        updateRequest.getRequestObj().setApplicationStatus(status);
        int versionNo = 1;
        if (!(CommonUtils.isNullOrEmpty(status))) {
            Optional<ApplicationMaster> masterObjDb = applicationMasterRepo
                    .findByApplicationId(updateRequest.getRequestObj().getApplicationId());
            ApplicationMaster applicationmaster =  masterObjDb.get();
            String branchCode = applicationmaster.getBranchCode();
            logger.debug("Printing ApplicationMaster rec:" + masterObjDb);
            if (masterObjDb.isPresent()) {
                versionNo = (StringUtils.isNotBlank(masterObjDb.get().getVersionNum())
                        ? Integer.parseInt(masterObjDb.get().getVersionNum())
                        : 1) + 1;
                ApplicationMaster masterObj = masterObjDb.get();
                masterObj.setRemarks(updateRequest.getRequestObj().getRemarks());
                masterObj.setVersionNum("" + versionNo);
                Object addInfo1 = updateRequest.getRequestObj().getAddInfo();
                if (addInfo1 instanceof Map) {
                    Map<String, Object> addInfo1Map = (Map<String, Object>) addInfo1;
                    ObjectMapper objectMapper = new ObjectMapper();
                    try {
                        Map<String, Object> existingAddInfoMap = objectMapper
                                .readValue(masterObj.getAddInfo().toString(), Map.class);
                        logger.debug("existingAddInfoMap before update: " + existingAddInfoMap);
                        existingAddInfoMap.put("isRework",
                                addInfo1Map.getOrDefault("isRework", existingAddInfoMap.get("isRework")));
                        existingAddInfoMap.put("isReworkByMakerChecker", addInfo1Map.getOrDefault(
                                "isReworkByMakerChecker", existingAddInfoMap.get("isReworkByMakerChecker")));
                        existingAddInfoMap.put("isOnholdRpc",
                                addInfo1Map.getOrDefault("isOnholdRpc", existingAddInfoMap.get("isOnholdRpc")));
                        masterObj.setAddInfo(objectMapper.writeValueAsString(existingAddInfoMap));
                        logger.debug("Updated existingAddInfoMap: " + existingAddInfoMap);
                        logger.debug("Updated masterObj: " + masterObj);
                    } catch (Exception e) {
                        logger.error("Error processing addInfo JSON", e);
                    }
                }
                try {
                this.updateStatus(masterObj, status);
                }
                catch(Exception e) {}
                lockCustService.deleteApplicationLockCustomer(masterObj.getApplicationId());
                TbUaobIncomeAssessment tbIncAssessment = saveIncomeDetails(updateRequest, ("" + versionNo),branchCode);
                logger.debug("Printing tbIncAssessment rec:" + tbIncAssessment);
                CommonUtils.generateHeaderForSuccess(responseHeader);
                if (AppStatus.APPROVED.getValue().equalsIgnoreCase(status)) {
                    PopulateapplnWFRequest req = new PopulateapplnWFRequest();
                    PopulateapplnWFRequestFields reqFields = new PopulateapplnWFRequestFields();
                    reqFields.setAppId(masterObj.getAppId());
                    reqFields.setApplicationId(masterObj.getApplicationId());
                    reqFields.setCreatedBy(updateRequest.getUserId());
                    reqFields.setVersionNum(masterObj.getVersionNum());
                    reqFields.setApplicationStatus(masterObj.getApplicationStatus());
                    WorkFlowDetails wf = updateRequest.getRequestObj().getWorkFlow();
                    wf.setRemarks(wf.getRemarks());
                    reqFields.setWorkflow(wf);
                    req.setRequestObj(reqFields);
                    commonService.populateApplnWorkFlow(req);
                    updateAssessmentReq.setInterfaceName("updateIncomeAssessment");
                    updateAssessmentReq.setAppId(updateRequest.getAppId());
                    updateAssessmentReq.setUserId(updateRequest.getUserId());
                    updateAssessmentReq.setCustomerId(masterObj.getCustomerId());
                    updateAssessmentReq.setBranchId(masterObj.getBranchCode());
                    updateAssessmentReq.setQaFlag("YES");
                    updateAssessmentReq.setStatusFlag("YES");
                    updateAssessmentReq.setRecordStatus(status);
                    Optional<TbUaobOccupationDtls> occupDtlOpt = tbUaobOccpationDtlRepo
                            .findByApplicationId(masterObj.getApplicationId());
                    logger.debug("Printing occupDtlOpt rec:" + occupDtlOpt);
                    if (occupDtlOpt.isPresent()) {
                        JSONArray incomeJsonArr = new JSONArray(occupDtlOpt.get().getIncomePayload());
                        for (Object incomeJson : incomeJsonArr) {
                            JSONObject incomeJsonObj = new JSONObject(incomeJson.toString());

                            if (incomeJsonObj.has("assesmentDt")) {
                                updateAssessmentReq.setAssessmentDate(incomeJsonObj.getString("assesmentDt"));
                            }
                            if (incomeJsonObj.has("totExpense")) {
                                updateAssessmentReq.setTotExpense(incomeJsonObj.getString("totExpense"));
                            }
                            if (incomeJsonObj.has("totIncome")) {
                                updateAssessmentReq.setTotIncome(incomeJsonObj.getString("totIncome"));
                            }
                        }
                    }
//					this.interfaceAdapter.callExternalService(header, updateAssessmentReq,
//							updateAssessmentReq.getInterfaceName(), true);
                    if ("APPROVED".equalsIgnoreCase(status)) {
                        logger.debug(
                                "Status is Approved and we are calling Dedupe Update of EarningMember and IncomeAssessment");
                        updateIncomeAssessment(occupDtlOpt, masterObj, tbIncAssessment, header, updateRequest);
                        updateEarningMembers(occupDtlOpt, masterObj, tbIncAssessment, header, updateRequest);
                    }
                    lockCustService.deleteApplicationLockCustomer(masterObj.getApplicationId());
                }

            } else {
                CommonUtils.generateHeaderForNoResult(responseHeader);
            }
        } else {
            CommonUtils.generateHeaderForFailure(responseHeader, "invalid status");
        }

        JSONObject responeObject = new JSONObject();
        responeObject.put("applicationId", updateRequest.getRequestObj().getApplicationId());
        responeObject.put("versionNum", versionNo);
        responeObject.put("status", "success");
        responeObject.put("message", "Application details updated successfully!!");
        responseBody.setResponseObj(responeObject.toString());
        CommonUtils.generateHeaderForSuccess(responseHeader);
        response.setResponseBody(responseBody);
        response.setResponseHeader(responseHeader);
        return response;
    }
    
    public Response fallbackupdateStatus(CreateAppRequest updateRequest, Header header , Exception e) {
    	 // Log the exception if it occurred
        if (e != null) {
            logger.error("Exception at fallbackupdateStatus: {}", e.getMessage());
        } else {
            logger.warn("No exception information available");
        }
        
        Response response = new Response();
        ResponseHeader responseHeader = new ResponseHeader();
        ResponseBody responseBody = new ResponseBody();
        
        JSONObject responeObject = new JSONObject();
        responeObject.put("applicationId", updateRequest.getRequestObj().getApplicationId());
        responeObject.put("versionNum", "");
        responeObject.put("status", "failure");
        responeObject.put("message", "Application details updated failied!!");
        responseBody.setResponseObj(responeObject.toString());
        CommonUtils.generateHeaderForFailure(responseHeader, "Failed");
        response.setResponseBody(responseBody);
        response.setResponseHeader(responseHeader);
        return response;
        
    }

    @CircuitBreaker(name = "fallback", fallbackMethod = "approveFallback")
    public Mono<Object> approve(ApproveRequest req, Header header) {
        Response response = new Response();
        ResponseHeader responseHeader = new ResponseHeader();
        ResponseBody responseBody = new ResponseBody();
        UpdateAssessmentRequest updateAssessmentReq = new UpdateAssessmentRequest();

        Optional<TbUaobIncomeAssessment> tbIncAssessment = tbUaObIncomeAssessmentRepo
                .findByApplicationId(req.getRequestObj().getApplicationId());
        Optional<ApplicationMaster> masterObj = applicationMasterRepo
                .findByApplicationId(req.getRequestObj().getApplicationId());

        if (tbIncAssessment.isPresent()) {
            if (req.getRequestObj().getIsApproved().equalsIgnoreCase("YES")) {

                if (!(tbIncAssessment.get().getCreatedBy().equalsIgnoreCase(req.getRequestObj().getUpdatedBy()))) {

                    tbIncAssessment.get().setPayload(req.getRequestObj().getEditedResponse());
                    tbIncAssessment.get().setUpdatedBy(req.getRequestObj().getUpdatedBy());
                    tbIncAssessment.get().setUpdateTs(CommonUtils.currentTimeStamp());
                    tbUaObIncomeAssessmentRepo.save(tbIncAssessment.get());
                    updateAssessmentReq.setInterfaceName("updateIncomeAssessment");
                    updateAssessmentReq.setAppId("APZCBO");
                    updateAssessmentReq.setUserId("0000000012");
                    if (masterObj.isPresent()) {
                        updateAssessmentReq.setCustomerId(masterObj.get().getCustomerId());
                        updateAssessmentReq.setBranchId(masterObj.get().getBranchCode());
                    }
                    updateAssessmentReq.setQaFlag("YES");
                    updateAssessmentReq.setStatusFlag("YES");
                    updateAssessmentReq.setRecordStatus(req.getRequestObj().getStatus());
                    Optional<TbUaobOccupationDtls> occupDtlOpt = tbUaobOccpationDtlRepo
                            .findByApplicationId(tbIncAssessment.get().getApplicationId());
                    if (occupDtlOpt.isPresent()) {
                        JSONArray incomeJsonArr = new JSONArray(occupDtlOpt.get().getIncomePayload());
                        for (Object incomeJson : incomeJsonArr) {
                            JSONObject incomeJsonObj = new JSONObject(incomeJson.toString());

                            if (incomeJsonObj.has("assesmentDt")) {
                                updateAssessmentReq.setAssessmentDate(incomeJsonObj.getString("assesmentDt"));
                            }
                            if (incomeJsonObj.has("totExpense")) {
                                updateAssessmentReq.setTotExpense(incomeJsonObj.getString("totExpense"));
                            }
                            if (incomeJsonObj.has("totIncome")) {
                                updateAssessmentReq.setTotIncome(incomeJsonObj.getString("totIncome"));
                            }
                        }
                    }
                    return this.interfaceAdapter.callExternalService(header, updateAssessmentReq,
                            updateAssessmentReq.getInterfaceName(), true);

                }
            } else {
                if (masterObj.isPresent()) {
                    this.updateStatus(masterObj.get(), req.getRequestObj().getStatus());
                }
            }

        } else {
            CommonUtils.generateHeaderForNoResult(responseHeader);
        }

        response.setResponseBody(responseBody);
        response.setResponseHeader(responseHeader);
        return Mono.just(response);
    }

    private void updateEarningMembers(Optional<TbUaobOccupationDtls> occupDtlOpt, ApplicationMaster masterObj,
                                      TbUaobIncomeAssessment tbIncAssessment, Header header, CreateAppRequest updateRequest) {
        logger.debug("=====Inside updateEarningMembers=====");
        logger.debug("Printing incoming req for updateEarningMembers: " + updateRequest);
        EarningMemberDedupeUpdateRequestFields earningMemberDedupeUpdateRequestFields = new EarningMemberDedupeUpdateRequestFields();
        EarningMemberDedupeUpdateRequest earningMemberDedupeUpdateRequest = new EarningMemberDedupeUpdateRequest();
        String earningPayloadVal = "";

        ObjectMapper objectMapper = new ObjectMapper();
        Object incAssessmentPayload = updateRequest.getRequestObj().getIncAssessmentPayload();
        try {
            earningPayloadVal = objectMapper.writeValueAsString(incAssessmentPayload);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        JSONObject earningPayloadJson = new JSONObject(earningPayloadVal);
        logger.debug("Printing IncomeAssessment payload :" + earningPayloadJson);
        if (earningPayloadJson.has("earnings")) {
            JSONArray earningArr = earningPayloadJson.getJSONArray("earnings");
            logger.debug("payloadJson has earnings:" + earningArr.toString());
            if (earningArr.length() > 0) {
                int lengthOfEarningArr = earningArr.length();
                logger.debug("lengthOfEarningArr:" + lengthOfEarningArr);

                for (int i = 0; i < lengthOfEarningArr; i++) {
                    JSONObject earningObj = earningArr.getJSONObject(i);
                    if (earningObj.has("InputData")) {
                        JSONObject inputData = earningObj.getJSONObject("InputData");

                        logger.debug("Printing inputData:" + inputData);

                        String name = "", dob = "", memRelation = "", legaldocName = "", legaldocId = "";
                        if (inputData.has("name")) {
                            name = inputData.getString("name");
                        }
                        if (inputData.has("dob")) {
                            dob = inputData.getString("dob");
                        }
                        if (inputData.has("memRelation")) {
                            memRelation = inputData.getString("memRelation");
                        }
                        if (inputData.has("legaldocName")) {
                            legaldocName = inputData.getString("legaldocName");
                        }
                        if (inputData.has("legaldocId")) {
                            legaldocId = inputData.getString("legaldocId");
                        }

                        logger.debug("dob : " + dob);
                        SimpleDateFormat originalFormat = new SimpleDateFormat("dd/MM/yyyy");
                        SimpleDateFormat targetFormat = new SimpleDateFormat("yyyyMMdd");
                        String formattedDateStr = "";
                        try {
                            Date date = originalFormat.parse(dob);
                            formattedDateStr = targetFormat.format(date);
                            logger.debug("final dob : " + formattedDateStr);
                        } catch (Exception e) {
                        }

                        earningMemberDedupeUpdateRequestFields.setId("1.1");
                        earningMemberDedupeUpdateRequestFields.setGkv("2.0");
                        earningMemberDedupeUpdateRequestFields.setMethod("earningmember.Update");
                        earningMemberDedupeUpdateRequestFields.setRecordstatus("ACTIVE");
                        earningMemberDedupeUpdateRequestFields.setIncomeflag("NO");
                        earningMemberDedupeUpdateRequestFields.setName(name);
                        earningMemberDedupeUpdateRequestFields.setDob(formattedDateStr);
                        earningMemberDedupeUpdateRequestFields.setMemrelation(memRelation);
                        earningMemberDedupeUpdateRequestFields.setLegaldocname(legaldocName);
                        earningMemberDedupeUpdateRequestFields.setLegalid(legaldocId);
                        earningMemberDedupeUpdateRequestFields.setCustomerId(masterObj.getCustomerId());
                        earningMemberDedupeUpdateRequestFields.setBranchId(masterObj.getBranchCode());
                        earningMemberDedupeUpdateRequest.setRequestObj(earningMemberDedupeUpdateRequestFields);

                        header.setInterfaceId(EARNING_MEMBER_UPDATE_INTERFACEID);
                        logger.debug("Printing Final earningmem req:" + earningMemberDedupeUpdateRequest
                                + " headers are" + header);
                        Mono<Object> externalServiceResponse = interfaceAdapter.callExternalService(header,
                                earningMemberDedupeUpdateRequest, EARNING_MEMBER_UPDATE_INTERFACEID, true);
                        externalServiceResponse
                                .doOnNext(response -> logger
                                        .debug("Received response from external service earnMem: {}", response))
                                .subscribe();
                    }
                }
            } else {
                logger.debug("Earningm member is empty");
            }
        }
    }

    private void updateIncomeAssessment(Optional<TbUaobOccupationDtls> occupDtlOpt, ApplicationMaster masterObj,
                                        TbUaobIncomeAssessment tbIncAssessment, Header header, CreateAppRequest updateRequest) {
        logger.debug("=====Inside updateIncomeAssessment=====");
        logger.debug("Printing incoming req for updateEarningMembers: " + updateRequest);
        UpdateAssessmentDetailsRequest updateAssessmentDetailsRequest = new UpdateAssessmentDetailsRequest();
        UpdateAssessmentDetailsRequestFields updateAssessmentDetailsRequestFields = new UpdateAssessmentDetailsRequestFields();
        String incAssessmentPayloadVal = "", incomeJsonObj = "";

        updateAssessmentDetailsRequestFields.setId("1.1");
        updateAssessmentDetailsRequestFields.setGkv("2.0");
        updateAssessmentDetailsRequestFields.setMethod("incomeAssessment.Update");
        updateAssessmentDetailsRequestFields.setStatusFlag("YES");
        updateAssessmentDetailsRequestFields.setQaFlag("YES");
        updateAssessmentDetailsRequestFields.setRecordStatus("ACTIVE");
        updateAssessmentDetailsRequestFields.setCustomerId(masterObj.getCustomerId());
        updateAssessmentDetailsRequestFields.setBranchId(masterObj.getBranchCode());

        String payload = tbIncAssessment.getPayload();
        JSONObject payloadJson = new JSONObject(payload);
        logger.debug("Printing IncomeAssessment payload:" + payloadJson);

        ObjectMapper objectMapper = new ObjectMapper();
        Object incAssessmentPayload = updateRequest.getRequestObj().getIncAssessmentPayload();
        try {
            incAssessmentPayloadVal = objectMapper.writeValueAsString(incAssessmentPayload);
        } catch (JsonProcessingException e) {

            e.printStackTrace();
        }
        JSONObject incAssessmentPayloadJson = new JSONObject(incAssessmentPayloadVal);
        logger.debug("Printing incAssessmentPayloadJson:" + incAssessmentPayloadJson);
        if (incAssessmentPayloadJson.has("income")) {
            JSONArray incomeJsonArr = incAssessmentPayloadJson.getJSONArray("income");
            JSONObject incomeJson = incomeJsonArr.getJSONObject(0);

            if (incomeJson.has("assesmentDt")) {
                updateAssessmentDetailsRequestFields.setAssessmentDate(incomeJson.getString("assesmentDt"));
            }
            if (incomeJson.has("totExpense")) {
                updateAssessmentDetailsRequestFields.setTotExpense(incomeJson.getInt("totExpense") + "");
            }
            if (incomeJson.has("totIncome")) {
                updateAssessmentDetailsRequestFields.setTotIncome(incomeJson.getInt("totIncome") + "");
            }

            if (incomeJson.has("customerId")) {
                updateAssessmentDetailsRequestFields.setCustomerId(incomeJson.getInt("customerId") + "");
            }
            updateAssessmentDetailsRequest.setRequestObj(updateAssessmentDetailsRequestFields);
        }

        header.setInterfaceId(INCOME_ASSESSMENT_UPDATE_INTERFACEID);
        logger.debug("Printing Final Request updateAssessmentDetailsRequest::" + updateAssessmentDetailsRequest
                + " Headers are :" + header);
        
        Mono<Object> externalServiceResponse = interfaceAdapter.callExternalService(header,
                updateAssessmentDetailsRequest, INCOME_ASSESSMENT_UPDATE_INTERFACEID, true);
       
       // externalServiceResponse.doOnNext(
         //       response -> logger.debug("Received response from external service incomeAssessment: {}", response))
        //.subscribe();
        
        externalServiceResponse
        .timeout(Duration.ofSeconds(10)) // Set a max wait time
        .doOnNext(response -> logger.debug("Received response from external service incomeAssessment: {}", response))
        .doOnError(error -> logger.error("Error calling external service incomeAssessment", error))
        .onErrorResume(error -> {
            // Optionally return a fallback response or propagate error
            logger.warn("Using fallback or null due to error: {}", error.getMessage());
            return Mono.empty(); // or return some default Mono.just(new Object())
        })
        .subscribe(
            result -> logger.debug("Processed result: {}", result),
            error -> logger.error("Unhandled error in subscription", error)
        );
        
        
    }

    @CircuitBreaker(name = "fallback", fallbackMethod = "rejectFallback")
    public Mono<Object> reject(ApproveRequest req, Header header) {
        Response response = new Response();
        ResponseHeader responseHeader = new ResponseHeader();
        ResponseBody responseBody = new ResponseBody();

        if (req.getRequestObj().getIsRejected().equalsIgnoreCase("YES")) {

            Optional<ApplicationMaster> masterObj = applicationMasterRepo
                    .findByApplicationId(req.getRequestObj().getApplicationId());

            Optional<TbUaobIncomeAssessment> tbIncAssessment = tbUaObIncomeAssessmentRepo
                    .findByApplicationId(req.getRequestObj().getApplicationId());

            if (tbIncAssessment.isPresent()) {
                tbIncAssessment.get().setPayload(req.getRequestObj().getEditedResponse());
                tbIncAssessment.get().setUpdatedBy(req.getRequestObj().getUpdatedBy());
                tbIncAssessment.get().setUpdateTs(CommonUtils.currentTimeStamp());
                tbUaObIncomeAssessmentRepo.save(tbIncAssessment.get());
            }
            if (masterObj.isPresent()) {
                this.updateStatus(masterObj.get(), req.getRequestObj().getStatus());
            }
            responseBody.setResponseObj("status updated successfully");
            CommonUtils.generateHeaderForSuccess(responseHeader);
            response.setResponseBody(responseBody);
            response.setResponseHeader(responseHeader);
        }
        response.setResponseBody(responseBody);
        response.setResponseHeader(responseHeader);
        return Mono.just(response);
    }

    public void updateStatus(ApplicationMaster appMasterObj, String toStatus) {
        appMasterObj.setApplicationStatus(toStatus);
       
        
      //  int versionNo = (StringUtils.isNotBlank(appMasterObj.getVersionNum())
       //         ? Integer.parseInt(appMasterObj.getVersionNum())
        //        : 1) -1;
        
        try {
        applicationMasterRepo.deleteByApplicationId(appMasterObj.getApplicationId());
        }
        catch(Exception e)
        {
        	logger.debug("Exception while deleting the recode"+ e.getMessage());
        }
        
        try {
        applicationMasterRepo.save(appMasterObj);
        }
        catch(Exception e)
        {
        	logger.debug("Exception while deleting the recode"+ e.getMessage());
        }
    }

    @Transactional
    public Response fetchApplication(FetchAppRequest apiRequest) {
        Response response = new Response();
        ResponseHeader respHeader = new ResponseHeader();
        ResponseBody respBody = new ResponseBody();
        List<FetchResponsePayload> list = new ArrayList<FetchResponsePayload>();
        ObjectMapper objectMapper = new ObjectMapper().registerModule(new JavaTimeModule())
                .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        String userId = apiRequest.getRequestObj().getUserId();

        if (apiRequest.getRequestObj().getUserRole().equalsIgnoreCase("CRT")) {
            list = fetchData();
            logger.error("CRT Data execution completed ");
        } else if (apiRequest.getRequestObj().getUserRole().equalsIgnoreCase("CB_PENDING")) {
            list = fetchDataCBPending(userId);
        } else if (apiRequest.getRequestObj().getUserRole().equalsIgnoreCase("RPC")) {
            if (apiRequest.getRequestObj().getLoggedInUserRole() != null
                    && apiRequest.getRequestObj().getLoggedInUserRole().equalsIgnoreCase("KM")) {
                List<FetchResponsePayload> resp = fetchDataRPCKM(apiRequest.getRequestObj().getUserId());
                try {
                    respBody.setResponseObj(objectMapper.writeValueAsString(resp));
                } catch (JsonProcessingException e) {
                    e.printStackTrace();
                }
                response.setResponseHeader(respHeader);
                response.setResponseBody(respBody);
                return response;

            } else {
                list = fetchDataRPC();
            }

        }
        try {
            respBody.setResponseObj(objectMapper.writeValueAsString(list));
        } catch (JsonProcessingException e) {
            respBody.setResponseObj("[]");
            logger.error("exception occurred :: {}", e);
        }
        CommonUtils.generateHeaderForSuccess(respHeader);
        response.setResponseHeader(respHeader);
        response.setResponseBody(respBody);

        return response;
    }

    public Response fetchincomeDetails(JSONObject requstObject) {
        Response response = new Response();
        ResponseHeader respHeader = new ResponseHeader();
        ResponseBody respBody = new ResponseBody();
        List<FetchResponsePayload> list = new ArrayList<FetchResponsePayload>();
        ObjectMapper objectMapper = new ObjectMapper().registerModule(new JavaTimeModule())
                .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

        String applicationId = requstObject.getJSONObject("apiRequest").getJSONObject("requestObj")
                .getString("applicationId");
        JSONObject requestObjJson = requstObject.getJSONObject("apiRequest").getJSONObject("requestObj");

        String memberId = "";
        if (requestObjJson.has("memberId")) {
            memberId = requestObjJson.getString("memberId");
        }

        String versionNo = requstObject.getJSONObject("apiRequest").getJSONObject("requestObj").getString("versionNum");
        if (memberId != null && !memberId.isEmpty()) {
            String jpql = "SELECT a, i FROM ApplicationMaster a "
                    + "JOIN TbUaobIncomeAssessment i ON a.applicationId = i.applicationId "
                    + "AND a.applicationStatus = 'APPROVED' " + "AND a.versionNum = i.versionNum "
                    + "WHERE a.applicationType = 'INCOMEASSESSMENT' " + "AND a.customerId = '" + memberId + "' ";

            TypedQuery<Object[]> query = entityManager.createQuery(jpql, Object[].class);

            List<Object[]> results = query.getResultList();
            list = convertToDTO(results, true);
        } else {
            String jpql = "SELECT a, i FROM ApplicationMaster a "
                    + "JOIN TbUaobIncomeAssessment i ON a.applicationId = i.applicationId and a.versionNum=i.versionNum "
                    + "WHERE a.applicationType = 'INCOMEASSESSMENT' AND a.applicationId = '" + applicationId
                    + "' AND a.versionNum = '" + versionNo + "' ";
            TypedQuery<Object[]> query = entityManager.createQuery(jpql, Object[].class);

            List<Object[]> results = query.getResultList();
            list = convertToDTO(results, true);

        }
        try {
            respBody.setResponseObj(objectMapper.writeValueAsString(list));
        } catch (JsonProcessingException e) {
            respBody.setResponseObj("[]");
            logger.error("exception occurred :: {}", e);
        }
        CommonUtils.generateHeaderForSuccess(respHeader);
        response.setResponseHeader(respHeader);
        response.setResponseBody(respBody);

        return response;
    }

    @Transactional
    public Response fetchApplication1(FetchAppRequest apiRequest) {
        logger.debug("Enter method getting request :" + apiRequest);
        Response response = new Response();
        ResponseHeader respHeader = new ResponseHeader();
        ResponseBody respBody = new ResponseBody();
        List<FetchResponsePayload> list = new ArrayList<FetchResponsePayload>();
        ObjectMapper objectMapper = new ObjectMapper().registerModule(new JavaTimeModule())
                .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        // String userId = apiRequest.getRequestObj().getUserId();

        if (apiRequest.getRequestObj().getUserRole().equalsIgnoreCase("RPC")) {
            if (apiRequest.getRequestObj().getLoggedInUserRole() != null
                    && apiRequest.getRequestObj().getLoggedInUserRole().equalsIgnoreCase("KM")) {
                List<FetchResponsePayload> resp = fetchDataRPCKM(apiRequest.getRequestObj().getUserId());
                try {
                    respBody.setResponseObj(objectMapper.writeValueAsString(resp));
                } catch (JsonProcessingException e) {
                    e.printStackTrace();
                }
                response.setResponseHeader(respHeader);
                response.setResponseBody(respBody);
                return response;
            } else {
                try {
                    String regionId = tBAsmiUserRepository.findAddInfo2ByAddInfo1AndUserId(apiRequest.getUserId());
                    logger.debug("Individual regionId: " + regionId);
                    if (regionId != null && !regionId.isEmpty()) {
                        List<String> regionIds = Arrays.stream(regionId.split(",")).map(String::trim)
                                .collect(Collectors.toList());
                        logger.debug("Individual region Ids: " + regionIds);
                        List<String> allBranchIds = new ArrayList<>();
                        for (String region : regionIds) {
                            try {
                                List<String> branchIds = officeDataRepository.findBranchIdsByRegionId(region);
                                logger.debug("Branch IDs for region '" + region + "': " + branchIds);
                                allBranchIds.addAll(branchIds);
                            } catch (Exception innerEx) {
                                logger.error("Error fetching branch IDs for region '" + region + "': ", innerEx);
                            }
                        }
                        if (!allBranchIds.isEmpty()) {
                            try {
                                list = fetchDataRPC1(allBranchIds);
                                logger.debug("list of fetchDataRPC1: " + list);
                            } catch (Exception rpcEx) {
                                logger.error("Error fetching data via RPC for branch IDs: " + allBranchIds, rpcEx);
                            }
                        } else {
                            list = fetchDataRPC1Exiting();
                            logger.debug("list of fetchDataRPC1Exiting???: " + list);
                        }
                    } else {
                        list = fetchDataRPC1Exiting();
                        logger.debug("list of fetchDataRPC1Exiting: " + list);
                    }
                } catch (Exception e) {
                    logger.error("Error processing region names and branch IDs for userId: " + apiRequest.getUserId(),
                            e);
                }
            }
        }
        logger.debug("list are: " + list);
        try {
            respBody.setResponseObj(objectMapper.writeValueAsString(list));
        } catch (JsonProcessingException e) {
            respBody.setResponseObj("[]");
            logger.error("exception occurred :: {}", e);
        }
        CommonUtils.generateHeaderForSuccess(respHeader);
        response.setResponseHeader(respHeader);
        response.setResponseBody(respBody);
        return response;
    }

    private List<FetchResponsePayload> fetchDataRPCKM(String createdBy) {
        String jpql = "SELECT a, i FROM ApplicationMaster a JOIN TbUaobIncomeAssessment i ON a.applicationId = i.applicationId AND "
                + "a.versionNum = i.versionNum WHERE a.applicationType = 'INCOMEASSESSMENT' AND a.createdBy = :createdBy";
        TypedQuery<Object[]> query = entityManager.createQuery(jpql, Object[].class);
        query.setParameter("createdBy", createdBy);
        List<Object[]> results = query.getResultList();
        return convertToDTO(results, true);

    }

    private List<FetchResponsePayload> fetchDataRPC() {
        String jpql = "SELECT a, i FROM ApplicationMaster a "
                + "JOIN TbUaobIncomeAssessment i ON a.applicationId = i.applicationId and a.versionNum=i.versionNum "
                + "WHERE a.applicationType = 'INCOMEASSESSMENT' AND "
                + "a.applicationStatus IN ('ON HOLD', 'PENDINGBYRPC', 'MOVE TO CHECKER')";
        TypedQuery<Object[]> query = entityManager.createQuery(jpql, Object[].class);

        List<Object[]> results = query.getResultList();
        return convertToDTO(results, true);
    }

    private List<FetchResponsePayload> fetchDataRPC1(List<String> branchIds) {
        String jpql = "SELECT a, i FROM ApplicationMaster a "
                + "JOIN TbUaobIncomeAssessment i ON a.applicationId = i.applicationId and a.versionNum=i.versionNum "
                + "WHERE a.applicationType = 'INCOMEASSESSMENT' AND "
                + "a.applicationStatus IN ('ON HOLD', 'PENDINGBYRPC', 'MOVE TO CHECKER') AND a.branchCode IN (:branchIds)";
        TypedQuery<Object[]> query = entityManager.createQuery(jpql, Object[].class);
        query.setParameter("branchIds", branchIds);
        List<Object[]> results = query.getResultList();
        return convertToDTO1(results, true);
    }

    private List<FetchResponsePayload> fetchDataRPC1Exiting() {
        String jpql = "SELECT a, i FROM ApplicationMaster a "
                + "JOIN TbUaobIncomeAssessment i ON a.applicationId = i.applicationId and a.versionNum=i.versionNum "
                + "WHERE a.applicationType = 'INCOMEASSESSMENT' AND "
                + "a.applicationStatus IN ('ON HOLD', 'PENDINGBYRPC', 'MOVE TO CHECKER')";
        TypedQuery<Object[]> query = entityManager.createQuery(jpql, Object[].class);
        List<Object[]> results = query.getResultList();
        return convertToDTO1(results, true);
    }

    public List<FetchResponsePayload> fetchData() {
//		String jpql = "SELECT a, c, l FROM ApplicationMaster a JOIN TbUaobCbResponse c ON a.applicationId = c.applicationId JOIN TbUalnLoanDtls l "
//				+ "ON a.applicationId = l.applicationId WHERE(a.applicationType IS NULL OR a.applicationType = 'LOAN') AND"
//				+ " (c.status = 'FAILURE' OR c.cbCheckstatus = 'FAILURE') AND a.applicationStatus NOT IN ('DISBURSED', 'REJECTED') ";
//		String jpql = "SELECT a, c, l FROM ApplicationMaster a JOIN TbUaobCbResponse c ON a.applicationId = c.applicationId JOIN TbUalnLoanDtls l ON a.applicationId = l.applicationId"
//				+ " WHERE(a.applicationType IS NULL OR a.applicationType = 'LOAN') AND ((c.status = 'FAILURE' OR c.cbCheckstatus = 'FAILURE') AND a.applicationStatus NOT "
//				+ "IN ('DISBURSED', 'REJECTED', 'PENDING')) AND NOT (c.status = 'SUCCESS' AND c.cbCheckstatus = 'SUCCESS' AND a.applicationStatus = 'INITIATE') ";
        String jpql = "SELECT a, c " + "FROM ApplicationMaster a "
                + "JOIN TbUaobCbResponse c ON a.applicationId = c.applicationId "
                + "WHERE (a.applicationType IS NULL OR a.applicationType = 'LOAN') "
                + "AND ((c.status = 'FAILURE' OR c.cbCheckstatus = 'FAILURE') "
                + "AND a.applicationStatus NOT IN ('DISBURSED', 'REJECTED', 'PENDING')) "
                + "AND NOT (c.status = 'SUCCESS' AND c.cbCheckstatus = 'SUCCESS' "
                + "AND a.applicationStatus = 'INITIATE') ";
        // "AND c.resTs = (SELECT MAX(tucr.resTs) " +
        // "FROM TbUaobCbResponse tucr " +
        // "WHERE tucr.applicationId = a.applicationId)";

        logger.error("Going to fetch CRT data");
        TypedQuery<Object[]> query = entityManager.createQuery(jpql, Object[].class);
        query.setMaxResults(1000);
        logger.error("Fetching rows");
        List<Object[]> results = query.getResultList();
        logger.error("fetch CRT data complete");
        return convertToDTOCRT(results, false);
    }

    public List<FetchResponsePayload> fetchDataCBPending(String userId) {
        String jpql = "SELECT a, c, l FROM ApplicationMaster a "
                + "JOIN TbUaobCbResponse c ON a.applicationId = c.applicationId JOIN TbUalnLoanDtls l "
                + " ON a.applicationId = l.applicationId "
                + "WHERE (a.applicationType is null or a.applicationType='LOAN') and c.status='FAILURE' and "
                + "a.createdBy = :kendraUserId";

        TypedQuery<Object[]> query = entityManager.createQuery(jpql, Object[].class);
        query.setParameter("kendraUserId", userId);

        List<Object[]> results = query.getResultList();
        return convertToDTO(results, false);
    }

    public List<FetchResponsePayload> convertToDTO(List<Object[]> results, boolean isIncomeAssessment) {
        return results.stream().map(result -> {
            ApplicationMaster am = (ApplicationMaster) result[0];
            TbUaobCbResponse c = null;
            TbUaobIncomeAssessment i = null;
            TbUalnLoanDtls loanDtl = null;
            if (isIncomeAssessment) {
                i = (TbUaobIncomeAssessment) result[1];
            } else {
                c = (TbUaobCbResponse) result[1];
                try {
                    loanDtl = (TbUalnLoanDtls) result[2];
                } catch (Exception exp) {
                    logger.error("Error Occurred while converting the TbUalnLoanDtls :: {} ", exp);
                }
            }
            FetchResponsePayload dto = new FetchResponsePayload();
            // Set fields from ApplicationMaster
            dto.setAppId(am.getAppId());
            dto.setApplicationId(am.getApplicationId());
            dto.setLatestVersionNo(am.getVersionNum());
            dto.setAmount(am.getAmount());
            dto.setApplicationDate(am.getApplicationDate());
            dto.setApplicationRefNo(am.getApplicationRefNo());
            dto.setApplicationStatus(am.getApplicationStatus());
            dto.setApplicationType(am.getApplicationType());
            dto.setBranchCode(am.getBranchCode());
            dto.setCbCheck(am.getCbCheck());
            dto.setCreateTs(am.getCreateTs());
            dto.setCreatedBy(am.getCreatedBy());
            dto.setCurrentScreenId(am.getCurrentScreenId());
            dto.setCurrentStage(am.getCurrentStage());
            dto.setCustomerId(am.getCustomerId());
            dto.setCustomerName(am.getCustomerName());
            dto.setKendraId(am.getKendraId());
            dto.setKendraName(am.getKendraName());
            dto.setKmId(am.getKmId());
            dto.setKycType(am.getKycType());
            dto.setLeader(am.getLeader());
            dto.setLoanMode(am.getLoanMode());
            dto.setProductCode(am.getProductCode());
            dto.setProductGroupCode(am.getProductGroupCode());
            dto.setRemarks(am.getRemarks());
            dto.setAddInfo(am.getAddInfo());

            // Set fields from TbUaobCbResponse
            if (c != null) {
                dto.setReqPayload(c.getReqPayload());
                dto.setResPayload(c.getResPayload());
                dto.setStatus("FAILURE");
            }

            // Set fields from TbUaobIncomeAssessment
            if (i != null) {
                dto.setPayload(i.getPayload());
                dto.setIncCreatedBy(i.getCreatedBy());
                dto.setIncCreateTs(i.getCreateTs());
                dto.setUpdatedBy(i.getUpdatedBy());
                dto.setUpdateTs(i.getUpdateTs());
            }
            String pdtDesc = "";
            if (loanDtl != null) {
                try {
                    pdtDesc = new JSONObject(loanDtl.getPayload()).getString("shortDesc");
                } catch (Exception exp) {
                    logger.error("Error Occurred while getting the Product short Desc :: " + exp);
                }
            }
            dto.setProductshortDesc(pdtDesc);
            return dto;
        }).collect(Collectors.toList());
    }

    public List<FetchResponsePayload> convertToDTOCRT(List<Object[]> results, boolean isIncomeAssessment) {
        return results.stream().map(result -> {
            ApplicationMaster am = (ApplicationMaster) result[0];
            TbUaobCbResponse c = null;
            TbUaobIncomeAssessment i = null;
            // TbUalnLoanDtls loanDtl = null;
            if (isIncomeAssessment) {
                i = (TbUaobIncomeAssessment) result[1];
            } else {
                c = (TbUaobCbResponse) result[1];

            }
            FetchResponsePayload dto = new FetchResponsePayload();
            // Set fields from ApplicationMaster
            // dto.setAppId(am.getAppId());
            dto.setApplicationId(am.getApplicationId());
            dto.setLatestVersionNo(am.getVersionNum());
            dto.setAmount(am.getAmount());
            dto.setApplicationDate(am.getApplicationDate());
            // dto.setApplicationRefNo(am.getApplicationRefNo());
            dto.setApplicationStatus(am.getApplicationStatus());
            // dto.setApplicationType(am.getApplicationType());
            dto.setBranchCode(am.getBranchCode());
            // dto.setCbCheck(am.getCbCheck());
            dto.setCreateTs(am.getCreateTs());
            // dto.setCreatedBy(am.getCreatedBy());
            // dto.setCurrentScreenId(am.getCurrentScreenId());
            dto.setCurrentStage(am.getCurrentStage());
            dto.setCustomerId(am.getCustomerId());
            dto.setCustomerName(am.getCustomerName());
            dto.setKendraId(am.getKendraId());
            dto.setKendraName(am.getKendraName());
            dto.setKmId(am.getKmId());
            // dto.setKycType(am.getKycType());
            // dto.setLeader(am.getLeader());
            // dto.setLoanMode(am.getLoanMode());
            dto.setProductCode(am.getProductCode());
            // dto.setProductGroupCode(am.getProductGroupCode());
            // dto.setRemarks(am.getRemarks());
            dto.setAddInfo(am.getAddInfo());

            // Set fields from TbUaobCbResponse
            if (c != null) {
                dto.setReqPayload(c.getReqPayload());
                dto.setResPayload(c.getResPayload());
                // dto.setStatus("FAILURE");
            }
            String pdtDesc = am.getProductCode();
            dto.setProductshortDesc(pdtDesc);
            return dto;
        }).collect(Collectors.toList());
    }

    public List<FetchResponsePayload> convertToDTO1(List<Object[]> results, boolean isIncomeAssessment) {
        return results.stream().map(result -> {
            ApplicationMaster am = (ApplicationMaster) result[0];
            TbUaobCbResponse c = null;
            TbUaobIncomeAssessment i = null;
            TbUalnLoanDtls loanDtl = null;
            if (isIncomeAssessment) {
                i = (TbUaobIncomeAssessment) result[1];
            } else {
                c = (TbUaobCbResponse) result[1];
                try {
                    loanDtl = (TbUalnLoanDtls) result[2];
                } catch (Exception exp) {
                    logger.error("Error Occurred while converting the TbUalnLoanDtls :: {} ", exp);
                }
            }
            FetchResponsePayload dto = new FetchResponsePayload();
            // Set fields from ApplicationMaster
            dto.setAppId(am.getAppId());
            dto.setApplicationId(am.getApplicationId());
            dto.setLatestVersionNo(am.getVersionNum());
            dto.setAmount(am.getAmount());
            dto.setApplicationDate(am.getApplicationDate());
            dto.setApplicationRefNo(am.getApplicationRefNo());
            dto.setApplicationStatus(am.getApplicationStatus());
            dto.setApplicationType(am.getApplicationType());
            dto.setBranchCode(am.getBranchCode());
            dto.setCbCheck(am.getCbCheck());
            dto.setCreateTs(am.getCreateTs());
            dto.setCreatedBy(am.getCreatedBy());
            dto.setCurrentScreenId(am.getCurrentScreenId());
            dto.setCurrentStage(am.getCurrentStage());
            dto.setCustomerId(am.getCustomerId());
            dto.setCustomerName(am.getCustomerName());
            dto.setKendraId(am.getKendraId());
            dto.setKendraName(am.getKendraName());
            dto.setKmId(am.getKmId());
            dto.setKycType(am.getKycType());
            dto.setLeader(am.getLeader());
            dto.setLoanMode(am.getLoanMode());
            dto.setProductCode(am.getProductCode());
            dto.setProductGroupCode(am.getProductGroupCode());
            dto.setRemarks(am.getRemarks());
            dto.setAddInfo(am.getAddInfo());

            // Set fields from TbUaobCbResponse
            if (c != null) {
                dto.setReqPayload(c.getReqPayload());
                dto.setResPayload(c.getResPayload());
                dto.setStatus("FAILURE");
            }

            // Set fields from TbUaobIncomeAssessment
            if (i != null) {
                dto.setPayload("");
                dto.setIncCreatedBy(i.getCreatedBy());
                dto.setIncCreateTs(i.getCreateTs());
                dto.setUpdatedBy(i.getUpdatedBy());
                dto.setUpdateTs(i.getUpdateTs());
            }
            String pdtDesc = "";
            if (loanDtl != null) {
                try {
                    pdtDesc = new JSONObject(loanDtl.getPayload()).getString("shortDesc");
                } catch (Exception exp) {
                    logger.error("Error Occurred while getting the Product short Desc :: " + exp);
                }
            }
            dto.setProductshortDesc(pdtDesc);
            return dto;
        }).collect(Collectors.toList());
    }

    public Mono<Object> updateStatusFallback(UserRequest userRequest, Header header, Exception e) {
        logger.error("update status error :{} and req is:{} , header is:{} ", e, userRequest, header);
        return FallbackUtils.genericFallbackMonoObject();
    }

    public Mono<Object> approveFallback(ApproveRequest req, Header header, Exception e) {
        logger.error("approveApplicationFallback error :{} and req is:{} , header is:{} ", e, req, header);
        return FallbackUtils.genericFallbackMonoObject();
    }

    public Mono<Object> rejectFallback(ApproveRequest req, Header header, Exception e) {
        logger.error("RejectApplicationFallback error :{} and req is:{} , header is:{} ", e, req, header);
        return FallbackUtils.genericFallbackMonoObject();
    }

    public Response deleteApplication(DeleteApplicationRequest apiRequest) {
        Response response = new Response();
        ResponseHeader respHeader = new ResponseHeader();
        ResponseBody respBody = new ResponseBody();
        JSONObject responeObject = new JSONObject();

        String applicationId = apiRequest.getRequestObj().getApplicationId();
        logger.debug("applicationId to delete record is :" + applicationId);

        Optional<ApplicationMaster> opApplicationMasterRec = applicationMasterRepo.findByApplicationId(applicationId);
        Optional<List<TbUaobIncomeAssessment>> opIncomeAssessmentRec = tbUaObIncomeAssessmentRepo
                .findByApplicationIdList(applicationId);

        if (opApplicationMasterRec.isPresent() && opIncomeAssessmentRec.isPresent()) {
            ApplicationMaster applicationMasterRecord = opApplicationMasterRec.get();
            List<TbUaobIncomeAssessment> tbUaobIncomeAssessmentRecord = opIncomeAssessmentRec.get();
            this.deleteAllApplicationInMaster(applicationMasterRecord);
//			applicationMasterRepo.delete(applicationMasterRecord);
            
            try {
            tbUaObIncomeAssessmentRepo.deleteAll(tbUaobIncomeAssessmentRecord);
            }
            catch(Exception e) {}

            responeObject.put("status", "success");
            responeObject.put("message", "Record Deleted Successfully!!!");
            respBody.setResponseObj(responeObject.toString());
            CommonUtils.generateHeaderForSuccess(respHeader);
            response.setResponseBody(respBody);
            response.setResponseHeader(respHeader);
            return response;
        }

        responeObject.put("status", "Failure");
        responeObject.put("message", "Application Not found");
        respBody.setResponseObj(responeObject.toString());
        CommonUtils.generateHeaderForFailure(respHeader, "Record not present to delete");
        response.setResponseBody(respBody);
        response.setResponseHeader(respHeader);
        return response;
    }

    private void deleteAllApplicationInMaster(ApplicationMaster applicationMasterRecord) {
        logger.debug("Delete All ApplicationMasterRec on basis of customerId INITIATED");
        String customerId = applicationMasterRecord.getCustomerId();
        logger.debug("CustomerId " + customerId);
        List<ApplicationMaster> allApplnMasterRecods = applicationMasterRepo.findAllRecods(customerId);
        logger.debug("ApplicationMaster Records" + allApplnMasterRecods);
        try {
        if (!allApplnMasterRecods.isEmpty()) {
            applicationMasterRepo.deleteAll(allApplnMasterRecods);
            logger.debug("All Records on basis of customerId is DELETED");
        } else {
            logger.debug("No record for applicationId");
        }
        }
        catch(Exception e)
        {
        	
        }
    }

    @CircuitBreaker(name = "fallbackcallupdateEarningMemberService", fallbackMethod = "fallbackcallupdateEarningMemberService")
    public Response callupdateEarningMemberService(UpdateEarningRequest updateRequest, Header header) {
        Response response = new Response();
        ResponseHeader responseHeader = new ResponseHeader();
        ResponseBody responseBody = new ResponseBody();
        logger.debug("Inside callupdateEarningMemberService");

        UpdateEarningRequestFields earningMemberDedupeUpdateRequestFields = new UpdateEarningRequestFields();
        UpdateEarningRequest earningMemberDedupeUpdateRequest = new UpdateEarningRequest();

        earningMemberDedupeUpdateRequestFields.setId("1.1");
        earningMemberDedupeUpdateRequestFields.setGkv("2.0");
        earningMemberDedupeUpdateRequestFields.setMethod("earningmember.Update");
        earningMemberDedupeUpdateRequestFields.setRecordstatus(updateRequest.getRequestObj().getRecordstatus());
        earningMemberDedupeUpdateRequestFields.setIncomeflag(updateRequest.getRequestObj().getIncomeflag());
        earningMemberDedupeUpdateRequestFields.setName(updateRequest.getRequestObj().getName());
        earningMemberDedupeUpdateRequestFields.setDob(updateRequest.getRequestObj().getDob());
        earningMemberDedupeUpdateRequestFields.setMemrelation(updateRequest.getRequestObj().getMemrelation());
        earningMemberDedupeUpdateRequestFields.setLegaldocname(updateRequest.getRequestObj().getLegaldocname());
        earningMemberDedupeUpdateRequestFields.setLegalid(updateRequest.getRequestObj().getLegalid());
        earningMemberDedupeUpdateRequestFields.setCustomerId(updateRequest.getRequestObj().getCustomerId());
        earningMemberDedupeUpdateRequestFields.setBranchId(updateRequest.getRequestObj().getBranchId());
        earningMemberDedupeUpdateRequest.setRequestObj(earningMemberDedupeUpdateRequestFields);

        header.setInterfaceId(EARNING_MEMBER_UPDATE_INTERFACEID);
        logger.debug("Printing Final earningmem req:" + earningMemberDedupeUpdateRequest + " headers are" + header);
        Mono<Object> externalServiceResponse = interfaceAdapter.callExternalService(header,
                earningMemberDedupeUpdateRequest, EARNING_MEMBER_UPDATE_INTERFACEID, true);
        externalServiceResponse
                .doOnNext(resp -> logger.debug("Received response from external service earnMem: {}", resp))
                .subscribe();

        responseHeader.setResponseMessage("Successfully Updated Earning Member");
        responseBody.setResponseObj("Earning member Updated Successfully!!!");
        response.setResponseBody(responseBody);
        return response;
    }

    
    public Response fallbackcallupdateEarningMemberService(UpdateEarningRequest updateRequest, Header header , Exception e) {
		
    	 Response response = new Response();
         ResponseHeader responseHeader = new ResponseHeader();
         ResponseBody responseBody = new ResponseBody();
         logger.debug("Inside fallbackcallupdateEarningMemberService");
         logger.error("Inside fallbackcallupdateEarningMemberService");
         
        // Log the exception if it occurred
        if (e != null) {
            logger.error("Exception at fetchCustomerPayOffDetail: {}", e.getMessage());
        } else {
            logger.warn("No exception information available");
        }
        
        responseHeader.setResponseMessage("Failed to update Member");
        responseBody.setResponseObj("Failed to update Member!!!");
        response.setResponseBody(responseBody);
        logger.error("response :" + response);
        return response;
}

    
    

    @Transactional
    public Response fetchDataForCRTService(FetchDataRequest apiRequest) {
        logger.debug("Enter method of fetch data " + apiRequest);
        Response response = new Response();
        ResponseHeader respHeader = new ResponseHeader();
        ResponseBody respBody = new ResponseBody();

        List<FetchResponsePayload> list = new ArrayList<FetchResponsePayload>();
        ObjectMapper objectMapper = new ObjectMapper().registerModule(new JavaTimeModule())
                .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        String filterValue = apiRequest.getRequestObj().getFiltervalue();
        logger.debug("filterValue:" + filterValue);
        try {
            if (apiRequest.getRequestObj().getUserRole().equalsIgnoreCase("CRT")) {
                list = fetchDataOnyCRT(filterValue);
            }
            try {
                respBody.setResponseObj(objectMapper.writeValueAsString(list));
            } catch (JsonProcessingException e) {
                logger.error("Error while setting response object: {}", e.getMessage(), e);
                respBody.setResponseObj("[]");
            }
            CommonUtils.generateHeaderForSuccess(respHeader);
            response.setResponseHeader(respHeader);
            response.setResponseBody(respBody);

        } catch (Exception e) {
            logger.error("Exception in fetchDataForCRTService: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to fetch data", e);
        }
        return response;
    }

    public List<FetchResponsePayload> fetchDataOnyCRT(String filterValue) {
        logger.error("Executing query with filterValue: ", filterValue);
        logger.debug("Executing query with filterValue: " + filterValue);

        String jpql = "SELECT a, c " + "FROM ApplicationMaster a "
                + "JOIN TbUaobCbResponse c ON a.applicationId = c.applicationId "
                + "WHERE (a.applicationType IS NULL OR a.applicationType = 'LOAN') "
                + "AND ((c.status = 'FAILURE' OR c.cbCheckstatus = 'FAILURE') "
                + "AND a.applicationStatus NOT IN ('DISBURSED', 'REJECTED', 'PENDING')) "
                + "AND NOT (c.status = 'SUCCESS' AND c.cbCheckstatus = 'SUCCESS' "
                + "AND a.applicationStatus = 'INITIATE') " + "AND (LOWER(a.customerId) = LOWER(:filterValue) "
                + "    OR LOWER(a.customerName) = LOWER(:filterValue) "
                + "    OR LOWER(a.branchCode) = LOWER(:filterValue) "
                + "    OR LOWER(a.addInfo) LIKE LOWER('%' || '\"branchName\":\"' || :filterValue || '%'))";
        
        logger.debug("Query generated: " + jpql);
        logger.error("Going to fetch CRT data");
        
        String formattedQuery = jpql.replace(":filterValue", "'" + filterValue + "'");
        logger.debug("Executing Query with Values: {}" +formattedQuery);
        logger.debug("Executing Query with Values: {}" , formattedQuery);
        
        TypedQuery<Object[]> query = entityManager.createQuery(jpql, Object[].class);
        query.setParameter("filterValue", filterValue);
        logger.info("Executing Query: {} with filterValue: {}", jpql, filterValue);
        List<Object[]> results = query.getResultList();
        logger.debug("Enter method of fetch data " + results);
        logger.error("Fetched {} rows", results.size());
        logger.error("fetch CRT data complete");

        if (!results.isEmpty()) {
            logger.error("First result: {}", Arrays.toString(results.get(0)));
        }
        return convertToDTOCRTData(results);
    }

    public List<FetchResponsePayload> convertToDTOCRTData(List<Object[]> results) {
        return results.stream().map(result -> {
            ApplicationMaster am = (ApplicationMaster) result[0];
            TbUaobCbResponse c = null;
            if (result.length > 1 && result[1] instanceof TbUaobCbResponse) {
                c = (TbUaobCbResponse) result[1];
            }
            FetchResponsePayload dto = new FetchResponsePayload();
            dto.setApplicationId(am.getApplicationId());
            dto.setLatestVersionNo(am.getVersionNum());
            dto.setAmount(am.getAmount());
            dto.setApplicationDate(am.getApplicationDate());
            dto.setApplicationStatus(am.getApplicationStatus());
            dto.setBranchCode(am.getBranchCode());
            dto.setCreateTs(am.getCreateTs());
            dto.setCurrentStage(am.getCurrentStage());
            dto.setCustomerId(am.getCustomerId());
            dto.setCustomerName(am.getCustomerName());
            dto.setKendraId(am.getKendraId());
            dto.setKendraName(am.getKendraName());
            dto.setKmId(am.getKmId());
            dto.setProductCode(am.getProductCode());
            dto.setAddInfo(am.getAddInfo());
            if (c != null) {
                dto.setReqPayload(c.getReqPayload());
                dto.setResPayload(c.getResPayload());
            }
            dto.setProductshortDesc(am.getProductCode());
            return dto;
        }).collect(Collectors.toList());
    }

	@Transactional
	public Response saveQuestionary(CreateIncomeRequest apiRequest) {
		logger.debug("inside saveQuestionary: {}", apiRequest);
		Response response = new Response();
		ResponseHeader respHeader = new ResponseHeader();
		ResponseBody respBody = new ResponseBody();
		try {
			TbUacoIncomeQuestionary tbUacoIncomeQuestionary = incomeQuestionaryRepository
					.findByApplicationId(apiRequest.getRequestObj().getApplicationId())
					.map(existing -> updateExistingQuestionary(existing, apiRequest))
					.orElseGet(() -> createNewQuestionary(apiRequest));
			logger.debug("tbUacoIncomeQuestionary: " + tbUacoIncomeQuestionary);
			incomeQuestionaryRepository.save(tbUacoIncomeQuestionary);
			respBody.setResponseObj("Questionary details saved successfully!!");
			JSONObject responseObject = new JSONObject();
			responseObject.put("applicationId", apiRequest.getRequestObj().getApplicationId());
			responseObject.put("status", "success");
			responseObject.put("message", "Questionary details saved successfully!!");
			respBody.setResponseObj(responseObject.toString());
			CommonUtils.generateHeaderForSuccess(respHeader);
			response.setResponseBody(respBody);
			response.setResponseHeader(respHeader);
		} catch (Exception e) {
			logger.error("Exception at saveQuestionary: ", e);
			respBody.setResponseObj("");
			CommonUtils.generateHeaderForFailure(respHeader, EXCEPTION_OCCURED);
			response.setResponseBody(respBody);
			response.setResponseHeader(respHeader);
		}
		return response;
	}

	private TbUacoIncomeQuestionary updateExistingQuestionary(TbUacoIncomeQuestionary existing,
			CreateIncomeRequest apiRequest) {
		logger.debug("existing: " + existing);
		logger.debug("apiRequest: " + apiRequest);
		TbUacoIncomeQuestionary questionary = mapCommonFields(existing, apiRequest);
		logger.debug("questionary: " + questionary);
		questionary.setUpdatedBy(apiRequest.getRequestObj().getUpdatedBy());
		questionary.setUpdatedTs(new Timestamp(System.currentTimeMillis()));
		questionary.setCreatedTs(existing.getCreatedTs());
		questionary.setCreatedBy(existing.getCreatedBy());
		updateAdditionalInfo(questionary, apiRequest.getRequestObj().getHouseHoldDetails(), "Update");
		return questionary;
	}

	private TbUacoIncomeQuestionary createNewQuestionary(CreateIncomeRequest apiRequest) {
		TbUacoIncomeQuestionary questionary = mapCommonFields(new TbUacoIncomeQuestionary(), apiRequest);
		questionary.setCreatedTs(new Timestamp(System.currentTimeMillis()));
		questionary.setCreatedBy(apiRequest.getRequestObj().getUserId());
		questionary.setCustomerId(apiRequest.getRequestObj().getCustomerID());
		updateAdditionalInfo(questionary, apiRequest.getRequestObj().getHouseHoldDetails(), "Insert");
		return questionary;

	}

	private TbUacoIncomeQuestionary mapCommonFields(TbUacoIncomeQuestionary questionary,
			CreateIncomeRequest apiRequest) {
		questionary.setAppId(apiRequest.getAppId());
		questionary.setApplicationId(apiRequest.getRequestObj().getApplicationId());
		questionary.setUserId(apiRequest.getUserId());
		questionary.setLoanId(apiRequest.getRequestObj().getLoanId());
		questionary.setCustomerId(apiRequest.getRequestObj().getCustomerID());
		questionary.setUpdatedBy(apiRequest.getRequestObj().getUpdatedBy());
		return questionary;
	}

	private void updateAdditionalInfo(TbUacoIncomeQuestionary questionary, HouseHoldDetails houseHoldDetails,
			String questionFlag) {
		try {
			ObjectMapper objectMapper = new ObjectMapper();
			String addInfo = houseHoldDetails != null ? new Gson().toJson(houseHoldDetails) : "{}";
			JsonNode addInfoJson = objectMapper.readTree(addInfo);
			addInfo = (addInfo != null && !addInfo.isEmpty()) ? addInfo : "{}";
			questionary.setAddInfo(objectMapper.writeValueAsString(addInfoJson));
			Optional<ApplicationMaster> optionalApplicationMaster = applicationMasterRepo
					.findByApplicationId(questionary.getLoanId());
			if (optionalApplicationMaster.isPresent()) {
				ApplicationMaster applicationMaster = optionalApplicationMaster.get();
				String existingAddInfo = applicationMaster.getAddInfo();
				existingAddInfo = (existingAddInfo != null && !existingAddInfo.isEmpty()) ? existingAddInfo : "{}";
				JsonNode existingAddInfoJson = objectMapper.readTree(existingAddInfo);
				if (existingAddInfoJson.isObject()) {
					((ObjectNode) existingAddInfoJson).put("isQuestionComplete", questionFlag);
				}
				applicationMaster.setAddInfo(objectMapper.writeValueAsString(existingAddInfoJson));
				logger.debug("applicationMaster  data fetch : " + applicationMaster);
				applicationMasterRepo.save(applicationMaster);
			} else {
				logger.error("ApplicationMaster not found for Loan ID: " + questionary.getLoanId());
			}

		} catch (JsonProcessingException e) {
			logger.error("Error updating Income Questionary JSON", e);
		}
	}

}
