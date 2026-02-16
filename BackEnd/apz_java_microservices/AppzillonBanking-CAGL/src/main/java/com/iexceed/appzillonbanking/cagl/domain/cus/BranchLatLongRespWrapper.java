package com.iexceed.appzillonbanking.cagl.domain.cus;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BranchLatLongRespWrapper {
	
	@JsonProperty("apiResponse")
	private BranchLatLongResponse apiResponse;

}
