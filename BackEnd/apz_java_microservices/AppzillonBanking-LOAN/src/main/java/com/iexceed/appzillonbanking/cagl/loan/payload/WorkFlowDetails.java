package com.iexceed.appzillonbanking.cagl.loan.payload;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class WorkFlowDetails {

	@JsonProperty("workflowId")
	private String workflowId;

	@JsonProperty("currentStage")
	private String currentStage;

	@JsonProperty("action")
	private String action;

	@JsonProperty("seqNo")
	private int seqNo;

	@JsonProperty("nextStageId")
	private String nextStageId;

	@JsonProperty("currentRole")
	private String currentRole;
	
	@JsonProperty("nextRole")
	private String nextRole;

	@JsonProperty("remarks")
	private String remarks;

	@JsonProperty("nextWorkflowStatus")
	private String nextWorkflowStatus;

}