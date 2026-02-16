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
public class Earnings {

	@JsonProperty("customerId")
	private String customerId;

	@JsonProperty("name")
	private String name;

	@JsonProperty("dob")
	private String dob;

	@JsonProperty("memRelation")
	private String memRelation;

	@JsonProperty("legaldocName")
	private String legaldocName;

	@JsonProperty("legaldocId")
	private String legaldocId;

}
