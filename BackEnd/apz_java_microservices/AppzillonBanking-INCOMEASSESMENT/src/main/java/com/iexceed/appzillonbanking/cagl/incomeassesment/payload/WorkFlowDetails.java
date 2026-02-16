package com.iexceed.appzillonbanking.cagl.incomeassesment.payload;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;
@Getter @Setter
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
	
	@JsonProperty("remarks")
	private String remarks;
	
	@JsonProperty("nextWorkflowStatus")
	private String nextWorkflowStatus;
	
}