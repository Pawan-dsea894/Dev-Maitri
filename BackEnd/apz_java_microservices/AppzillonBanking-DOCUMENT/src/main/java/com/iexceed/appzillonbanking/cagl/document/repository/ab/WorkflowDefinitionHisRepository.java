package com.iexceed.appzillonbanking.cagl.document.repository.ab;

import com.iexceed.appzillonbanking.cagl.document.domain.ab.WorkflowDefinitionHis;
import com.iexceed.appzillonbanking.cagl.document.domain.ab.WorkflowDefinitionHisId;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface WorkflowDefinitionHisRepository extends CrudRepository<WorkflowDefinitionHis, WorkflowDefinitionHisId> {

    List<WorkflowDefinitionHis> findByFromStageId(String nextWorkFlowStage);
}
