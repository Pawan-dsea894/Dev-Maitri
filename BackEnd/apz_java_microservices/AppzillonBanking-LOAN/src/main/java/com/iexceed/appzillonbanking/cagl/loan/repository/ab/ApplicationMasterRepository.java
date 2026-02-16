package com.iexceed.appzillonbanking.cagl.loan.repository.ab;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.iexceed.appzillonbanking.cagl.loan.domain.ab.ApplicationMaster;
import com.iexceed.appzillonbanking.cagl.loan.domain.ab.ApplicationMasterId;
import jakarta.transaction.Transactional;

@Repository
public interface ApplicationMasterRepository extends CrudRepository<ApplicationMaster, ApplicationMasterId> {

	Optional<ApplicationMaster> findByApplicationIdAndVersionNum(String appId, String verNo);


	
/*	@Modifying
	@Transactional
	@Query(value = "UPDATE TB_UACO_APPLICATION_MASTER SET LATEST_VERSION_NO = :versionNum, APPLICATION_DATE = :applicationDate, CREATE_TS = :createTs, CREATED_BY = :createdBy, APPLICATION_TYPE = :applicationType, KYC_TYPE = :kycType, CURRENT_STAGE = :currentStage, CURRENT_SCREEN_ID = :currentScreenId, PRODUCT_CODE = :productCode, PRODUCT_GROUP_CODE = :productGroupCode, BRANCH_CODE = :branchId, CB_CHECK = :cbCheck, CUSTOMER_ID = :customerId, KENDRA_ID = :kendraId, KMID = :kmId, LEADER = :leader , LOANMODE = :loanMode WHERE APPLICATION_ID = :applicationId AND LATEST_VERSION_NO = :versionNo", nativeQuery = true)
	int updateApplicationMaster(String versionNum, LocalDate applicationDate, Timestamp createTs, String createdBy,
			String applicationType, String kycType, String currentStage, String currentScreenId, String productCode,
			String productGroupCode, String branchId, String cbCheck, String customerId, String kendraId, String kmId,
			String leader,String loanMode, String applicationId, String versionNo);*/
	 


	Optional<ApplicationMaster> findTopByAppIdAndApplicationIdOrderByVersionNumDesc(String appId, String applicationId);

	List<ApplicationMaster> findByApplicationId(String applicationId);	
	
	@Query(value= "Select * from TB_UACO_APPLICATION_MASTER  a where a.APPLICATION_ID IN (:applicationId)",nativeQuery = true)
	List<ApplicationMaster> findApplicationId(@Param("applicationId") List<String> applicationId );

	
	List<ApplicationMaster> findByCustomerId(String customerId);

	Optional<ApplicationMaster> findByAppIdAndApplicationIdAndVersionNum(String appId, String applicationId,
			String versionNum);

	List<ApplicationMaster> findByKmId(String kmId);
	 
	List<ApplicationMaster> findByApplicationIdIn(Set<String> applicationIds);
	
	List<ApplicationMaster> findByApplicationIdIn(List<String> applicationIds);

	@Query(value = "select 'sanctionApplnCnt' as status,count(application_id)  from tb_uaco_application_master where  "
			+ "		application_status='SANCTIONED' and kmid = :userId and "
			+ "		(application_type is null or application_type='LOAN') "
			+ "union all (select 'sanctionAmt' as status,sum(amount)  from tb_uaco_application_master where  "
			+ "		application_status='SANCTIONED' and kmid = :userId and "
			+ "		(application_type is null or application_type='LOAN')) "
			+ "union all (select cb.status,count(am.application_id) from tb_uaco_application_master am inner join tb_uaob_cb_response cb "
			+ "on am.application_id=cb.application_id and am.latest_version_no=cb.version_no where am.kmid = :userId group by cb.status)", nativeQuery = true)
	Object[] getMatrixData(String userId);

	Optional<ApplicationMaster> findTopByApplicationId(String applicationId);
	
	Optional<ApplicationMaster> findTopByApplicationIdOrderByCreateTsDesc(String applicationId);
	
	int deleteByApplicationId(String applicationId);

	@Query(value ="select * from TB_UACO_APPLICATION_MASTER a where a.APPLICATION_ID =:applicationId", nativeQuery = true)
	ApplicationMaster findApplicationIdBigDecimal(String applicationId);
	
	@Query(value ="select * from TB_UACO_APPLICATION_MASTER a where a.APPLICATION_ID =:applicationId ", nativeQuery = true)
	ApplicationMaster findApplicationIdInMaster(String applicationId);
	
