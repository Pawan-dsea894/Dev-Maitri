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
public class Income {

	@JsonProperty("customerId")
	private String customerId;

	@JsonProperty("totIncome")
	private String totIncome;

	@JsonProperty("totExpense")
	private String totExpense;

	@JsonProperty("assesmentDt")
	private String assesmentDt;
}
