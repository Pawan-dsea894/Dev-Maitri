package com.iexceed.appzillonbanking.cagl.collection.payload;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class VerifyApplnReqFields {
	
	@JsonProperty("kendraId")
	private String kendraId;

	@JsonProperty("branchId")
	private String branchId;
	
	@JsonProperty("kmUserRole")
	private String kmUserRole;
	
	@JsonProperty("applicationType")
	private String applicationType;
	
	@JsonProperty("memberId")
	private String memberId;
	
	@JsonProperty("applicationId")
	private String applicationId;
	
}

