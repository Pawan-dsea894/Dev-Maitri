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
public class EarningMemberUpdateRequest {

	@JsonProperty("interfaceName")
	private String interfaceName;
	
	@JsonProperty("appId")
	private String appId;
	
	@JsonProperty("userId")
	private String userId;
	
	@JsonProperty("requestObj")	
	private EarningMemberUpdateRequestFields requestObj;
}
