package com.iexceed.appzillonbanking.scheduler.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CbRequestFields {
	
	@JsonProperty("applicationId")
	private String applicationId;

	@JsonProperty("versionNo")
	private String versionNo;
	
	@JsonProperty("schedulerEnabled")
	private String schedulerEnabled;
	
	@JsonProperty("cbRecheck")
	private String cbRecheck;
	
	@JsonProperty("custDtlId")
	private String custDtlId;

}
