package com.iexceed.appzillonbanking.kendra.payload;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SRApplicationRequestFields {

	@JsonProperty("applicationId")
	private String applicationId;
	
	@JsonProperty("srType")
	private String srType;
	
	@JsonProperty("payload")
	private String payload;
	
	@JsonProperty("createdRole")
	private String createdRole;
	
	@JsonProperty("currRole")
	private String currRole;
	
	@JsonProperty("nextRole")
	private String nextRole;
	
	@JsonProperty("appStatus")
	private String appStatus;
	
	@JsonProperty("createTs")
	private String createTs;
	
	@JsonProperty("createBy")
	private String createBy;
	
	@JsonProperty("updateTs")
	private String updateTs;
	
	@JsonProperty("updateBy")
	private String updateBy;
	
	@JsonProperty("remarks")
	private String remarks;
	
	@JsonProperty("addInfo1")
	private String addInfo1;
	
	@JsonProperty("addInfo2")
	private String addInfo2;
	
	@JsonProperty("branchId")
	private String branchId;
	
}
