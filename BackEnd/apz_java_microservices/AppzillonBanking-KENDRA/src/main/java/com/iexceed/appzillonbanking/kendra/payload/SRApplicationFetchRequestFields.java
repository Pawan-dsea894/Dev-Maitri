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
public class SRApplicationFetchRequestFields {

	@JsonProperty("applicationId")
	private String applicationId;
	
	@JsonProperty("currRole")
	private String currRole;
	
	@JsonProperty("createBy")
	private String createBy;
	
	@JsonProperty("srType")
	private String srType;
}
