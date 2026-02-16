package com.iexceed.appzillonbanking.cagl.collection.repository.ab;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.iexceed.appzillonbanking.cagl.collection.domain.ab.ApplicationMaster;
import com.iexceed.appzillonbanking.cagl.collection.domain.ab.ApplicationMasterId;

@Repository
public interface ApplicationMasterRepository extends CrudRepository<ApplicationMaster, ApplicationMasterId> {

	Optional<ApplicationMaster> findTopByAppIdAndApplicationIdAndKendraIdAndBranchCodeOrderByVersionNumDesc(
			String appId, String applicationId, String kendraId, String branchCode);

	List<ApplicationMaster> findByBranchCodeAndApplicationDateAndApplicationTypeAndKmIdInOrderByVersionNumDesc(
			String branchCode, LocalDate applnDate, String applnType, List<String> kmIds);

	List<ApplicationMaster> findByBranchCodeAndApplicationTypeAndKmIdInOrderByVersionNumDesc(String branchCode,
			String applnType, List<String> kmIds);

	List<ApplicationMaster> findByAppIdAndApplicationIdAndVersionNumAndKendraIdInOrderByVersionNumDesc(String appId,
			String applicationId, String versionNo, List<String> kendraId);

	List<ApplicationMaster> findByAppIdAndVersionNumAndKendraIdInAndApplicationTypeNotOrderByVersionNumDesc(
			String appId, String versionNo, List<String> kendraId, String applnType);

	List<ApplicationMaster> findByAppIdAndVersionNumAndKendraIdInAndApplicationTypeNotAndApplicationStatusNotOrderByVersionNumDesc(
			String appId, String versionNo, List<String> kendraId, String applnType, String applnStatus);

	List<ApplicationMaster> findByAppIdAndApplicationIdAndVersionNumAndKendraIdInAndApplicationTypeNotAndApplicationStatusNotOrderByVersionNumDesc(
			String appId, String applicationId, String versionNo, List<String> kendraId, String applnType,
			String applnStatus);

	Optional<ApplicationMaster> findTopByAppIdAndApplicationIdAndKendraIdOrderByVersionNumDesc(String appId,
			String applicationId, String kendraId);

	List<ApplicationMaster> findByBranchCodeAndApplicationType(String branchCode, String applnType);

	Optional<ApplicationMaster> findTopByAppIdAndApplicationIdAndKendraIdAndVersionNum(String appId,
			String applicationId, String kendraId, String versionNo);

	List<ApplicationMaster> findByAppIdAndApplicationTypeAndCreatedByOrderByApplicationDateDesc(String appId,
			String applnType, String createdBy);

	Optional<ApplicationMaster> findTopByAppIdAndCreatedByAndApplicationIdAndApplicationTypeAndKendraIdAndBranchCodeOrderByVersionNumDesc(
			String appId, String userId, String applicationId, String applicationType, String kendraId,
			String branchId);

	List<ApplicationMaster> findByKendraIdAndApplicationDate(String kendraId, LocalDate applicationDate);

	List<ApplicationMaster> findByKendraIdInAndApplicationDateAndProductCodeAndApplicationStatus(List<String> kendraIds,
			LocalDate applicationDate, String productCode, String applicationStatus);

	Optional<ApplicationMaster> findByKendraIdAndApplicationDateAndAddInfoContaining(String kendraId,
			LocalDate applicationDate, String addInfo);

}
