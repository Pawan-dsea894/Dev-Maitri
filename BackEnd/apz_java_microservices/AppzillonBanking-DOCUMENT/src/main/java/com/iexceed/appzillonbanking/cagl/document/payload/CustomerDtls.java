package com.iexceed.appzillonbanking.cagl.document.payload;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CustomerDtls {

	@JsonProperty("customerId")
	private String customerId;

	@JsonProperty("customerName")
	private String customerName;
	
	@JsonProperty("mobileNum")
	private String mobileNum;
	
	@JsonProperty("kycDtls")
	private KYCDetails kycDtls;
	
	@JsonProperty("income")
	private List<Income> income;
	
	@JsonProperty("earnings")
	private List<Earnings> earnings;

	@JsonProperty("bankDtls")
	private BankDetails bankDtls;
	
	@JsonProperty("addressDtls")
	private List<AddressDtls> addressDtls;
	
	@JsonProperty("loanDtls")
	private LoanDtls loanDtls;
	
	@JsonProperty("loanAmount")
	private String loanAmount;
	
}
