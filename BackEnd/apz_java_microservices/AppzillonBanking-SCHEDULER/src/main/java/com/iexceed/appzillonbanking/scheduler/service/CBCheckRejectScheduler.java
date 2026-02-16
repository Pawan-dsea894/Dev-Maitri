package com.iexceed.appzillonbanking.scheduler.service;

import java.net.URISyntaxException;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.iexceed.appzillonbanking.scheduler.dao.CbResponseStatusDao;
import com.iexceed.appzillonbanking.scheduler.domain.ab.ApplicationMaster;
import com.iexceed.appzillonbanking.scheduler.domain.ab.AuditTrailEntity;
import com.iexceed.appzillonbanking.scheduler.domain.ab.MisReport;
import com.iexceed.appzillonbanking.scheduler.domain.ab.SchedulerAuditLog;
import com.iexceed.appzillonbanking.scheduler.repository.ab.ApplicationMasterRepository;
import com.iexceed.appzillonbanking.scheduler.repository.ab.AuditTrailRepository;
import com.iexceed.appzillonbanking.scheduler.repository.ab.MisReportRepository;
import com.iexceed.appzillonbanking.scheduler.repository.ab.SchedulerAuditLogRepository;

@Service
public class CBCheckRejectScheduler {

    private static final Logger logger = LogManager.getLogger(CBCheckRejectScheduler.class.getName());

    @Autowired
    private ApplicationMasterRepository applicationMasterRepository;

    @Autowired
    private CbResponseStatusDao dao;

    @Autowired
    private SchedulerAuditLogRepository auditLogRepository;
    
    @Autowired
    private MisReportRepository misReportRepository;
    
    @Autowired
    private AuditTrailRepository auditTrailRepository;

    @Scheduled(cron = "${ab.common.rejectSchedulerCron}")
    public void scheduleTask() throws URISyntaxException, JsonProcessingException {
        SchedulerAuditLog auditLog = new SchedulerAuditLog();
        LocalDateTime startTime = LocalDateTime.now();
        auditLog.setSchedulerName("CBCheckRejectScheduler");
        auditLog.setStartTime(startTime);
        auditLog.setCreatedAt(startTime);
        try {
            logger.debug("Reject scheduler started at {}", startTime);
            int count = updateApplication(auditLog);
            auditLog.setStatus("SUCCESS");
            auditLog.setRecordCount(count);
        } catch (Exception e) {
            logger.error("Exception in CBCheckRejectScheduler: ", e);
            auditLog.setStatus("FAILURE");
            auditLog.setErrorMessage(e.getMessage());
        } finally {
            LocalDateTime endTime = LocalDateTime.now();
            auditLog.setEndTime(endTime);
            auditLogRepository.save(auditLog);
            logger.debug("Reject scheduler ended at {}", endTime);
        }
    }

