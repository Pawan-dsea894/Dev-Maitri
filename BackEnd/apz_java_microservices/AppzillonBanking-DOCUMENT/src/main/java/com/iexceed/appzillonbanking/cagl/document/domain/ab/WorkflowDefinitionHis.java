package com.iexceed.appzillonbanking.cagl.document.domain.ab;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "TB_UAWF_WORKFLOW_DEFINITION_HISTORY")
@IdClass(WorkflowDefinitionHisId.class)
@Getter
@Setter
public class WorkflowDefinitionHis {

    @Id
    private String appId;

    @Id
    private String workFlowId;

    @Id
    private int stageSeqNum;

    @JsonProperty("fromStageId")
    @Column(name = "FROM_STAGE_ID")
    private String fromStageId;

    @JsonProperty("action")
    @Column(name = "ACTION")
    private String action;

    @JsonProperty("nextStageId")
    @Column(name = "NEXT_STAGE_ID")
    private String nextStageId;

    @JsonProperty("createTs")
    @Column(name = "CREATE_TS")
    private LocalDateTime createTs;

    @JsonProperty("ruleId")
    @Column(name = "RULE_ID")
    private String ruleId;

    @JsonProperty("currentRole")
    @Column(name = "CURRENT_ROLE")
    private String currentRole;

    @JsonProperty("nextRole")
    @Column(name = "NEXT_ROLE")
    private String nextRole;
}
