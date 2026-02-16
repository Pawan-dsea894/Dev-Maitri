package com.iexceed.appzillonbanking.cagl.loan.payload;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ChargeAndBreakupDetails {

	@JsonProperty("loanAmt")
	private String loanAmt;

	@JsonProperty("aprxLoanCharges")
	private String aprxLoanCharges;

	@JsonProperty("loanProcessingFee")
	private String loanProcessingFee;

	@JsonProperty("aprxLoanAmt")
	private String aprxLoanAmt;

	@JsonProperty("aprxInstallmentAmt")
	private String aprxInstallmentAmt;

	@JsonProperty("GST")
	private String GST;
	
	@JsonProperty("addInfo1")
	private String addInfo1;
	
	@JsonProperty("addInfo2")
	private String addInfo2;
	
	@JsonProperty("iscbUpdated")
	private String iscbUpdated;
	
	@JsonProperty("interest_Fee")
	private String interest_Fee;
	
	@JsonProperty("upfront_Fee")
	private String upfront_Fee;

}
