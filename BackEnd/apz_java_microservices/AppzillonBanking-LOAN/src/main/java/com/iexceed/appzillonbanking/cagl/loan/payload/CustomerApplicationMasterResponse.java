package com.iexceed.appzillonbanking.cagl.loan.payload;


import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CustomerApplicationMasterResponse {

	@JsonProperty("appId")
	private String appId;
	
	@JsonProperty("applicationId")
	private String applicationId;
	
	@JsonProperty("latestVersionNo")
	private String latestVersionNo;

	@JsonProperty("applicationStatus")
	private String applicationStatus;
	
	@JsonProperty("applicationType")
	private String applicationType;
	
	@JsonProperty("branchCode")
	private String branchCode;
	
	@JsonProperty("branchName")
	private String branchName;

	@JsonProperty("currentScreenId")
	private String currentScreenId;
	
	@JsonProperty("currentStage")
	private String currentStage;

	@JsonProperty("remarks")
	private String remarks;

	@JsonProperty("customerId")
	private String customerId;
	
	@JsonProperty("customerName")
	private String customerName;

	@JsonProperty("kendraId")
	private Long kendraId;
	
	@JsonProperty("kendraName")
	private String kendraName;

	@JsonProperty("kmId")
	private String kmId;
	
	@JsonProperty("kycType")
	private String kycType;

	@JsonProperty("createdBy")
	private String createdBy;
	
	@JsonProperty("createTs")
	private String  createTs;

	@JsonProperty("addInfo")
	private String addInfo;
	
	@JsonProperty("add_info1")
	private String add_info1;

}
