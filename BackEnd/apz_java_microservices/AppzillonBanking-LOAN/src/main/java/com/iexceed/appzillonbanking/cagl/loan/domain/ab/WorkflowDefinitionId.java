package com.iexceed.appzillonbanking.cagl.loan.domain.ab;

import lombok.Getter;
import lombok.Setter;

import jakarta.persistence.*;
import java.io.Serializable;

@Getter @Setter
public class WorkflowDefinitionId implements Serializable {

	private static final long serialVersionUID = 1L;

	@Column(name = "APP_ID", nullable = false)
	private String appId;
	
	@Column(name = "WORKFLOW_ID", nullable = false)
	private String workFlowId;
	
	@Column(name = "STAGE_SEQ_NO", nullable = false)
	private int stageSeqNum;
}
