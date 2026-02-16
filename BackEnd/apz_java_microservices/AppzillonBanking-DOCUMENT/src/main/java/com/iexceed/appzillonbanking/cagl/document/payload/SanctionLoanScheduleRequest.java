package com.iexceed.appzillonbanking.cagl.document.payload;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SanctionLoanScheduleRequest {
	
	@JsonProperty("interfaceName")
	private String interfaceName;
	
	@JsonProperty("appId")
	private String appId;
	
	@JsonProperty("userId")
	private String userId;

	@JsonProperty("requestObj")
	private SanctionLoanScheduleRequestFields requestObj;
	
}