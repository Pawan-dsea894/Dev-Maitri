package com.iexceed.appzillonbanking.cagl.loan.repository.ab;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.iexceed.appzillonbanking.cagl.loan.domain.ab.WorkflowDefinition;
import com.iexceed.appzillonbanking.cagl.loan.domain.ab.WorkflowDefinitionId;

import java.util.List;

@Repository
public interface WorkflowDefinitionRepository extends CrudRepository<WorkflowDefinition, WorkflowDefinitionId> {

	List<WorkflowDefinition> findByFromStageId(String nextWorkFlowStage);
}