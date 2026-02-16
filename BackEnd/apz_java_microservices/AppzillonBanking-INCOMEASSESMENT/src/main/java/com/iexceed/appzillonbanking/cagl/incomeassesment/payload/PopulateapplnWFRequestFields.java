package com.iexceed.appzillonbanking.cagl.incomeassesment.payload;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PopulateapplnWFRequestFields {

	@JsonProperty("workflow")
	private WorkFlowDetails workflow;

	@JsonProperty("createdBy")
	private String createdBy;

	@JsonProperty("applicationId")
	private String applicationId;

	@JsonProperty("applicationStatus")
	private String applicationStatus;

	@JsonProperty("appId")
	private String appId;

	@JsonProperty("versionNum")
	private String versionNum;
}