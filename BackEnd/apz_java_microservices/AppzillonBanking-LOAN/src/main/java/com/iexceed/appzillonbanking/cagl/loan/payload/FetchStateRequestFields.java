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
public class FetchStateRequestFields {

	@JsonProperty("keyvalue")
	private String keyvalue;
	
	@JsonProperty("code")
	private String code;
	
	@JsonProperty("description")
	private String description;
}
