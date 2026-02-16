package com.iexceed.appzillonbanking.cagl.incomeassesment.payload;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UpdateAssessmentDetailsRequestFields {

	@JsonProperty("id")
	private String id;
	
	@JsonProperty("gkv")
	private String gkv;
	
	@JsonProperty("method")
	private String method;
	
	@JsonProperty("customerId")
	private String customerId;
	
	@JsonProperty("totIncome")
	private String totIncome;
	
	@JsonProperty("totExpense")
	private String totExpense;
	
	@JsonProperty("assessmentDate")
	private String assessmentDate;
	
	@JsonProperty("statusFlag")
	private String statusFlag;
	
	@JsonProperty("qaFlag")
	private String qaFlag;
	
	@JsonProperty("recordStatus")
	private String recordStatus;
	
	@JsonProperty("branchId")
	private String branchId;
	
	
}