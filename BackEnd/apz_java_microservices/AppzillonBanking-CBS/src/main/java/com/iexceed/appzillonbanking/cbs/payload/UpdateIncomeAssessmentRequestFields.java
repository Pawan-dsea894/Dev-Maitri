package com.iexceed.appzillonbanking.cbs.payload;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UpdateIncomeAssessmentRequestFields {

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

	@JsonProperty("recordstatus")
	private String recordstatus;

	@JsonProperty("branchId")
	private String branchId;
}
