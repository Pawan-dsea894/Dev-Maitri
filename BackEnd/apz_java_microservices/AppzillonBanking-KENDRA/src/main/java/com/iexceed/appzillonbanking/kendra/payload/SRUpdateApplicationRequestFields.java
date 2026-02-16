package com.iexceed.appzillonbanking.kendra.payload;

import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.persistence.Column;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SRUpdateApplicationRequestFields {

	@JsonProperty("applicationId")
	private String applicationId;
	
	@JsonProperty("srType")
	private String srType;

	@JsonProperty("appStatus")
	private String appStatus; 

	@JsonProperty("createBy")
	private String createBy; 

	@JsonProperty("updateTs")
	private String updateTs; 

	@JsonProperty("updateBy")
	private String updateBy; 

	@JsonProperty("remarks")
	private String remarks;

}
