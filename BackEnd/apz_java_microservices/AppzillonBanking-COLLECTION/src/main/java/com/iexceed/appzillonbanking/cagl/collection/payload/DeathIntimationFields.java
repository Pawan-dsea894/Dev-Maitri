package com.iexceed.appzillonbanking.cagl.collection.payload;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class DeathIntimationFields {

	@JsonProperty("kendraId")
	private String kendraId;

	@JsonProperty("kendraName")
	private String kendraName;
	
	@JsonProperty("custId")
	private String custId;
	
	@JsonProperty("custName")
	private String custName;
	
	@JsonProperty("type")
	private String type;
	
	@JsonProperty("payload")
	private String payload;
}
