package com.iexceed.appzillonbanking.cagl.collection.payload;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ApplicationWFDetails {

	@JsonProperty("appId")
	private String appId;

	@JsonProperty("applnId")
	private String applnId;

	@JsonProperty("verNo")
	private String verNo;

	@JsonProperty("kendraId")
	private String kendraId;
	
	@JsonProperty("kmUserName")
	private String kmUserName;

	@JsonProperty("seqNo")
	private String seqNo;

	@JsonProperty("createdBy")
	private String createdBy;
	
	@JsonProperty("createTs")
	private String createTs;

	@JsonProperty("appStatus")
	private String appStatus;

	@JsonProperty("usrRole")
	private String usrRole;

	@JsonProperty("nextWFStage")
	private String nextWFStage;

	@JsonProperty("remarks")
	private String remarks;
	
	@JsonProperty("workflowId")
	private String workflowId;
	
	@JsonProperty("fromStageId")
	private String fromStageId;
	
	@JsonProperty("stageSeqNo")
	private String stageSeqNo;
}
