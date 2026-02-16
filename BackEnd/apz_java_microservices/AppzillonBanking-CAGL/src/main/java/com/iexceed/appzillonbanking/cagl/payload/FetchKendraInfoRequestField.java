package com.iexceed.appzillonbanking.cagl.payload;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class FetchKendraInfoRequestField {

	@JsonProperty("branchId")
	private String branchId;

	@JsonProperty("nextMeetingDt")
	private String nextMeetingDt;
	
	//@JsonProperty("roleName")
	//private String roleName;

}
