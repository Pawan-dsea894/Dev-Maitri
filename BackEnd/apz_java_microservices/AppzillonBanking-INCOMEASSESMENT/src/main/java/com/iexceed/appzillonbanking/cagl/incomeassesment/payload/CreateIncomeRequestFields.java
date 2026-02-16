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
public class CreateIncomeRequestFields {

	@JsonProperty("appId")
    private String appId;
	
	@JsonProperty("applicationId")
    private String applicationId;
	
	@JsonProperty("updatedBy")
    private String updatedBy;
	
	@JsonProperty("userId")
    private String userId;
	
	@JsonProperty("houseHoldDetails")
    private HouseHoldDetails houseHoldDetails;
	
	@JsonProperty("loanId")
    private String loanId;
	
	@JsonProperty("customerID")
	private String customerID;
	
}
