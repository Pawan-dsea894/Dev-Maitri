package com.iexceed.appzillonbanking.cagl.loan.payload;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SanctionLoanScheduleRequestFields {

	@JsonProperty("loanFrequency")
	private String loanFrequency;
	
	@JsonProperty("interestRate")
	private String interestRate;
	
	@JsonProperty("loanAmount")
	private String loanAmount;
	
	@JsonProperty("tenure")
	private String tenure;
	
	@JsonProperty("customerId")
	private String customerId;
	
	@JsonProperty("productID")
	private String productID;
}
