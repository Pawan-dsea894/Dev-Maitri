package com.iexceed.appzillonbanking.cagl.loan.payload;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class FetchRoleRequest {

	@JsonProperty("requestObj")
	private FetchRoleRequestFields requestObj;

	@JsonProperty("interfaceName")
	private String interfaceName;

	@JsonProperty("appId")
	private String appId;
}