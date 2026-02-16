package com.iexceed.appzillonbanking.cagl.loan.payload;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class FetchLoanTargetRequest {
	
	@JsonProperty("appId")
	private String appId;

	@JsonProperty("interfaceName")
	private String interfaceName;
	
	@JsonProperty("requestObj")
	FetchLoanTarget requestObj;
	
	

}
