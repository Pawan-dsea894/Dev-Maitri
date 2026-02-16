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
public class LovMaintenanceRequestFields {
	
	@JsonProperty("lovDtls")
	private Object lovDtls;

	@JsonProperty("lovName")
	private String lovName;

	@JsonProperty("appId")
	private String appId;
	
	@JsonProperty("lovId")
	private String lovId;
	
	@JsonProperty("language")
	private String language;
	
	@JsonProperty("flag")
	private String flag;
	
	@JsonProperty("userInput")
	private String userInput;

	@JsonProperty("base64Value")
	private String base64Value;
	
	@JsonProperty("fileContent")
	private String fileContent;
}
