package com.iexceed.appzillonbanking.core.repository.ab;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import com.iexceed.appzillonbanking.core.domain.ab.TbUaobApiAuditLogs;


@Repository
public interface TbUaobApiAuditLogsRepository extends CrudRepository<TbUaobApiAuditLogs, String> {
	
	List<TbUaobApiAuditLogs> findTop100BySchedulerStatusOrderByReqTsDesc(String schedulerStatus);
	
	List<TbUaobApiAuditLogs> findByApplicationIdAndCustDtlId(String applicationId, String custDtlId);
	
	@Query(value = "SELECT * FROM TB_UAOB_API_AUDIT_LOGS a " + "WHERE a.APPLICATION_ID =:applicationId "
			+ "AND a.API_NAME = 'preClosureLoan'", nativeQuery = true)
	List<TbUaobApiAuditLogs> findByApplicationId(@Param("applicationId") String applicationId);

	@Query(value = "SELECT * FROM TB_UAOB_API_AUDIT_LOGS a " + "WHERE a.APPLICATION_ID =:applicationId "
			+ "AND a.API_NAME = 'preClosureLoan' " + "AND a.STATUS = 'FAILURE'", nativeQuery = true)
	List<TbUaobApiAuditLogs> findByApplicationIdAndStatusFailure(@Param("applicationId") String applicationId);

	@Query(value = "SELECT * FROM TB_UAOB_API_AUDIT_LOGS a WHERE a.APPLICATION_ID =:applicationId AND a.STATUS ='INPROGRESS' ",nativeQuery = true)
	List<TbUaobApiAuditLogs> findByApplicationIdAndApiStatusInProgress(@Param("applicationId") String applicationId);
	
}
