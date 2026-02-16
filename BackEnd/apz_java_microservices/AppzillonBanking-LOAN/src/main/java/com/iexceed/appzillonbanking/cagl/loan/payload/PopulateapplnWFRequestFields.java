package com.iexceed.appzillonbanking.cagl.loan.payload;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PopulateapplnWFRequestFields {
	
	@JsonProperty("applicationDetailList")
	private List<ApplicationList> applicationDetailList;

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
	
	@JsonProperty("cbApproveManual")
	private String cbApproveManual;
	/*
	 * @JsonProperty("type") private String type;
	 */
	
	@JsonProperty("userRole")
	private String userRole;
	
	@JsonProperty("userName")
	private String userName;
	
	@JsonProperty("appVersion")
	private String appVersion;
	
	@JsonProperty("remarks")
	private String remarks;
}
