package com.iexceed.appzillonbanking.cagl.loan.payload;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SanctionReportRequestFields {
	
	@JsonProperty("applicationId")
	private String applicationId;

	@JsonProperty("appId")
	private String appId;

	@JsonProperty("versionNum")
	private String versionNum;
	
	@JsonProperty("customerName")
	private String customerName;
	
	@JsonProperty("mobileNo")
	private String mobileNo;
	
}