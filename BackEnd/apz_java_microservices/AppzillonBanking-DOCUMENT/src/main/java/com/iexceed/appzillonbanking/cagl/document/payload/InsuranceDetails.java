package com.iexceed.appzillonbanking.cagl.document.payload;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class InsuranceDetails {

	@JsonProperty("member")
	private String member;

	@JsonProperty("Spouse")
	private String Spouse;

	@JsonProperty("applicant_insurance_amt")
	private String applicant_insurance_amt;

	@JsonProperty("spouse_insurance_amt")
	private String spouse_insurance_amt;

	@JsonProperty("insuranceProvider")
	private String insuranceProvider;

	@JsonProperty("insurCharges")
	private String insurCharges;
}
