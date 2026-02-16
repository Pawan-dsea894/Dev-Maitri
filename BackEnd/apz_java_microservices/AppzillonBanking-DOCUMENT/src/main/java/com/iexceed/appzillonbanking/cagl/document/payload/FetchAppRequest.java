package com.iexceed.appzillonbanking.cagl.document.payload;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@NoArgsConstructor
@AllArgsConstructor
public class FetchAppRequest {

	@JsonProperty("appId")
	private String appId;
	
	@JsonProperty("interfaceId")
	private String interfaceId;
	
	@JsonProperty("userId")
	private String userId;
	
	@JsonProperty("requestObj")
	private FetchAppRequestFields requestObj;
}