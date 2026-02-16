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
public class IncomeDetails {

	@JsonProperty("totalEarningPerYr")
	private String totalEarningPerYr;
	
	@JsonProperty("totalExpensePerYr")
	private String totalExpensePerYr;
	
	@JsonProperty("incomeOfFamilyMember")
	private String incomeOfFamilyMember;
	
}
