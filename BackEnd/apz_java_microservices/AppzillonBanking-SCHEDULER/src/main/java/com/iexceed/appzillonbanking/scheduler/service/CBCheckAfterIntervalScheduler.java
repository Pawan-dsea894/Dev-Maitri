package com.iexceed.appzillonbanking.scheduler.service;

import java.net.URI;
import java.net.URISyntaxException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.iexceed.appzillonbanking.core.constants.CommonConstants;
import com.iexceed.appzillonbanking.scheduler.dao.CbResponseStatusDao;
import com.iexceed.appzillonbanking.scheduler.domain.ab.SchedulerAuditLog;
import com.iexceed.appzillonbanking.scheduler.model.CbCheckRequestWrapper;
import com.iexceed.appzillonbanking.scheduler.model.CbRequest;
import com.iexceed.appzillonbanking.scheduler.model.CbRequestFields;
import com.iexceed.appzillonbanking.scheduler.model.CbResponseStatus;
import com.iexceed.appzillonbanking.scheduler.repository.ab.SchedulerAuditLogRepository;

@Service
public class CBCheckAfterIntervalScheduler {

    private static final Logger logger = LoggerFactory.getLogger(CBCheckAfterIntervalScheduler.class);

    // Limit response body logs to avoid flooding and leaking PII
    private static final int LOG_BODY_LIMIT = 1500;

    // Consider moving to CommonConstants if used elsewhere
    private static final String DEVICE_ID = "WEB";

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Autowired
    private CbResponseStatusDao dao;

    @Autowired
    private RestTemplate template;

    @Autowired
    private SchedulerAuditLogRepository auditLogRepo;

    @Value("${spring.cbcheck.url}")
    private String cbUrl;

    @Value("${ab.common.cbCheckAfterIntervalSchedulerCron}")
    private String cron; // retained for visibility/reference

    private int intervalThreadPoolSize = 10;

    @Scheduled(cron = "${ab.common.cbCheckAfterIntervalSchedulerCron}")
    public void scheduleTask() {
        LocalDateTime startTime = LocalDateTime.now();
        LocalDateTime endTime = null;
        String status = "SUCCESS";
        String errorMessage = null;
        List<String> processedIds = null;
        int totalProcessed = 0;
        int success = 0;
        int failure = 0;

        logger.info("CB check (15-day interval) scheduler started at {}", startTime);

        try {
            List<CbResponseStatus> respList = dao.fetchCbCheckAfterSomeDaysApplications();

            if (respList == null || respList.isEmpty()) {
                logger.info("No applications found for CB check after interval.");
                return; // finally block still executes
            }

            processedIds = respList.stream()
                    .map(CbResponseStatus::getApplicationId)
                    .collect(Collectors.toList());

            URI url = toUri(cbUrl);

            ExecutorService executor = Executors.newFixedThreadPool(intervalThreadPoolSize);
            AtomicInteger successCount = new AtomicInteger(0);
            AtomicInteger failureCount = new AtomicInteger(0);

            for (CbResponseStatus resp : respList) {
                executor.submit(() -> sendCbIntervalRequest(resp, url, successCount, failureCount));
            }

            executor.shutdown();
            try {
            	 if (!executor.awaitTermination(58, TimeUnit.MINUTES)) {  // this will stop schduler at max 58 minutes
                    executor.shutdownNow();
                    logger.warn("Interval scheduler forced shutdown after timeout.");
                }
            } catch (InterruptedException ie) {
                executor.shutdownNow();
                Thread.currentThread().interrupt();
                logger.warn("Interval scheduler interrupted during shutdown.", ie);
            }

            totalProcessed = respList.size();
            success = successCount.get();
            failure = failureCount.get();

        } catch (Exception e) {
            status = "FAILURE";
            errorMessage = e.getMessage();
            logger.error("Exception in interval scheduler: {}", e.getMessage(), e);
        } finally {
            endTime = LocalDateTime.now();
            SchedulerAuditLog audit = new SchedulerAuditLog();
            audit.setSchedulerName("CBCheckAfterIntervalScheduler(15 Days Old CBRetrigger)");
            audit.setStartTime(startTime);
            audit.setEndTime(endTime);
            audit.setRecordCount(totalProcessed);
            audit.setStatus(status);
            audit.setErrorMessage(errorMessage);
            audit.setProcessedIds(processedIds == null ? null : String.join(",", processedIds));
            audit.setCreatedAt(LocalDateTime.now());
            auditLogRepo.save(audit);

            logger.info("CB check (15-day interval) scheduler ended at {}, Total: {}, Success: {}, Failed: {}",
                    endTime, totalProcessed, success, failure);
        }
    }

