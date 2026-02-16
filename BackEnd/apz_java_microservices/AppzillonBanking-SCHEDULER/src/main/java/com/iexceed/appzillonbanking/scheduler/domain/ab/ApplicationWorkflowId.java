package com.iexceed.appzillonbanking.scheduler.domain.ab;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

import jakarta.persistence.Column;

@Getter @Setter
public class ApplicationWorkflowId implements Serializable {

	private static final long serialVersionUID = 1L;
	
	@Column(name = "APP_ID", nullable = false)
	private String appId;

	@Column(name = "APPLICATION_ID", nullable = false)
	private String applicationId;

	@Column(name = "VERSION_NO", nullable = false)
	private int versionNum;

	@Column(name = "WORKFLOW_SEQ_NO", nullable = false)
	private int workflowSeqNum;

	public ApplicationWorkflowId(String appId, String applicationId, int versionNum, int workflowSeqNum) {
		this.appId = appId;
		this.applicationId = applicationId;
		this.versionNum = versionNum;
		this.workflowSeqNum = workflowSeqNum;
	}
	
	public ApplicationWorkflowId() {}
}