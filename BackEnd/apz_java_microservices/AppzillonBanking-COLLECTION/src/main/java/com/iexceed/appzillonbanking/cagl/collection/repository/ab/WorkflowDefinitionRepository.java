package com.iexceed.appzillonbanking.cagl.collection.repository.ab;

import java.util.List;
import java.util.Optional;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.iexceed.appzillonbanking.cagl.collection.domain.ab.WorkflowDefinition;
import com.iexceed.appzillonbanking.cagl.collection.domain.ab.WorkflowDefinitionId;

@Repository
public interface WorkflowDefinitionRepository extends CrudRepository<WorkflowDefinition, WorkflowDefinitionId> {

	List<WorkflowDefinition> findByFromStageId(String fromStageId);
	
	Optional<WorkflowDefinition> findByAppIdAndStageSeqNumAndFromStageIdAndWorkFlowId(String appId, int stageSeqNo, String fromStageId, String workflowId);
	
	
	List<WorkflowDefinition> findByAppIdAndCurrentRole(String appId, String currentRole);
}