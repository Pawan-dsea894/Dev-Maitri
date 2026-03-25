package com.iexceed.appzillonbanking.scheduler.service;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.iexceed.appzillonbanking.scheduler.dao.CbDraftDeleteDao;
import com.iexceed.appzillonbanking.scheduler.dao.CbResponseStatusDao;
import com.iexceed.appzillonbanking.scheduler.domain.ab.ApplicationMaster;
import com.iexceed.appzillonbanking.scheduler.domain.ab.AuditTrailEntity;
import com.iexceed.appzillonbanking.scheduler.domain.ab.MisReport;
import com.iexceed.appzillonbanking.scheduler.domain.ab.SchedulerAuditLog;
import com.iexceed.appzillonbanking.scheduler.repository.ab.ApplicationMasterRepository;
import com.iexceed.appzillonbanking.scheduler.repository.ab.AuditTrailRepository;
import com.iexceed.appzillonbanking.scheduler.repository.ab.MisReportRepository;
import com.iexceed.appzillonbanking.scheduler.repository.ab.SchedulerAuditLogRepository;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.net.URISyntaxException;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class DraftUpdateSchedular {
    private static final Logger logger = LogManager.getLogger(DraftUpdateSchedular.class.getName());

    @Autowired
    private ApplicationMasterRepository applicationMasterRepository;

    @Autowired
    private CbResponseStatusDao dao;

    @Autowired
    private CbDraftDeleteDao cbDraftDeleteDao;

    @Autowired
    private MisReportRepository misReportRepository;

    @Autowired
    private AuditTrailRepository auditTrailRepository;

    @Autowired
    private SchedulerAuditLogRepository auditLogRepo;

    @Scheduled(cron = "${ab.common.draftUpdateSchedularCron}")
    public void scheduleTask() throws URISyntaxException, JsonProcessingException {
        LocalDateTime startTime = LocalDateTime.now();
        LocalDateTime endTime;
        int processedCount = 0;
        String status = "SUCCESS";
        String errorMessage = null;
        List<String> applicationIds = new ArrayList<>();

        try {
            logger.debug("Draft Schedular started at {}", startTime);
            processedCount = updateApplications(applicationIds);
            logger.debug("Draft Schedular ended.");
        } catch (Exception e) {
            status = "FAILURE";
            errorMessage = e.getMessage();
            logger.error("Exception in Draft Schedular: ", e);
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

	private int updateApplications(List<String> applicationIds) {
		logger.debug("========= Inside Update DraftApplications =========");
		Optional<List<ApplicationMaster>> opApplicationdraftRecords = cbDraftDeleteDao.findDraftApplications();
		logger.debug("Fetched records for draft update data : {}", opApplicationdraftRecords);

		if (opApplicationdraftRecords.isPresent()) {
			List<ApplicationMaster> records = opApplicationdraftRecords.get();
			logger.debug("Total records to draft update: {}", records.size());

			List<ApplicationMaster> updatedRecords = new ArrayList<>();
			List<AuditTrailEntity> updateRecordForAudit = new ArrayList<>();
			List<MisReport> updateRecordForMIS = new ArrayList<>();

			for (ApplicationMaster appMas : records) {
				try {
					applicationIds.add(appMas.getApplicationId());
					logger.debug("Preparing draft for applicationId: {}", appMas.getApplicationId());
					appMas.setApplicationStatus("CANCELLED");
					appMas.setRemarks("CANCELLED By Scheduler Draft cases..");
					updatedRecords.add(appMas);
					logger.debug("Printing appMas getApplicationId for draft Schedular: {}", appMas.getApplicationId());

					Timestamp currentTimestamp = new Timestamp(System.currentTimeMillis());
					String timestampString = currentTimestamp.toString();
					
					AuditTrailEntity existingAuditTrail = auditTrailRepository
							.findApplicationIdForDraftSchedular(appMas.getApplicationId());
					logger.debug("Printing existingAuditTrail for update draft schedular: {}", existingAuditTrail);
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
						newAuditTrail.setStageid("18");
						newAuditTrail.setAddInfo2("The Loan Reject By Schedular ~Draft Application cases..");	
						updateRecordForAudit.add(newAuditTrail);
						logger.debug("Prepared AuditTrail for applicationId {}: {}", appMas.getApplicationId(),
								newAuditTrail);
					} else {
						logger.warn("No AuditTrail found for applicationId: {}", appMas.getApplicationId());
					}

					// Update MIS Report
					Optional<MisReport> optionalExisting = misReportRepository
							.findByApplicationId(appMas.getApplicationId());
					logger.debug("Printing optionalExisting for update draft schedular: {}", optionalExisting);
					if (optionalExisting.isPresent()) {
						MisReport existingReport = optionalExisting.get();
						existingReport.setUpdateDate(timestampString);
						existingReport.setModifyBy("SCHEDULER");
						existingReport.setStageID("18");
						existingReport.setRemarks("The Loan Reject By Schedular ~Draft Application cases..");
						existingReport.setApplicationStatus("CANCELLED BY SCHEDULER");
						updateRecordForMIS.add(existingReport);
						logger.debug("Prepared MIS update for applicationId {}: {}", appMas.getApplicationId(),
								existingReport);
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
			if (!updateRecordForAudit.isEmpty()) {
				auditTrailRepository.saveAll(updateRecordForAudit);
			}
			if (!updateRecordForMIS.isEmpty()) {
				misReportRepository.saveAll(updateRecordForMIS);
			}
			logger.debug("Updated {} draft applications successfully with audit & MIS updates.", records.size());
			return records.size();
		}

		logger.debug("No applications found for deletion.");
		return 0;
	}

}
