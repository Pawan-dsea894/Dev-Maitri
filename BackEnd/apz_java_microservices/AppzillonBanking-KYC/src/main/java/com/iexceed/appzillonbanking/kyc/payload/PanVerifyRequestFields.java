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
public class PanVerifyRequestFields {

	@JsonProperty("pan")
	private String pan; 
	
}
