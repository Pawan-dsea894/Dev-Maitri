package com.iexceed.appzillonbanking.cagl.incomeassesment.payload;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ApproveRequest {

	@JsonProperty("interfaceName")
	private String interfaceName;

	@JsonProperty("appId")
	private String appId;
	
	@JsonProperty("userId")
	private String userId;
	
	@JsonProperty("requestObj")
	private ApproveRequestField requestObj;
}
