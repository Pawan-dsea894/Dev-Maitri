package com.iexceed.appzillonbanking.scheduler.repository.ab;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.iexceed.appzillonbanking.scheduler.domain.ab.AuditTrailEntity;

@Repository
public interface AuditTrailRepository extends JpaRepository<AuditTrailEntity,Integer> {

	@Query(value = "SELECT * FROM tb_uaco_audit_trail a WHERE a.application_id =:applicationId ORDER BY create_ts DESC LIMIT 1", nativeQuery = true)
	AuditTrailEntity findApplicationIdForRejectSchedular(@Param("applicationId") String applicationId);
	

	@Query(value = "SELECT * FROM tb_uaco_audit_trail a "
			+ "	 WHERE a.create_ts = (SELECT MAX(b.create_ts) FROM tb_uaco_audit_trail b WHERE b.application_id = a.application_id) "
			+ "	  AND a.stage_id IN ('8', '9', '12', '14') AND a.application_id =:applicationId ", nativeQuery = true)
	AuditTrailEntity findApplicationId(@Param("applicationId") String applicationId);

}
