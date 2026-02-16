package com.iexceed.appzillonbanking.kendra.repository.cus;

import java.util.List;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.iexceed.appzillonbanking.kendra.domain.cus.ApplicationWorkflow;
import com.iexceed.appzillonbanking.kendra.domain.cus.ApplicationWorkflowId;

import jakarta.transaction.Transactional;

@Repository
public interface ApplicationWorkflowRepository extends CrudRepository<ApplicationWorkflow, ApplicationWorkflowId> {

	@Modifying
	@Transactional
	@Query(value = "UPDATE TB_UAWF_APPLN_WORKFLOW " + "SET CREATED_BY = :curDeoUserId "
			+ "WHERE APPLICATION_ID IN (:applicationIds)", nativeQuery = true)
	int updateCreatedByForApplicationIds(@Param("curDeoUserId") String curDeoUserId,
			@Param("applicationIds") List<String> applicationIds);

}
