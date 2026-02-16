package com.iexceed.appzillonbanking.cagl.collection.repository.ab;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.iexceed.appzillonbanking.cagl.collection.domain.ab.ApplicationWorkflow;
import com.iexceed.appzillonbanking.cagl.collection.domain.ab.ApplicationWorkflowId;

@Repository
public interface ApplicationWorkflowRepository extends CrudRepository<ApplicationWorkflow, ApplicationWorkflowId> {

	Optional<ApplicationWorkflow> findTopByAppIdAndApplicationIdAndVersionNumOrderByWorkflowSeqNumDesc(String appId,
			String applicationId, int versionNum);

	List<ApplicationWorkflow> findByAppIdAndApplicationIdAndApplicationStatusOrderByCreateTsAsc(String appId,
			String applicationId, String applicationrejectedstatus);

	List<ApplicationWorkflow> findByApplicationIdAndApplicationStatusIn(String applicationId, List<String> statusList);

	Optional<ApplicationWorkflow> findTopByAppIdAndApplicationIdOrderByWorkflowSeqNumDesc(String appId,
			String applicationId);

	List<ApplicationWorkflow> findByAppIdAndApplicationIdOrderByWorkflowSeqNumDesc(String appId, String applicationId);

	@Query("SELECT a FROM ApplicationWorkflow a WHERE a.appId IN :appIds AND a.applicationId IN :applicationIds")
	List<ApplicationWorkflow> findAllByAppIdsAndApplicationIds(@Param("appIds")List<String> appIds, @Param("applicationIds")List<String> applicationIds);

	Optional<ApplicationWorkflow> findTopByAppIdAndApplicationIdAndApplicationStatusOrderByWorkflowSeqNumDesc(String appId,String applicationId,String applicationStatus);
}
