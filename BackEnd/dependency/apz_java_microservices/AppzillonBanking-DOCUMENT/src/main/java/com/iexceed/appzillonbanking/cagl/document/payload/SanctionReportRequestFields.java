package com.iexceed.appzillonbanking.cagl.document.payload;

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
	
	@JsonProperty("reportType")
	private String reportType;
	
	@JsonProperty("appId")
	private String appId;
	
	@JsonProperty("versionNum")
	private String versionNum;
	
	@JsonProperty("customerName")
	private String customerName;
	
	@JsonProperty("mobileNo")
	private String mobileNo;
	
}