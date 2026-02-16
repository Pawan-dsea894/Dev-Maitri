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
public class EmergencyLoanRequestFields {

	@JsonProperty("modeOfPayment")
	private String modeOfPayment;

	@JsonProperty("loanPurpose")
	private String loanPurpose;

	@JsonProperty("loanSubPurpose")
	private String loanSubPurpose;

	@JsonProperty("customerId")
	private String customerId;

	@JsonProperty("loanProduct")
	private String loanProduct;

	@JsonProperty("term")
	private String term;

	@JsonProperty("amount")
	private String amount;
	
	@JsonProperty("paymentFreq")
	private String paymentFreq;
	
	@JsonProperty("amountApproved")
	private String amountApproved;

}
