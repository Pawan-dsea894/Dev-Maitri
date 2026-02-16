package com.iexceed.appzillonbanking.cbs.repository.ab;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.iexceed.appzillonbanking.cbs.domain.ab.AuditTrailEntity;

@Repository
public interface AuditTrailRepo extends JpaRepository<AuditTrailEntity,Integer>{
	
	@Query(value = "SELECT * FROM tb_uaco_audit_trail a WHERE a.application_id =:applicationId ORDER BY create_ts DESC LIMIT 1", nativeQuery = true)
	AuditTrailEntity findApplicationId(@Param("applicationId") String applicationId);

}
