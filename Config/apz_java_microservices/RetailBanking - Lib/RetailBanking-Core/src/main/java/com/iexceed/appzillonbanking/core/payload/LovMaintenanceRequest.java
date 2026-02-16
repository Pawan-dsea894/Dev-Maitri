package com.iexceed.appzillonbanking.core.payload;

import com.fasterxml.jackson.annotation.JsonProperty;

import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.Schema.RequiredMode;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LovMaintenanceRequest {
	
	@Schema(requiredMode = RequiredMode.REQUIRED, example = "AddBiller")
	@JsonProperty("interfaceName")
	private String interfaceName;

	@Schema(requiredMode = RequiredMode.REQUIRED, example = "000000000002")
	@JsonProperty("userId")
	private String userId;

	@JsonProperty("requestObj")
	private LovMaintenanceRequestFields requestObj;

}
