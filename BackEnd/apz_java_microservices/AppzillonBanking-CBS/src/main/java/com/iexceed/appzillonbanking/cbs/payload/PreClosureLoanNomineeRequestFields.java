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
public class PreClosureLoanNomineeRequestFields {

	@JsonProperty("nomineeName")
	private String nomineeName;

	@JsonProperty("relationToCust")
	private String relationToCust;


}
