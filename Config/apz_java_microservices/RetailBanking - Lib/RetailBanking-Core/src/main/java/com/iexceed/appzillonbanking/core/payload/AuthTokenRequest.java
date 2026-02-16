package com.iexceed.appzillonbanking.core.payload;

import com.fasterxml.jackson.annotation.JsonProperty;

import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.Schema.RequiredMode;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AuthTokenRequest {

	@Schema(requiredMode = RequiredMode.REQUIRED, example = "GetAuthToken")
	@JsonProperty("interfaceName")
	private String interfaceName;

	@Schema(requiredMode = RequiredMode.REQUIRED, example = "UNORMB")
	@JsonProperty("appId")
	private String appId;

	@Schema(requiredMode = RequiredMode.REQUIRED, example = "")
	@JsonProperty("userId")
	private String userId;

	@JsonProperty("requestObj")
	private AuthTokenRequestFields requestObj;
}
