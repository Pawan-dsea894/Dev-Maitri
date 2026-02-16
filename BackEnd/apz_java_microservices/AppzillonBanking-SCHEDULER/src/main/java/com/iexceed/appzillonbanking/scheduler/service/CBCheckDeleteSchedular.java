package com.iexceed.appzillonbanking.scheduler.service;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.iexceed.appzillonbanking.scheduler.dao.CbDraftDeleteDao;
import com.iexceed.appzillonbanking.scheduler.dao.CbResponseStatusDao;
import com.iexceed.appzillonbanking.scheduler.domain.ab.ApplicationMaster;
import com.iexceed.appzillonbanking.scheduler.domain.ab.SchedulerAuditLog;
import com.iexceed.appzillonbanking.scheduler.repository.ab.ApplicationMasterRepository;
import com.iexceed.appzillonbanking.scheduler.repository.ab.SchedulerAuditLogRepository;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.net.URISyntaxException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class CBCheckDeleteSchedular {
    private static final Logger logger = LogManager.getLogger(CBCheckDeleteSchedular.class.getName());

    @Autowired
    private ApplicationMasterRepository applicationMasterRepository;

    @Autowired
    private CbResponseStatusDao dao;

    @Autowired
    private CbDraftDeleteDao cbDraftDeleteDao;

    @Autowired
    private SchedulerAuditLogRepository auditLogRepo;

    @Scheduled(cron = "${ab.common.CBCheckDeleteSchedular}")
    public void scheduleTask() throws URISyntaxException, JsonProcessingException {
        LocalDateTime startTime = LocalDateTime.now();
        LocalDateTime endTime;
        int processedCount = 0;
        String status = "SUCCESS";
        String errorMessage = null;
        List<String> applicationIds = new ArrayList<>();

        try {
            logger.debug("CBCheckDeleteSchedular started at {}", startTime);
            processedCount = deleteApplications(applicationIds);
            logger.debug("CBCheckDeleteSchedular ended.");
        } catch (Exception e) {
            status = "FAILURE";
            errorMessage = e.getMessage();
            logger.error("Exception in CBCheckDeleteSchedular: ", e);
        } finally {
            endTime = LocalDateTime.now();
            SchedulerAuditLog audit = new SchedulerAuditLog();
            audit.setSchedulerName("One Day old Draft Application");
            audit.setStartTime(startTime);
            audit.setEndTime(endTime);
            audit.setRecordCount(processedCount);
            audit.setStatus(status);
            audit.setErrorMessage(errorMessage);
            audit.setProcessedIds(applicationIds.isEmpty() ? null : String.join(",", applicationIds));
            audit.setCreatedAt(LocalDateTime.now());
            auditLogRepo.save(audit);
        }
    }

    private int deleteApplications(List<String> applicationIds) {
        logger.debug("========= Inside deleteDraftApplications =========");
        Optional<List<ApplicationMaster>> opApplicationdraftRecords = cbDraftDeleteDao.findDraftApplications();
        logger.debug("Fetched records for draft delete: {}", opApplicationdraftRecords);

        if (opApplicationdraftRecords.isPresent()) {
            List<ApplicationMaster> records = opApplicationdraftRecords.get();
            logger.debug("Total records to draft delete: {}", records.size());

            for (ApplicationMaster appln : records) {
                applicationIds.add(appln.getApplicationId());
                logger.debug("Deleting applicationId: {}", appln.getApplicationId());
            }

            // Bulk delete
            applicationMasterRepository.deleteAll(records);
            dao.updateCBCheckStatusBulk(applicationIds);

            logger.debug("Deleted {} applications successfully.", records.size());
            return records.size();
        }

        logger.debug("No applications found for deletion.");
        return 0;
    }

}
