package com.iexceed.appzillonbanking.cagl.incomeassesment.repository.ab;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.iexceed.appzillonbanking.cagl.incomeassesment.domain.ab.ApplicationWorkflow;
import com.iexceed.appzillonbanking.cagl.incomeassesment.domain.ab.ApplicationWorkflowId;

import java.util.List;
import java.util.Optional;

@Repository
public interface ApplicationWorkflowRepository extends CrudRepository<ApplicationWorkflow, ApplicationWorkflowId> {

	Optional<ApplicationWorkflow> findTopByAppIdAndApplicationIdAndVersionNumOrderByWorkflowSeqNumDesc(String appId, String applicationId, int versionNum);

	List<ApplicationWorkflow> findByAppIdAndApplicationIdAndApplicationStatusOrderByCreateTsAsc(String appId, String applicationId, String applicationrejectedstatus);

	List<ApplicationWorkflow> findByApplicationIdAndApplicationStatusIn(String applicationId, List<String> statusList);
}