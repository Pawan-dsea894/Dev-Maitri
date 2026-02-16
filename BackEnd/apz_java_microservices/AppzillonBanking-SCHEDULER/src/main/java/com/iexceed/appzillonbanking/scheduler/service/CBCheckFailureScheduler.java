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
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.iexceed.appzillonbanking.core.constants.CommonConstants;
import com.iexceed.appzillonbanking.scheduler.dao.CbResponseStatusDao;
import com.iexceed.appzillonbanking.scheduler.domain.ab.SchedulerAuditLog;
import com.iexceed.appzillonbanking.scheduler.model.CbCheckRequestWrapper;
import com.iexceed.appzillonbanking.scheduler.model.CbRequest;
import com.iexceed.appzillonbanking.scheduler.model.CbRequestFields;
import com.iexceed.appzillonbanking.scheduler.model.CbResponseStatus;
import com.iexceed.appzillonbanking.scheduler.repository.ab.SchedulerAuditLogRepository;

@Service
public class CBCheckFailureScheduler {

    @Autowired
    CbResponseStatusDao dao;

    @Autowired
    RestTemplate template;

    @Autowired
    SchedulerAuditLogRepository auditLogRepo;
    

    Logger logger = LoggerFactory.getLogger(CBCheckFailureScheduler.class);

    @Value("${spring.cbcheck.url}")
    String cbUrl;

    @Value("${ab.common.maxRetryCount}")
    Integer maxRetryCount;

    @Scheduled(cron = "${ab.common.cbCheckFailureSchedulerCron}")
    public void scheduleTask() throws URISyntaxException, JsonProcessingException {

        LocalDateTime startTime = LocalDateTime.now();
        LocalDateTime endTime = null;
        String status = "SUCCESS";
        String errorMessage = null;
        List<String> processedIds = null;
        int totalProcessed = 0;
        int success = 0;
        int failure = 0;

        logger.info("CB check scheduler started at {}", startTime);

        try {
            int retryLimit = (maxRetryCount == null) ? 5 : maxRetryCount;
            List<CbResponseStatus> respList = dao.fetchCbCheckFailureApplications(retryLimit);

            if (respList.isEmpty()) {
                logger.info("No failed CB check applications found.");
                return;
            }

            processedIds = respList.stream()
                                    .map(CbResponseStatus::getApplicationId)
                                    .collect(Collectors.toList());

            URI url = new URI(cbUrl);
            int threadPoolSize = 5;
            ExecutorService executor = Executors.newFixedThreadPool(threadPoolSize);

            AtomicInteger successCount = new AtomicInteger(0);
            AtomicInteger failureCount = new AtomicInteger(0);

            for (CbResponseStatus resp : respList) {
                executor.submit(() -> sendCbCheckRequest(resp, url, successCount, failureCount));
            }

            executor.shutdown();
            if (!executor.awaitTermination(58, TimeUnit.MINUTES)) { // this will allow to run scheduler atleast 58 minutes
                executor.shutdownNow();
                logger.warn("Scheduler timeout: Forcing shutdown after 10 minutes.");
            }

            totalProcessed = respList.size();
            success = successCount.get();
            failure = failureCount.get();

        } catch (Exception e) {
            status = "FAILURE";
            errorMessage = e.getMessage();
            logger.error("Exception in failure scheduler: {}", e.getMessage(), e);
        } finally {
            endTime = LocalDateTime.now();
            SchedulerAuditLog audit = new SchedulerAuditLog();
            audit.setSchedulerName("CBCheckFailureScheduler( CB Retrigger Cases)");
            audit.setStartTime(startTime);
            audit.setEndTime(endTime);
            audit.setRecordCount(totalProcessed);
            audit.setStatus(status);
            audit.setErrorMessage(errorMessage);
            audit.setProcessedIds(processedIds == null ? null : String.join(",", processedIds));
            audit.setCreatedAt(LocalDateTime.now());
            auditLogRepo.save(audit);

            logger.info("CB check failure scheduler ended at {}, Total: {}, Success: {}, Failed: {}", endTime, totalProcessed, success, failure);
        }
    }

    private void sendCbCheckRequest(CbResponseStatus resp, URI url, AtomicInteger successCount,
                                    AtomicInteger failureCount) {
        try {
            CbRequestFields fields = new CbRequestFields();
            fields.setApplicationId(resp.getApplicationId());
            fields.setVersionNo(resp.getVersionNo());
            fields.setSchedulerEnabled("Y");
            fields.setCbRecheck("N");
            CbRequest cbRequest = new CbRequest();
            cbRequest.setAppId(CommonConstants.APP_ID);
            cbRequest.setInterfaceName(CommonConstants.INTERFACE_NAME);
            cbRequest.setRequestObj(fields);
            cbRequest.setUserId(resp.getUserId());

            CbCheckRequestWrapper wrapper = new CbCheckRequestWrapper();
            wrapper.setApiRequest(cbRequest);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.set("appId", cbRequest.getAppId());
            headers.set("interfaceId", cbRequest.getInterfaceName());
            headers.set("userId", cbRequest.getUserId());
            headers.set("masterTxnRefNo", fields.getApplicationId());
            headers.set("deviceId", "Android");

            RequestEntity<String> entity = new RequestEntity(wrapper, headers, HttpMethod.POST, url);

           // template.exchange(entity, String.class);
            ResponseEntity<String> response = template.exchange(entity, String.class);

            if (response != null) {
                logger.info("CB Interval Request for Application ID = {} Response: " +
                            "StatusCode={}, Headers={}, Body={}",resp.getApplicationId(),
                            response.getStatusCode(),
                            response.getHeaders(),
                            response.getBody());
            }
            
            successCount.incrementAndGet();
            logger.debug("CB check request successful for Application ID: {}", fields.getApplicationId());
        } catch (Exception e) {
            failureCount.incrementAndGet();
            logger.error("Failed CB check for Application ID: {}, Error: {}", resp.getApplicationId(), e.getMessage(),
                    e);
        }
    }
} 
