package com.iexceed.appzillonbanking.cagl.incomeassesment.payload;

import java.time.LocalDateTime;

import org.hibernate.annotations.CreationTimestamp;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CreateAppRequestFields {

	@JsonProperty("customerId")
	private String customerId; 
	
	@JsonProperty("addInfo")
	private Object addInfo;
    
	@JsonProperty("kendraId")
	private String kendraId;
	
	@JsonProperty("kendraName")
	private String kendraName;
	
	@JsonProperty("leader")
	private String leader;
	
	@JsonProperty("customerName")
	private String customerName;
	
	@JsonProperty("loanMode")
	private String loanMode;
    	
	@JsonProperty("kmId")
	private String kmId;
	
	@JsonProperty("applicationId")
	private String applicationId;

	@JsonProperty("applicationDate")
	private String applicationDate;

	@CreationTimestamp
	@JsonProperty("createTs")
	private LocalDateTime createTs;

	@JsonProperty("createdBy")
	private String createdBy;
	
	@JsonProperty("updatedBy")
	private String updatedBy;
	
	@JsonProperty("applicationType")
	private String applicationType;

	@JsonProperty("kycType")
	private String kycType;
	
	@JsonProperty("status")
	private String status;
	
	@JsonProperty("applicationStatus")
	private String applicationStatus;
	
	@JsonProperty("productGrpCode")
	private String productGrpCode;
	
	@JsonProperty("productCode")
	private String productCode;

	@JsonProperty("currentStage")
	private String currentStage;

	@JsonProperty("currentScrId")
	private String currentScrId;
		
	@JsonProperty("branchCode")
	private String branchCode;
	
	@JsonProperty("cbCheck")
	private String cbCheck;
	
	@JsonProperty("applicationRefNo")
	private String applicationRefNo;
	
	@JsonProperty("incAssessmentPayload")
	private Object incAssessmentPayload;
	
	@JsonProperty("remarks")
	private String remarks;
	
	@JsonProperty("workflow")
	private WorkFlowDetails workFlow;
		
}
