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
public class VoterAuthenticateRequestFields {

	@JsonProperty("requestId")
	private String requestId; 
    
	@JsonProperty("voterId")
	private String voterId;
	
}
