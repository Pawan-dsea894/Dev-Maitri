package com.iexceed.appzillonbanking.cagl.incomeassesment.payload;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FetchDataRequest {
	
	@JsonProperty("appId")
	private String appId;

	@JsonProperty("interfaceName")
	private String interfaceName;

	@JsonProperty("userId")
	private String userId;

	@JsonProperty("requestObj")
	private FetchDataRequestFields requestObj;

}