    private void sendCbIntervalRequest(CbResponseStatus resp, URI url,
                                       AtomicInteger successCount, AtomicInteger failureCount) {
        try {
            // Build request fields
            CbRequestFields fields = new CbRequestFields();
            fields.setApplicationId(resp.getApplicationId());
            fields.setVersionNo(resp.getVersionNo());
            fields.setSchedulerEnabled("Y");
            fields.setCbRecheck("Y");
            fields.setCustDtlId(resp.getMemberId());

            // Build main request
            CbRequest cbRequest = new CbRequest();
            cbRequest.setAppId(CommonConstants.APP_ID);
            cbRequest.setInterfaceName(CommonConstants.INTERFACE_NAME);
            cbRequest.setRequestObj(fields);
            cbRequest.setUserId(resp.getUserId());

            // Wrap request
            CbCheckRequestWrapper wrapper = new CbCheckRequestWrapper();
            wrapper.setApiRequest(cbRequest);

            // Headers & entity (type-safe)
            HttpHeaders headers = buildHeaders(cbRequest, fields);
            RequestEntity<CbCheckRequestWrapper> entity =
                    new RequestEntity<>(wrapper, headers, HttpMethod.POST, url);

            // Optional: request log at DEBUG
            if (logger.isDebugEnabled()) {
                logger.debug("CB Interval Request: appId={}, url={}, headers={}, payload={}",
                        fields.getApplicationId(),
                        url,
                        headers,
                        toJsonSafe(wrapper));
            }

            // Execute
            ResponseEntity<String> response = template.exchange(entity, String.class);

            // High-level success log
            logger.info("CB Interval Request success: appId={}, httpStatus={}",
                    fields.getApplicationId(), response.getStatusCode());

                logger.debug("CB Interval Response headers for appId={}: {}",
                        fields.getApplicationId(), response.getHeaders());
                logger.debug("CB Interval Response body for appId={}: {}",
                        fields.getApplicationId(), safeTruncate(response.getBody()));

            successCount.incrementAndGet();

        } catch (RestClientException rce) {
            failureCount.incrementAndGet();
            logger.error("CB Interval Request failed (HTTP): appId={}, error={}",
                    resp.getApplicationId(), rce.getMessage(), rce);
        } catch (Exception e) {
            failureCount.incrementAndGet();
            logger.error("CB Interval Request failed (unexpected): appId={}, error={}",
                    resp.getApplicationId(), e.getMessage(), e);
        }
    }

    /* -------------------- Helpers -------------------- */

    private HttpHeaders buildHeaders(CbRequest cbRequest, CbRequestFields fields) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("appId", cbRequest.getAppId());
        headers.set("interfaceId", cbRequest.getInterfaceName());
        headers.set("userId", cbRequest.getUserId());
        headers.set("masterTxnRefNo", fields.getApplicationId());
        headers.set("deviceId", DEVICE_ID);
        return headers;
    }

    private String safeTruncate(String body) {
        if (body == null) return null;
        return body.length() > LOG_BODY_LIMIT
                ? body.substring(0, LOG_BODY_LIMIT) + "...(truncated)"
                : body;
    }

    private String toJsonSafe(Object obj) {
        try {
            return objectMapper.writeValueAsString(obj);
        } catch (JsonProcessingException e) {
            return String.valueOf(obj);
        }
    }

    private URI toUri(String value) {
        try {
            return new URI(value);
        } catch (URISyntaxException e) {
            throw new IllegalArgumentException("Invalid URI for spring.cbcheck.url: " + value, e);
        }
    }
}
