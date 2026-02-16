package com.iexceed.appzillonbanking.cagl.loan.payload;

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
public class AuditTrailRequestFields {
	
	@JsonProperty("userId")
	private String userId;

	@JsonProperty("userRole")
	private String userRole;

	@JsonProperty("branchId")
	private String branchId;
	
	@JsonProperty("kendraId")
	private List<String> kendraId;

}
