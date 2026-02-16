package com.iexceed.appzillonbanking.cagl.loan.payload;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class HouseholdMemberPayload {

	@JsonProperty("applicantType")
	private String applicantType;

	@JsonProperty("relationtype")
	private String relationtype;

	@JsonProperty("custName")
	private String custName;

	@JsonProperty("dob")
	private String dob;

	@JsonProperty("gender")
	private String gender;

	@JsonProperty("phone")
	private String phone;

	@JsonProperty("earning_flag")
	private String earningFlag;

	@JsonProperty("address")
	private List<AddressPayload> address;

	@JsonProperty("document")
	private List<DocumentPayload> document;
}
