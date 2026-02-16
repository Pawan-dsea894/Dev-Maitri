package com.iexceed.appzillonbanking.kendra.payload;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MultiKendraDetailsUpdateRequestFields {

	@JsonProperty("kendra")
	private String kendra;
	
	@JsonProperty("allocateKendra")
	private String allocateKendra;
	
	@JsonProperty("branchId")
	private String branchId;
}
