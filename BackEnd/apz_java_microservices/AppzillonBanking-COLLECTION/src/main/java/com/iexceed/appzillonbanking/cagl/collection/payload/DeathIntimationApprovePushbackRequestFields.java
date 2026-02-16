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
public class DeathIntimationApprovePushbackRequestFields {

	@JsonProperty("applicationId")
	private String applicationId;

	@JsonProperty("kendraId")
	private String kendraId;

	@JsonProperty("versionNo")
	private String versionNo;

	@JsonProperty("kmUserRole")
	private String kmUserRole;

	@JsonProperty("action")
	private String action;

	@JsonProperty("remarks")
	private String remarks;
}
