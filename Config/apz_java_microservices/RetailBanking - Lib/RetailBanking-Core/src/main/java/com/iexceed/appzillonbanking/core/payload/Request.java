package com.iexceed.appzillonbanking.core.payload;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Request {

	@JsonProperty("interfaceName")
	private String interfaceName;
	
	@JsonProperty("appId")
	private String appId;
	
	@JsonProperty("userId")
	private String userId;
	
	@JsonProperty("requestObj")
	private Object requestObj;
	
}
