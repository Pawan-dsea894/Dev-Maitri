package com.iexceed.appzillonbanking.cagl.incomeassesment.payload;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UpdateAssessmentRequest {

	@JsonProperty("interfaceName")
	private String interfaceName;

	@JsonProperty("appId")
	private String appId;
	
	@JsonProperty("userId")
	private String userId;
	
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
