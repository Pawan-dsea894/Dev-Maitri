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
public class UpdateKendraGeoLocationRequestFields {

	@JsonProperty("id")
	private String id; 
	
	@JsonProperty("gkv")
	private String gkv; 
	
	@JsonProperty("method")
	private String method; 
    
	@JsonProperty("kendraid")
	private String kendraid;
    	
	@JsonProperty("latitude")
	private String latitude;
	
	@JsonProperty("longitude")
	private String longitude;
	
}