	@Query(value = "SELECT * FROM TB_UACO_APPLICATION_MASTER a WHERE a.APPLICATION_ID =:applicationId ",nativeQuery = true)
	ApplicationMaster findApplicationIdForCRTFlow(@Param("applicationId") String applicationId);
	
	@Modifying
	@Transactional
	@Query(value = "UPDATE TB_UACO_APPLICATION_MASTER  SET APPLICATION_STATUS = :status WHERE APPLICATION_ID = :applicationId ", nativeQuery = true)
	void updateApplicationStatus(@Param("status") String status, @Param("applicationId") String applicationId);
	
	
	@Modifying
	@Transactional
	@Query(value = "UPDATE TB_UACO_APPLICATION_MASTER  SET AMOUNT = :amount WHERE APPLICATION_ID = :applicationId ", nativeQuery = true)
	void updateApplicationAmount(@Param("amount") BigDecimal amount, @Param("applicationId") String applicationId);
	
	
	@Query(value = " SELECT a.APPLICATION_ID, a.APPLICATION_STATUS, a.ADD_INFO,a.KENDRANAME,a.CUSTOMER_NAME,a.AMOUNT,a.KMID,\r\n"
			+ "  a.CUSTOMER_ID,a.CREATED_BY,a.PRODUCT_CODE,\r\n"
			+ "			           r.STATUS, r.CB_CHECK_STATUS,\r\n"
			+ "			           w.APPLICATION_STATUS , w.VERSION_NO, w.WORKFLOW_SEQ_NO,\r\n"
			+ "			           w.PRESENT_ROLE, w.NEXT_WORKFLOW_STAGE, w.REMARKS\r\n"
			+ "			    FROM TB_UACO_APPLICATION_MASTER a\r\n"
			+ "			    JOIN (\r\n"
			+ "			      SELECT DISTINCT ON (APPLICATION_ID) * \r\n"
			+ "			        FROM TB_UAOB_CB_RESPONSE \r\n"
			+ "			        ORDER BY APPLICATION_ID, REQ_TS DESC \r\n"
			+ "			    ) r ON a.APPLICATION_ID = r.APPLICATION_ID \r\n"
			+ "			    JOIN ( \r\n"
			+ "			        SELECT DISTINCT ON (APPLICATION_ID) * \r\n"
			+ "			        FROM TB_UAWF_APPLN_WORKFLOW \r\n"
			+ "			       ORDER BY APPLICATION_ID, CREATED_TS DESC  \r\n"
			+ "			    ) w ON r.APPLICATION_ID = w.APPLICATION_ID \r\n"
			+ "			   WHERE a.CREATED_BY =:userId  OR a.BRANCH_CODE =:branchId \r\n"
			+ "				ORDER BY w.APPLICATION_ID DESC", nativeQuery = true)
	List<Map<String, Object>> findLoanList(@Param("userId") String userId, @Param("branchId") String branchId);
	
	@Query(value = "select * from TB_UACO_APPLICATION_MASTER a WHERE a.APPLICATION_STATUS IN(:statuses) AND a.APPLICATION_TYPE IS NULL AND  a.CUSTOMER_ID =:customerId", nativeQuery = true)
	List<ApplicationMaster> findApplicationBasedOnCustomerId(@Param("customerId") String customerId,
			@Param("statuses") List<String> statuses);
	
	@Query(value = "select * from TB_UACO_APPLICATION_MASTER a WHERE a.CUSTOMER_ID =:customerId AND a.APPLICATION_TYPE IS NULL AND  a.APPLICATION_ID =:applicationId", nativeQuery = true)
	List<ApplicationMaster> findApplicationBasedOnCustomerIdandApplication(@Param("customerId") String customerId,
			@Param("applicationId") String applicationId);
	
	@Query(value = "select * from TB_UACO_APPLICATION_MASTER a WHERE a.APPLICATION_STATUS IN(:statuses) AND a.APPLICATION_TYPE IS NULL AND  a.APPLICATION_ID =:applicationId", nativeQuery = true)
	List<ApplicationMaster> findApplicationBasedOnApplicationId(@Param("applicationId") String applicationId,
			@Param("statuses") List<String> statuses);
	
	@Query(value = "Select * from TB_UACO_APPLICATION_MASTER  a WHERE a.APPLICATION_ID =:applicationId", nativeQuery = true)
	ApplicationMaster findApplicationID(@Param("applicationId") String applicationId);
	
}
