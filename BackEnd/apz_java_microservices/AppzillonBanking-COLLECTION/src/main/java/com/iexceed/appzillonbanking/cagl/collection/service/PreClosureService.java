package com.iexceed.appzillonbanking.cagl.collection.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.iexceed.appzillonbanking.cagl.collection.domain.ab.PreclosureDtls;
import com.iexceed.appzillonbanking.cagl.collection.domain.ab.TbUaobAuditLogs;
import com.iexceed.appzillonbanking.cagl.collection.payload.*;
import com.iexceed.appzillonbanking.cagl.collection.repository.ab.TbUacoPrecloseDetailsRepository;
import com.iexceed.appzillonbanking.cagl.collection.repository.ab.TbUaobAuditLogsRepository;
import com.iexceed.appzillonbanking.core.payload.Header;
import com.iexceed.appzillonbanking.core.payload.Response;
import com.iexceed.appzillonbanking.core.payload.ResponseBody;
import com.iexceed.appzillonbanking.core.payload.ResponseHeader;
import com.iexceed.appzillonbanking.core.utils.CommonUtils;
import com.iexceed.appzillonbanking.interfaceAdapter.service.InterfaceAdapter;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class PreClosureService {

    @Autowired
    private InterfaceAdapter interfaceAdapter;

    @Autowired
    private TbUaobAuditLogsRepository auditLogsRepository;

    @Autowired
    private TbUacoPrecloseDetailsRepository preCloseRepo;

    private static final String PRE_CLOSURE_SIMULATE_INTF = "preClosureSimulate";
    private static final String PRE_CLOSURE_INITIATE_INTF = "cashPreClosure";
    private static final String PRE_CLOSURE_SIM_FETCH_INTF = "simulateFetch";

    private static final Logger logger = LogManager.getLogger(PreClosureService.class);

    public Mono<Response> simulatePreClosure(PreClosureSimulateApiRequest apiRequest, Header header) {

        Response response = new Response();
        ResponseHeader respHeader = new ResponseHeader();
        ResponseBody respBody = new ResponseBody();

        try {

            PreClosureSimulateRequestFields reqFields = apiRequest.getRequestObj();
            if (reqFields == null || reqFields.getArrangementId() == null ||
                    reqFields.getArrangementId().isEmpty()) {

                respBody.setResponseObj("");
                CommonUtils.generateHeaderForFailure(respHeader, "ArrangementId list is mandatory");
                response.setResponseBody(respBody);
                response.setResponseHeader(respHeader);
                return Mono.just(response);
            }
            List<String> arrangementIds = reqFields.getArrangementId();
            return Flux.fromIterable(arrangementIds)

                    .flatMap(arrangementId -> {
                        // Build per-loan request with STRING
                        PreClosureSimulateRequestFields singleReq =
                                PreClosureSimulateRequestFields.builder()
                                        .branchCode(reqFields.getBranchCode()).arrangementId(reqFields.getArrangementId())
                                        .arrangementIdStr(arrangementId).payoffDate(reqFields.getPayoffDate())
                                        .build();

                        PreClosureSimulateApiRequest newRequest =
                                PreClosureSimulateApiRequest.builder()
                                        .appId(apiRequest.getAppId()).userId(apiRequest.getUserId())
                                        .interfaceName(PRE_CLOSURE_SIMULATE_INTF).requestObj(singleReq)
                                        .build();
                        header.setInterfaceId(PRE_CLOSURE_SIMULATE_INTF);

                        return interfaceAdapter.callExternalService(
                                        header, newRequest, PRE_CLOSURE_SIMULATE_INTF, true)

                                .flatMap(simExtResponse -> {
                                    Map<String, Object> simResponse =
                                            (Map<String, Object>) simExtResponse;

                                    if ("T24Error".equalsIgnoreCase(
                                            (String) simResponse.get("successIndicator"))) {

                                        return Mono.just(buildErrorMap(
                                                arrangementId,
                                                (String) simResponse.get("messages")));
                                    }
                                    String arrId = (String) simResponse.get("ARRANGEMENT");

                                    if (arrId == null || arrId.isEmpty()) {
                                        return Mono.just(buildErrorMap(arrangementId, "Arrangement missing"));
                                    }
                                    // FETCH CALL
                                    return callFetch(apiRequest, header, arrId);
                                })
                                .onErrorResume(ex ->
                                        Mono.just(buildErrorMap(arrangementId, ex.getMessage()))
                                );

                    })
                    .collectList()
                    .map(finalList -> {
                        try {
                            String respStr = new ObjectMapper().writeValueAsString(finalList);
                            respBody.setResponseObj(respStr);
                            CommonUtils.generateHeaderForSuccess(respHeader);

                        } catch (Exception e) {
                            respBody.setResponseObj("");
                            CommonUtils.generateHeaderForFailure(respHeader, "Response conversion error");
                        }
                        response.setResponseBody(respBody);
                        response.setResponseHeader(respHeader);
                        return response;
                    });

        } catch (Exception e) {

            respBody.setResponseObj("");
            CommonUtils.generateHeaderForFailure(respHeader, "Exception occurred");
            response.setResponseBody(respBody);
            response.setResponseHeader(respHeader);
            return Mono.just(response);
        }
    }

    private Mono<Map<String, Object>> callFetch(PreClosureSimulateApiRequest apiRequest, Header header,
            String arrangementId) {

        // Build request with STRING
        PreClosureSimulateRequestFields fetchReq = PreClosureSimulateRequestFields.builder()
                        .arrangementIdStr(arrangementId).build();

        PreClosureSimulateApiRequest fetchRequest =
                PreClosureSimulateApiRequest.builder()
                        .appId(apiRequest.getAppId()).userId(apiRequest.getUserId())
                        .interfaceName(PRE_CLOSURE_SIM_FETCH_INTF).requestObj(fetchReq)
                        .build();

        header.setInterfaceId(PRE_CLOSURE_SIM_FETCH_INTF);
        return interfaceAdapter.callExternalService(
                        header, fetchRequest, PRE_CLOSURE_SIM_FETCH_INTF, true)

                .map(fetchExtResponse -> {

                    try {
                        Map<String, Object> fetchMap = (Map<String, Object>) fetchExtResponse;
                        Object body = fetchMap.get("body");
                        Map<String, Object> result = new HashMap<>();
                        result.put("data", body);

                        return result;

                    } catch (Exception e) {
                        return buildErrorMap(arrangementId, "Parsing error");
                    }
                });
    }
    private Map<String, Object> buildErrorMap(String arrangementId, String message) {

        Map<String, Object> error = new HashMap<>();
        error.put("arrangementId", arrangementId);
        error.put("error", message);

        return error;
    }

    public Mono<Response> savePrecloseDetails(PrecloseSaveRequestWrapper requestWrapper, Header header) {
        Response response = new Response();
        ResponseHeader respHeader = new ResponseHeader();
        ResponseBody respBody = new ResponseBody();

        ObjectMapper mapper = new ObjectMapper();

        try {
            List<PrecloseSaveRequestFields> requestList =
                    requestWrapper.getApiRequest().getRequestObj();
            if (requestList == null || requestList.isEmpty()) {
                throw new RuntimeException("Request list cannot be empty");
            }
            String userId = requestWrapper.getApiRequest().getUserId();
            return Flux.fromIterable(requestList)
                    .map(req -> {
                        Map<String, Object> resultMap = new HashMap<>();
                        try {
                            Map<String, Object> fullMap = mapper.convertValue(req, Map.class);

                            String kendraId = (String) fullMap.get("kendraId");
                            String branchCode = (String) fullMap.get("branchCode");
                            String meetingDateStr = (String) fullMap.get("meetingDate");
                            String customerName = (String) fullMap.get("customerName");

                            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd");
                            LocalDate parsedDate = LocalDate.parse(meetingDateStr, formatter);

                            fullMap.remove("kendraId");
                            fullMap.remove("branchCode");
                            fullMap.remove("meetingDate");
                            fullMap.remove("customerName");
                            String payloadJson = mapper.writeValueAsString(fullMap);
                            String arrangementId = (String) fullMap.get("loanAccount");

                            // Save entity
                            String appId = requestWrapper.getApiRequest().getAppId();
                            PreclosureDtls entity = new PreclosureDtls();
                            entity.setApplicationId(generateApplicationId(req.getMemberId(), arrangementId));
                            entity.setAppId(appId);
                            entity.setKendraId(kendraId);
                            entity.setBranchCode(branchCode);
                            entity.setPayload(payloadJson);
                            entity.setCustomerId(req.getMemberId());
                            entity.setStatus("INPROGRESS");
                            entity.setCreatedBy(userId);
                            entity.setCreateTs(new Timestamp(System.currentTimeMillis()));
                            entity.setMeetingDate(parsedDate);
                            entity.setCustomerName(customerName);
                            preCloseRepo.save(entity);

                            // Success response
                            resultMap.put("applicationId", entity.getApplicationId());
                            resultMap.put("arrangementId", arrangementId);
                            resultMap.put("status", "SUCCESS");
                        } catch (Exception e) {
                            resultMap.put("status", "FAILED");
                            resultMap.put("message", e.getMessage());
                        }
                        return resultMap;
                    })
                    .collectList()
                    .map(finalList -> {
                        try {
                            respBody.setResponseObj(mapper.writeValueAsString(finalList));
                            CommonUtils.generateHeaderForSuccess(respHeader);
                        } catch (Exception e) {
                            respBody.setResponseObj("");
                            CommonUtils.generateHeaderForFailure(respHeader, "Response error");
                        }
                        response.setResponseBody(respBody);
                        response.setResponseHeader(respHeader);
                        return response;
                    });
        } catch (Exception e) {
            respBody.setResponseObj("");
            CommonUtils.generateHeaderForFailure(respHeader, e.getMessage());
            response.setResponseBody(respBody);
            response.setResponseHeader(respHeader);
            return Mono.just(response);
        }
    }

    private String generateApplicationId(String customerId, String arrangementId) {
        String unique = UUID.randomUUID().toString().substring(0, 6).toUpperCase();
        return "PRE-" + customerId + "-" + arrangementId + "-" + unique;
    }

    public Response dedupeCheck(PreclosureDedupeRequest request) {
        String customerId = request.getRequestObj().getCustomerId();
        String meetingDateStr = request.getRequestObj().getMeetingDate();

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd");
        LocalDate parsedDate = LocalDate.parse(meetingDateStr, formatter);

        logger.debug("Inside preclosureDedupe => customerId: {}", customerId);
        Map<String, Object> respMap = new HashMap<>();
        Optional<PreclosureDtls> existingRecord = preCloseRepo.findTopByCustomerIdAndMeetingDate(customerId, parsedDate);

        if (existingRecord.isPresent()) {
            PreclosureDtls record = existingRecord.get();
            String message = String.format("Preclosure already exists for CustomerId %s with ApplicationId %s",
                    record.getCustomerId(), record.getApplicationId());
            respMap.put("CustomerId", record.getCustomerId());
            respMap.put("ApplicationId", record.getApplicationId());
            respMap.put("Status", record.getStatus());
            respMap.put("KendraId", record.getKendraId());
            respMap.put("MeetingDate", record.getMeetingDate().toString());
            respMap.put("StatusCode", 0);
            respMap.put("Message", message);
        } else {
            respMap.put("StatusCode", 1);
            respMap.put("Message", "No preclosure record found for given CustomerId");
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

    public Mono<Response> fetchPrecloseDetails(FetchPrecloseRequestWrapper request, Header header) {
        Response response = new Response();
        ResponseHeader respHeader = new ResponseHeader();
        ResponseBody respBody = new ResponseBody();
        try {
            String branchCode = request.getApiRequest().getRequestObj().getBranchCode();
            String meetingDateStr = request.getApiRequest().getRequestObj().getMeetingDate();
            if (branchCode == null || meetingDateStr == null) {
                respBody.setResponseObj("");
                CommonUtils.generateHeaderForFailure(respHeader, "BranchCode & MeetingDate required");
                response.setResponseHeader(respHeader);
                response.setResponseBody(respBody);
                return Mono.just(response);
            }
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd");
            LocalDate meetingDate = LocalDate.parse(meetingDateStr, formatter);

            List<PreclosureDtls> records = preCloseRepo.findByBranchCodeAndMeetingDate(branchCode, meetingDate);
            if (records.isEmpty()) {
                respBody.setResponseObj("No data found");
                CommonUtils.generateHeaderForNoResult(respHeader);
                response.setResponseHeader(respHeader);
                response.setResponseBody(respBody);
                return Mono.just(response);
            }
            // Building final response
            List<Map<String, Object>> responseList = records.stream()
                    .map(record -> {
                        Map<String, Object> map = new HashMap<>();
                        map.put("applicationId", record.getApplicationId());
                        map.put("kendraId", record.getKendraId());
                        map.put("branchCode", record.getBranchCode());
                        map.put("customerId", record.getCustomerId());
                        map.put("status", record.getStatus());
                        map.put("payload", record.getPayload());
                        map.put("createdBy", record.getCreatedBy());
                        map.put("createTs", record.getCreateTs());
                        map.put("meetingDate", record.getMeetingDate().toString());
                        map.put("customerName", record.getCustomerName());
                        return map;
                    })
                    .collect(Collectors.toList());

            String respStr = new ObjectMapper().writeValueAsString(responseList);
            respBody.setResponseObj(respStr);
            CommonUtils.generateHeaderForSuccess(respHeader);
            response.setResponseHeader(respHeader);
            response.setResponseBody(respBody);
            return Mono.just(response);
        } catch (Exception e) {
            respBody.setResponseObj("");
            CommonUtils.generateHeaderForFailure(respHeader, e.getMessage());
            response.setResponseHeader(respHeader);
            response.setResponseBody(respBody);
            return Mono.just(response);
        }
    }

    public Mono<Response> processPrecloseAction(PrecloseActionRequestWrapper closeRequest, Header header) {
        Response response = new Response();
        ResponseHeader respHeader = new ResponseHeader();
        ResponseBody respBody = new ResponseBody();
        try {
            String applicationId = closeRequest.getApiRequest().getRequestObj().getApplicationId();
            String meetingDateStr = closeRequest.getApiRequest().getRequestObj().getMeetingDate();
            String status = closeRequest.getApiRequest().getRequestObj().getStatus();
            if (applicationId == null || meetingDateStr == null || status == null) {
                respBody.setResponseObj("");
                CommonUtils.generateHeaderForFailure(respHeader, "Mandatory fields missing");
                response.setResponseHeader(respHeader);
                response.setResponseBody(respBody);
                return Mono.just(response);
            }
            LocalDate meetingDate = LocalDate.parse(meetingDateStr);
            Optional<PreclosureDtls> optionalRecord = preCloseRepo.findByApplicationIdAndMeetingDate(applicationId, meetingDate);
            if (optionalRecord.isEmpty()) {
                respBody.setResponseObj("");
                CommonUtils.generateHeaderForFailure(respHeader, "Application not found");
                response.setResponseHeader(respHeader);
                response.setResponseBody(respBody);
                return Mono.just(response);
            }
            PreclosureDtls record = optionalRecord.get();
            if ("REJECTED".equalsIgnoreCase(status)) {
                record.setStatus("REJECTED");
                preCloseRepo.save(record);

                respBody.setResponseObj("Application Rejected Successfully");
                CommonUtils.generateHeaderForSuccess(respHeader);
                response.setResponseHeader(respHeader);
                response.setResponseBody(respBody);
                return Mono.just(response);
            }
            // Audit entry
            TbUaobAuditLogs auditLog = new TbUaobAuditLogs();
            Timestamp now = new Timestamp(System.currentTimeMillis());

            auditLog.setSeqId(UUID.randomUUID().toString());
            auditLog.setApiName("cashPreClosure");
            auditLog.setAppId(header.getAppId());
            auditLog.setApplicationId(applicationId);
            auditLog.setCustDtlId(record.getCustomerId());
            auditLog.setReqTs(now);
            auditLog.setStatus("INITIATED");
            auditLog.setApiStatus("REQUEST_SENT");
            auditLog.setCreateTs(now);

            ObjectMapper mapper = new ObjectMapper();
            Map<String, Object> payloadMap = mapper.readValue(record.getPayload(), Map.class);
            if (payloadMap.containsKey("memberId")) {
                Object memberIdValue = payloadMap.remove("memberId");
                payloadMap.put("customerId", memberIdValue);
            }
            Map<String, Object> finalRequest = new HashMap<>();
            finalRequest.put("body", payloadMap);
            auditLog.setRequestPayload(mapper.writeValueAsString(finalRequest));
            auditLogsRepository.save(auditLog);

            header.setInterfaceId(PRE_CLOSURE_INITIATE_INTF);
            return interfaceAdapter.callExternalService(
                            header, finalRequest, PRE_CLOSURE_INITIATE_INTF, true)
                    .map(extResponse -> {
                        try {
                            Map<String, Object> responseMap = (Map<String, Object>) extResponse;
                            record.setStatus("APPROVED");
                            preCloseRepo.save(record);

                            auditLog.setResponsePayload(mapper.writeValueAsString(responseMap));
                            auditLog.setApiStatus("SUCCESS");
                            auditLog.setStatus("COMPLETED");
                            auditLog.setResTs(new Timestamp(System.currentTimeMillis()));
                            auditLog.setUpdateTs(new Timestamp(System.currentTimeMillis()));
                            auditLogsRepository.save(auditLog);

                            respBody.setResponseObj(mapper.writeValueAsString(responseMap));
                            CommonUtils.generateHeaderForSuccess(respHeader);
                        } catch (Exception e) {
                            record.setStatus("REJECTED");
                            preCloseRepo.save(record);

                            respBody.setResponseObj("");
                            CommonUtils.generateHeaderForFailure(respHeader, "Parsing error");
                        }
                        response.setResponseHeader(respHeader);
                        response.setResponseBody(respBody);
                        return response;
                    })
                    .onErrorResume(ex -> {
                        record.setStatus("REJECTED");
                        preCloseRepo.save(record);

                        auditLog.setResponsePayload(ex.getMessage());
                        auditLog.setApiStatus("FAILED");
                        auditLog.setStatus("ERROR");
                        auditLog.setResTs(new Timestamp(System.currentTimeMillis()));
                        auditLog.setUpdateTs(new Timestamp(System.currentTimeMillis()));
                        auditLogsRepository.save(auditLog);

                        respBody.setResponseObj("");
                        CommonUtils.generateHeaderForFailure(respHeader, ex.getMessage());
                        response.setResponseHeader(respHeader);
                        response.setResponseBody(respBody);
                        return Mono.just(response);
                    });
        } catch (Exception e) {
            respBody.setResponseObj("");
            CommonUtils.generateHeaderForFailure(respHeader, e.getMessage());
            response.setResponseHeader(respHeader);
            response.setResponseBody(respBody);
            return Mono.just(response);
        }
    }}
