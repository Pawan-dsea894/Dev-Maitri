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
public class IncrementLoanRequestFields {

	@JsonProperty("company")
	private String company;

	@JsonProperty("password")
	private String password;

	@JsonProperty("userName")
	private String userName;

	@JsonProperty("columnName")
	private String columnName;

	@JsonProperty("criteriaValue")
	private String criteriaValue;

	@JsonProperty("operand")
	private String operand;

}
