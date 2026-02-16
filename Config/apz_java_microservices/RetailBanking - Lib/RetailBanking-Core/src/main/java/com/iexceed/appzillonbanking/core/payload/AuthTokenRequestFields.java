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
public class AuthTokenRequestFields {

	@Schema(requiredMode = RequiredMode.REQUIRED, example = "")
	@JsonProperty("client_id")
	public String client_id;

	@Schema(requiredMode = RequiredMode.REQUIRED, example = "")
	@JsonProperty("client_secret")
	public String client_secret;

	@Schema(requiredMode = RequiredMode.REQUIRED, example = "")
	@JsonProperty("grant_type")
	public String grant_type;

}
