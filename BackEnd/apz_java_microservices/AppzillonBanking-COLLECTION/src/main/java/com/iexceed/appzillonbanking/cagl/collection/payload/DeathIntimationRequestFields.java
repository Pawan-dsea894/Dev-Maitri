package com.iexceed.appzillonbanking.cagl.collection.payload;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class DeathIntimationRequestFields {

	@JsonProperty("appId")
	private String appId;
	
	@JsonProperty("kmId")
	private String kmId;
	
	@JsonProperty("kmUserRole")
	private String kmUserRole;

	@JsonProperty("branchId")
	private String branchId;

	@JsonProperty("deathIntimation")
	private List<DeathIntimationFields> deathIntimation;
}
