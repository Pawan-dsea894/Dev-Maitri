package com.iexceed.appzillonbanking.cagl.document.domain.ab;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "TB_UAWF_APPLN_WORKFLOW_HISTORY")
@IdClass(ApplicationWorkflowHisId.class)
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ApplicationWorkflowHis {
    @Id
    private String appId;

    @Id
    private String applicationId;

    @Id
    private int versionNum;

    @Id
    private int workflowSeqNum;

    @JsonProperty("createdBy")
    @Column(name = "CREATED_BY")
    private String createdBy;

    @JsonProperty("createTs")
    @Column(name = "CREATED_TS")
    private LocalDateTime createTs;

    @JsonProperty("applicationStatus")
    @Column(name = "APPLICATION_STATUS")
    private String applicationStatus;

    @JsonProperty("remarks")
    @Column(name = "REMARKS")
    private String remarks;

    @JsonProperty("currentRole")
    @Column(name = "PRESENT_ROLE")
    private String currentRole;

    @JsonProperty("nextWorkFlowStage")
    @Column(name = "NEXT_WORKFLOW_STAGE")
    private String nextWorkFlowStage;
}
