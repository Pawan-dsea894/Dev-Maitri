package com.iexceed.appzillonbanking.cagl.document.domain.ab;

import jakarta.persistence.Column;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class WorkflowDefinitionHisId implements Serializable {

        private static final long serialVersionUID = 1L;

        @Column(name = "APP_ID", nullable = false)
        private String appId;

        @Column(name = "WORKFLOW_ID", nullable = false)
        private String workFlowId;

        @Column(name = "STAGE_SEQ_NO", nullable = false)
        private int stageSeqNum;
}
