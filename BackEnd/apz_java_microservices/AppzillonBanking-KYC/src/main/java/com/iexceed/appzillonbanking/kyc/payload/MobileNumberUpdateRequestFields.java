package com.iexceed.appzillonbanking.kyc.payload;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MobileNumberUpdateRequestFields {

	@JsonProperty("phone")
	private String phone;
	
	@JsonProperty("memberId")
	private String memberId;
	
}
