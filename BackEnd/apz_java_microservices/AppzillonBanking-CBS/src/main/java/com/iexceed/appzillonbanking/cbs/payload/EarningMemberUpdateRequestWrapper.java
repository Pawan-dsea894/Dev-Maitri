package com.iexceed.appzillonbanking.cbs.payload;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class EarningMemberUpdateRequestWrapper {

	@JsonProperty("apiRequest")	
	private EarningMemberUpdateRequest apiRequest;
}
