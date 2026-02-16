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
public class CancelScheduler {

    private static final Logger logger = LogManager.getLogger(CancelScheduler.class.getName());

    @Autowired
    private ApplicationMasterRepository applicationMasterRepository;

    @Autowired
    private CbResponseStatusDao dao;

    @Autowired
    private SchedulerAuditLogRepository auditLogRepo;
    
    @Autowired
    private MisReportRepository misReportRepository;
    
    @Autowired
    private AuditTrailRepository auditTrailRepository;

    @Scheduled(cron = "${ab.common.cancelSchedulerCron}")
    public void scheduleTask() throws URISyntaxException, JsonProcessingException {
        LocalDateTime startTime = LocalDateTime.now();
        LocalDateTime endTime = null;
        int processedCount = 0;
        String status = "SUCCESS";
        String errorMessage = null;
        List<String> applicationIds = new ArrayList<>();

        try {
            logger.debug("Cancel scheduler started at {}", startTime);
            processedCount = updateApplication(applicationIds);
            logger.debug("Cancel scheduler ended.");
        } catch (Exception e) {
            status = "FAILURE";
            errorMessage = e.getMessage();
            logger.error("Exception in scheduler: ", e);
        } finally {
            endTime = LocalDateTime.now();
            SchedulerAuditLog audit = new SchedulerAuditLog();
            audit.setSchedulerName("CancelScheduler(31 Days Old Application)");
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

    private int updateApplication(List<String> applicationIds) {
        logger.debug("=========Inside updateApplication=========");
        Optional<List<ApplicationMaster>> opApplicationRecords = applicationMasterRepository.findRecords();
        logger.debug("Printing opApplicationRecords {}", opApplicationRecords);
        int processed = 0;

        if (opApplicationRecords.isPresent()) {
            List<ApplicationMaster> records = opApplicationRecords.get();
            logger.debug("Total records to process: {}", records.size());

			List<ApplicationMaster> updatedRecords = new ArrayList<>();
			List<MisReport>  updateRecordforMIS =  new ArrayList<>();
			List<AuditTrailEntity> updateRecordforAudit = new ArrayList<>();
			for (ApplicationMaster applnRec : records) {
				try {
					applnRec.setApplicationStatus("CANCELLED");
					applnRec.setRemarks("CANCELLED By Scheduler");
					updatedRecords.add(applnRec);
					applicationIds.add(applnRec.getApplicationId());
					logger.debug("Printing applnRec getApplicationId for Cancel Schedular: {}",
							applnRec.getApplicationId());
					// Audit changes for cancel Schedular
					AuditTrailEntity existingAuditTrail = auditTrailRepository
							.findApplicationId(applnRec.getApplicationId());
					logger.debug("Printing Cancel schedular for existingAuditTrail: {}", existingAuditTrail);
					Timestamp currentTimestamp = new Timestamp(System.currentTimeMillis());
					String timestampString = currentTimestamp.toString();
					AuditTrailEntity newAuditTrail = null;
					if (existingAuditTrail != null) {
						newAuditTrail = new AuditTrailEntity();
						DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
						newAuditTrail.setAppId(existingAuditTrail.getAppId());
						newAuditTrail.setApplicationId(existingAuditTrail.getApplicationId());
						newAuditTrail.setUserId("SCHEDULER");
						newAuditTrail.setUserRole("SCHEDULER");
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
						String stageId = existingAuditTrail.getStageid();
						newAuditTrail.setStageid("18");
						newAuditTrail.setAddInfo2(
								"The Loan Reject By Schedular ~30days or Retry limit cases.");
						if (stageId != null) {
							switch (stageId) {
							case "8":
							case "9":
								newAuditTrail.setStageid("18");
								newAuditTrail.setAddInfo2(
										"The Loan Reject By Schedular ~30days or Retry limit cases(BM Sanction).");
								break;
							case "12":
								newAuditTrail.setStageid("18");
								newAuditTrail.setAddInfo2(
										"The Loan Reject By Schedular ~30days or Retry limit cases(KM Disbursement).");
								break;
							case "14":
								newAuditTrail.setStageid("18");
								newAuditTrail.setAddInfo2(
										"The Loan Reject By Schedular ~30days or Retry limit cases(BM Disbursement).");
								break;
							default:
								logger.debug("Unhandled stage_id: {}", stageId);
								break;
							}
							logger.debug("Cancel Schedular Saved AuditTrailEntity before save for Audit Trail: {}",
									newAuditTrail);
							updateRecordforAudit.add(newAuditTrail);
							//auditTrailRepository.save(newAuditTrail);
						} else {
							logger.warn("Stage ID is null for applicationId: {}",
									existingAuditTrail.getApplicationId());
						}
					} else {
						logger.warn("No AuditTrailEntity found for applicationId: {}", applnRec.getApplicationId());
					}
					Optional<MisReport> optionalExisting = misReportRepository
							.findByApplicationId(applnRec.getApplicationId());
					logger.debug("Printing Cancel Schedular for Mis Report{}", optionalExisting);
					if (optionalExisting.isPresent()) {
						MisReport existingReport = optionalExisting.get();
						existingReport.setUpdateDate(timestampString);
						existingReport.setModifyBy("SCHEDULER");
						existingReport.setStageID("18");
						existingReport.setRemarks("The Loan Reject By Schedular ~30days or Retry limit cases");
						existingReport.setApplicationStatus("CANCELLED BY SCHEDULER");
						if (newAuditTrail != null) {
							existingReport.setStageID(newAuditTrail.getStageid());
						} else {
							existingReport.setStageID(null);
						}
						logger.debug("Data saved successfully after Cancel Schedular for MIS report {}",
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
			}	
			if (!updateRecordforAudit.isEmpty()) {
				auditTrailRepository.saveAll(updateRecordforAudit);
			}
			if (!updateRecordforMIS.isEmpty()) {
				misReportRepository.saveAll(updateRecordforMIS);
			}	
			processed = updatedRecords.size();
		}
        logger.debug("CancelScheduler FINISHED");
        return processed;
    }
}