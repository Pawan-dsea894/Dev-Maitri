package com.iexceed.appzillonbanking.cagl.collection.domain.ab;

import java.io.Serializable;

import jakarta.persistence.Column;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Builder
@Data
@EqualsAndHashCode
@AllArgsConstructor
@NoArgsConstructor
public class WorkflowDefinitionId implements Serializable {

	private static final long serialVersionUID = 1L;

	@Column(name = "APP_ID", nullable = false)
	private String appId;
	
	@Column(name = "WORKFLOW_ID", nullable = false)
	private String workFlowId;
	
	@Column(name = "STAGE_SEQ_NO", nullable = false)
	private int stageSeqNum;
}
