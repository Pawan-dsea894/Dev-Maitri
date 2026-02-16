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
public class UserRequestFields {

	@JsonProperty("appId")
	private String appId;

	@JsonProperty("applicationId")
	private String applicationId;

	@JsonProperty("versionNum")
	private String versionNum;

	@JsonProperty("status")
	private String status;

	@JsonProperty("userId")
	private String userId;

	@JsonProperty("remarks")
	private String remarks;

	@JsonProperty("workflow")
	private WorkFlowDetails workFlow;

}