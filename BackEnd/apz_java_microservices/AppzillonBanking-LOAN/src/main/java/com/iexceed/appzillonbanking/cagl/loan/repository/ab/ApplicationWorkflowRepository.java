package com.iexceed.appzillonbanking.cagl.loan.repository.ab;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.iexceed.appzillonbanking.cagl.loan.domain.ab.ApplicationWorkflow;
import com.iexceed.appzillonbanking.cagl.loan.domain.ab.ApplicationWorkflowId;

import jakarta.transaction.Transactional;

import java.util.List;
import java.util.Optional;

@Repository
public interface ApplicationWorkflowRepository extends CrudRepository<ApplicationWorkflow, ApplicationWorkflowId> {

	Optional<ApplicationWorkflow> findTopByAppIdAndApplicationIdAndVersionNumOrderByWorkflowSeqNumDesc(String appId, String applicationId, int versionNum);

	List<ApplicationWorkflow> findByAppIdAndApplicationIdAndApplicationStatusOrderByCreateTsAsc(String appId, String applicationId, String applicationrejectedstatus);

	List<ApplicationWorkflow> findByApplicationIdAndApplicationStatusIn(String applicationId, List<String> statusList);

	Optional<ApplicationWorkflow> findTopByAppIdAndApplicationIdOrderByWorkflowSeqNumDesc(String appId,
			String applicationId);

	Optional<ApplicationWorkflow> findTopByApplicationId(String applicationId);

	Optional<ApplicationWorkflow> findTopByApplicationIdOrderByCreateTsDesc(String applicationId);

	@Query(value = "SELECT * FROM public.tb_uawf_appln_workflow where application_id =:applicationId and application_status = 'INITDISBURSE' and created_ts =(select max(created_ts) FROM public.tb_uawf_appln_workflow where application_id =:applicationId and application_status = 'INITDISBURSE') ", nativeQuery = true)
	Optional<ApplicationWorkflow> findCreatedByUsingApplicationIdAndApplicationStatus(String applicationId);

	@Query(value = "SELECT * FROM ( SELECT DISTINCT created_by, application_id, workflow_seq_no, application_status FROM public.tb_uawf_appln_workflow WHERE application_id =:applicationId AND application_status = 'SANCTIONINPROGRESS')", nativeQuery = true)
	List<ApplicationWorkflow> findCreatedByUsingApplicationIdAndApplicationStatusForSanction(String applicationId);

	@Modifying
	@Transactional
	@Query(value = "UPDATE public.tb_uawf_appln_workflow SET application_id = :updatedApplicationId WHERE application_id = :originalApplicationId", nativeQuery = true)
	void updateApplicationId(@Param("originalApplicationId") String originalApplicationId, @Param("updatedApplicationId") String updatedApplicationId);
	
	@Query(value = "SELECT * FROM public.tb_uawf_appln_workflow a where a.application_id =:applicationId ORDER BY a.created_ts DESC LIMIT 1 ", nativeQuery = true)
	Optional<ApplicationWorkflow> findlatestWorkflowDetails(@Param("applicationId") String applicationId);
	
}