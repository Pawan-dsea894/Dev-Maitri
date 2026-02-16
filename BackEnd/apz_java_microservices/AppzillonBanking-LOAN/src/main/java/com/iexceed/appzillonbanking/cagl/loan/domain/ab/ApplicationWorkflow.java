package com.iexceed.appzillonbanking.cagl.loan.domain.ab;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "TB_UAWF_APPLN_WORKFLOW")
@IdClass(ApplicationWorkflowId.class)
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ApplicationWorkflow {

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

	@Override
	public String toString() {
		return "ApplicationWorkflow [appId=" + appId + ", applicationId=" + applicationId + ", versionNum=" + versionNum
				+ ", workflowSeqNum=" + workflowSeqNum + ", createdBy=" + createdBy + ", createTs=" + createTs
				+ ", applicationStatus=" + applicationStatus + ", remarks=" + remarks + ", currentRole=" + currentRole
				+ ", nextWorkFlowStage=" + nextWorkFlowStage + "]";
	}

	
}