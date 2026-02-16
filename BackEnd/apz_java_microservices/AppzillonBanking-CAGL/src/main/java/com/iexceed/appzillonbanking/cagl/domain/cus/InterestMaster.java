package com.iexceed.appzillonbanking.cagl.domain.cus;

import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.persistence.Column;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

//@Entity
//@Table(name = "interest_master")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class InterestMaster {

	@JsonProperty("state")
	@Column(name = "STATE")
	private String state;

	@JsonProperty("categoryCode")
	@Column(name = "CATEGORY_CODE")
	private String categoryCode;

	@JsonProperty("loanType")
	@Column(name = "LOAN_TYPE")
	private String loanType;

	@JsonProperty("vintage")
	@Column(name = "VINTAGE")
	private String vintage;

	@JsonProperty("interestRate")
	@Column(name = "INTEREST_RATE")
	private String interestRate;

	@JsonProperty("companyType")
	@Column(name = "COMPANY_TYPE")
	private String companyType;

}
