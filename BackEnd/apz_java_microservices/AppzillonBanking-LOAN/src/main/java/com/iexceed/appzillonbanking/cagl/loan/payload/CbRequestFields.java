package com.iexceed.appzillonbanking.cagl.loan.payload;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CbRequestFields {

	@JsonProperty("applicationId")
	private String applicationId;

	@JsonProperty("versionNo")
	private String versionNo;
	
	@JsonProperty("schedulerEnabled")
	private String schedulerEnabled;
	
	@JsonProperty("custDtlId")
	private String custDtlId;
	
	@JsonProperty("cbRecheck")
	private String cbRecheck;
	
	@JsonProperty("crtFlag")
	private String crtFlag;
	
	@JsonProperty("userRole")
	private String userRole;
	
	@JsonProperty("userName")
	private String userName;
	
	@JsonProperty("appVersion")
	private String appVersion;
	
	@JsonProperty("remarks")
	private String remarks;
	
	@JsonProperty("userId")
	private String userId;
	
}
