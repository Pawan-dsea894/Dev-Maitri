package com.iexceed.appzillonbanking.cagl.payload;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ApplyLoanRequestFields {

	@JsonProperty("currentScr")
	private String currentScr;
	
	@JsonProperty("customerDetails")
	private CustomerDtls customerDetails;

	@JsonProperty("loanDetails")
	private LoanDtls loanDetails;

	@JsonProperty("nomineeDetails")
	private NomineeDtls nomineeDetails;
}