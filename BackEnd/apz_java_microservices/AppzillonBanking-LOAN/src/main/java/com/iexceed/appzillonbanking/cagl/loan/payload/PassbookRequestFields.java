package com.iexceed.appzillonbanking.cagl.loan.payload;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PassbookRequestFields {

	@JsonProperty("applicationId")
	private String applicationId;

	@JsonProperty("loanId")
	private String loanId;

	@JsonProperty("memberId")
	private String memberId = "";

	@JsonProperty("memberName")
	private String memberName = "";

	@JsonProperty("kendraName")
	private String kendraName = "";

	@JsonProperty("productName")
	private String productName = "";

	@JsonProperty("loanPurpose")
	private String loanPurpose = "";

	@JsonProperty("moratorium")
	private String moratorium = "";

	@JsonProperty("disbursementDate")
	private String disbursementDate = "";

	@JsonProperty("spouseName")
	private String spouseName = "";

	@JsonProperty("loanAmt")
	private String loanAmt = "";

	@JsonProperty("roi")
	private String roi = "";

	@JsonProperty("phone")
	private String phone = "";

	@JsonProperty("processingFee")
	private String processingFee = "";

	@JsonProperty("insurancePremiumMember")
	private String insurancePremiumMember = "";
	
	@JsonProperty("insurancePremiumSpouse")
	private String insurancePremiumSpouse = "";
	
	@JsonProperty("termOfLoan")
	private String termOfLoan = "";
	
	@JsonProperty("type")
	private String type;
	
}
