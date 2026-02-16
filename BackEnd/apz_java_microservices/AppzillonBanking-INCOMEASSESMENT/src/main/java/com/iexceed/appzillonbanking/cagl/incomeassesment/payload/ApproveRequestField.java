package com.iexceed.appzillonbanking.cagl.incomeassesment.payload;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ApproveRequestField {

	@JsonProperty("applicationId")
	private String applicationId;

	@JsonProperty("versionNo")
	private String versionNo;

	@JsonProperty("ocrResponse")
	private String ocrResponse;
	
	@JsonProperty("editedResponse")
	private String editedResponse;

	@JsonProperty("updatedBy")
	private String updatedBy;

	@JsonProperty("updateTS")
	private String updateTS;
	
	@JsonProperty("isEdited")
	private String isEdited;

	@JsonProperty("isApproved")
	private String isApproved;

	@JsonProperty("isRejected")
	private String isRejected;
	
	@JsonProperty("remarks")
	private String remarks;

	@JsonProperty("status")
	private String status;
	
	@JsonProperty("workflow")
	private WorkFlowDetails workFlow;
	
}
