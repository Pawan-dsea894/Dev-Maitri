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
public class FetchAllSRRecordsRequestFields {

	@JsonProperty("branchId")
	private String branchId;
	
	@JsonProperty("createdBy")
	private String createdBy;
	
	@JsonProperty("userRole")
	private String userRole;
	
	@JsonProperty("amId")
	private String amId;
	
}
