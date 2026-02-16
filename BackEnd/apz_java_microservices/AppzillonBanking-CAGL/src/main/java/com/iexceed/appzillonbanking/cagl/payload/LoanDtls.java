package com.iexceed.appzillonbanking.cagl.payload;

import java.math.BigDecimal;
import java.sql.Date;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class LoanDtls {

	@JsonProperty("loanDtlId")
	private BigDecimal loanDtlId;

	@JsonProperty("loanAmount")
	private BigDecimal loanAmount;

	@JsonProperty("tenureInMonths")
	private Integer tenureInMonths;

	@JsonProperty("tenureInYears")
	private Integer tenureInYears;

	@JsonProperty("roi")
	private Float roi;

	@JsonProperty("interest")
	private Float interest;

	@JsonProperty("loanClosureDate")
	private Date loanClosureDate;

	@JsonProperty("totPayableAmount")
	private BigDecimal totPayableAmount;

	@JsonProperty("autoEmiAccount")
	private String autoEmiAccount;

	@JsonProperty("autoEmiAccountType")
	private String autoEmiAccountType;

	@JsonProperty("emiDate")
	private String emiDate;

	@JsonProperty("loanCrAccount")
	private String loanCrAccount;

	@JsonProperty("loanCrAccountType")
	private String loanCrAccountType;

	@JsonProperty("monthlyEmi")
	private BigDecimal monthlyEmi;

	@JsonProperty("productCode")
	private String productCode;

	@JsonProperty("productGroupCode")
	private String productGroupCode;
}
