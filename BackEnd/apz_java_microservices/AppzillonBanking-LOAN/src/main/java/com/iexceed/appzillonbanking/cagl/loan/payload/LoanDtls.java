package com.iexceed.appzillonbanking.cagl.loan.payload;

import java.math.BigDecimal;
import java.util.List;


import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.persistence.Column;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class LoanDtls {

	@JsonProperty("caglAmt")
	private String caglAmt;

    @JsonProperty("cbAmt")
  	private String cbAmt;
      
    @JsonProperty("custVintageInterestRate")
  	private String custVintageInterestRate;
      
    @JsonProperty("insurancePercentage")
  	private String insurancePercentage;
   
    @JsonProperty("product")
  	private String product;
    
    @JsonProperty("productId")
  	private String productId;
    
    @JsonProperty("productType")
  	private String productType;
    
    @JsonProperty("shortDesc")
  	private String shortDesc;
    
    @JsonProperty("spouseInsurance")
   	private String spouseInsurance;
    
	@JsonProperty("term")
	private String term;
	
    @JsonProperty("loanMode")
  	private String loanMode;
	
	@JsonProperty("chargeAndBreakupDtls")
	private ChargeAndBreakupDetails chargeAndBreakupDtls;
	
	@JsonProperty("insurDtls")
	private InsuranceDetails insurDtls;
	
	@JsonProperty("disburseMode")
	private Object disburseMode;
	
	@JsonProperty("nomineeDtls")
	private NomineeDtls nomineeDtls;
	
	@JsonProperty("purpose")
	private Object purpose;
	
	@JsonProperty("activeLoanDtls")
	private List<Object> activeLoanDtls;
    
	@JsonProperty("repayFrequency")
	private Object repayFrequency;

	@JsonProperty("interestRate")
	private BigDecimal interestRate;

	@JsonProperty("installmentDetails")
	private String installmentDetails;
	
	@JsonProperty("maxAmountLimit")
	private String maxAmountLimit;
		
}
