package com.iexceed.appzillonbanking.cbs.payload;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PennyCheckRequestFields {

	@JsonProperty("useCombinedSolution")
	private String useCombinedSolution;

	@JsonProperty("nameMatchType")
	private String nameMatchType;

	@JsonProperty("allowPartialMatch")
	private String allowPartialMatch;

	@JsonProperty("accountNumber")
	private String accountNumber;

	@JsonProperty("ifsc")
	private String ifsc;

	@JsonProperty("consent")
	private String consent;

	@JsonProperty("accountHolderName")
	private String accountHolderName;

}
