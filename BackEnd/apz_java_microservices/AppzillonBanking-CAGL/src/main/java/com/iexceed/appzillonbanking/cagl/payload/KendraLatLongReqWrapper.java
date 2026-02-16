package com.iexceed.appzillonbanking.cagl.payload;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class KendraLatLongReqWrapper {
	
	@JsonProperty("apiRequest")
	private KendraLatLongReqApi apiRequest;

}
