package com.iexceed.appzillonbanking.cagl.incomeassesment.payload;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LockCustomerRequestFields {

	@JsonProperty("applicationId")
	private String applicationId;

	@JsonProperty("roleId")
	private String roleId;

	@JsonProperty("createdBy")
	private String createdBy;


}