    private int updateApplication(SchedulerAuditLog auditLog) {
        logger.debug("=========Inside updateApplication=========");
        logger.debug("STARTED RejectScheduler");
        Optional<List<ApplicationMaster>> opApplicationRecords = applicationMasterRepository.findRejectRecords();
		logger.debug("Fetch opApplicationRecords for CBCheck Reject Schedular : {}", opApplicationRecords);

        if (opApplicationRecords.isPresent()) {
            List<ApplicationMaster> records = opApplicationRecords.get();
            logger.debug("Total records to process: {}", records.size());

            List<ApplicationMaster> updatedRecords = new ArrayList<>();
            List<String> applicationIds = new ArrayList<>();
            List<MisReport>  updateRecordforMIS =  new ArrayList<>();
			List<AuditTrailEntity> updateRecordforAudit = new ArrayList<>();
            for (ApplicationMaster applnRec : records) {
                try {
                    logger.debug("Processing record {}: {}", updatedRecords.size(), applnRec);
                    applnRec.setApplicationStatus("CANCELLED");
                    applnRec.setRemarks("CANCELLED By Scheduler");
                    updatedRecords.add(applnRec);
                    applicationIds.add(applnRec.getApplicationId());
            		logger.debug("Fetch applnRec getApplicationId for CBCheck Reject Schedular : {}", applnRec.getApplicationId());

					// Audit Trail changes
					AuditTrailEntity existingAuditTrail = auditTrailRepository
							.findApplicationIdForRejectSchedular(applnRec.getApplicationId());
					logger.debug("Fetch AuditTrailEntity for CBCheck Loan Reject Schedular : {}", existingAuditTrail);
					Timestamp currentTimestamp = new Timestamp(System.currentTimeMillis());
					String timestampString = currentTimestamp.toString();
					if (existingAuditTrail != null) {
						AuditTrailEntity newAuditTrail = new AuditTrailEntity();
						DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
						newAuditTrail.setAppId(existingAuditTrail.getAppId());
						newAuditTrail.setApplicationId(existingAuditTrail.getApplicationId());
						newAuditTrail.setUserId("SCHEDULAR");
						newAuditTrail.setUserRole("SCHEDULAR");
						newAuditTrail.setLoanAmount(existingAuditTrail.getLoanAmount());
						newAuditTrail.setMobileNumber(existingAuditTrail.getMobileNumber());
						newAuditTrail.setPurpose(existingAuditTrail.getPurpose());
						newAuditTrail.setBranchId(existingAuditTrail.getBranchId());
						newAuditTrail.setProductId(existingAuditTrail.getProductId());
						newAuditTrail.setCustomerId(existingAuditTrail.getCustomerId());
						newAuditTrail.setKendraName(existingAuditTrail.getKendraName());
						newAuditTrail.setUserName(existingAuditTrail.getUserName());
						newAuditTrail.setSpouse(existingAuditTrail.getSpouse());
						newAuditTrail.setCustomerName(existingAuditTrail.getCustomerName());
						newAuditTrail.setPayload(existingAuditTrail.getPayload());
						newAuditTrail.setRepaymentFrequency(existingAuditTrail.getRepaymentFrequency());
						newAuditTrail.setAddInfo1(existingAuditTrail.getAddInfo1());
						newAuditTrail.setAddInfo3(existingAuditTrail.getAddInfo3());
						newAuditTrail.setAddInfo4(existingAuditTrail.getAddInfo4());
						newAuditTrail.setCreateTs(timestampString);
						newAuditTrail.setCreateDate(LocalDate.now().format(dateFormatter));
						newAuditTrail.setKendraId(existingAuditTrail.getKendraId());
						newAuditTrail.setAppVersion("NA");
						newAuditTrail.setStageid("18");
						newAuditTrail.setAddInfo2("The Loan Reject By Schedular ~7days interval cases(CB Failed and Queue).");
						logger.debug("CBCheck Loan Reject Schedular Saved AuditTrailEntity before save: {}",
								newAuditTrail);
						 updateRecordforAudit.add(newAuditTrail);
						//auditTrailRepository.save(newAuditTrail);
					}
					Optional<MisReport> optionalExisting = misReportRepository
							.findByApplicationId(applnRec.getApplicationId());
					logger.debug("Printing Reject Schedular for MIS report {}", optionalExisting);
					if (optionalExisting.isPresent()) {
						MisReport existingReport = optionalExisting.get();
						existingReport.setUpdateDate(timestampString);
						existingReport.setModifyBy("CBCheck Reject Schedular");
						existingReport.setUpdateDate(timestampString);
						existingReport.setModifyBy("SCHEDULER");
						existingReport.setApplicationStatus("CANCELLED BY SCHEDULER");
						existingReport.setRemarks("The Loan Reject By Schedular ~7days interval cases(CB Failed and Queue).");
						existingReport.setStageID("18");
						logger.debug("Data saved successfully after Reject Schedular for MIS report {}",
								existingReport);
						 updateRecordforMIS.add(existingReport);
						//misReportRepository.save(existingReport);
					}

				} catch (Exception e) {
					logger.error("Error while processing record {}: {}", updatedRecords.size(), e.getMessage(), e);
				}
			}
            if (!updatedRecords.isEmpty()) {
                applicationMasterRepository.saveAll(updatedRecords);
                dao.updateCBCheckStatusBulk(applicationIds);
                logger.debug("Bulk save and CB check status update completed for {} records.", updatedRecords.size());
                auditLog.setProcessedIds(String.join(",", applicationIds));
            }
			if (!updateRecordforAudit.isEmpty()) {
				auditTrailRepository.saveAll(updateRecordforAudit);
			}

			if (!updateRecordforMIS.isEmpty()) {
				misReportRepository.saveAll(updateRecordforMIS);
			}
            logger.debug("RejectScheduler FINISHED");
            return updatedRecords.size();
        }
        return 0;
    }

}
