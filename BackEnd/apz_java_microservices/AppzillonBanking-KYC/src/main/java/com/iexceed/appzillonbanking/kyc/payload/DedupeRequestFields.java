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
public class DedupeRequestFields {

	@JsonProperty("mobileNo")
	private String mobileNo; 
    
	@JsonProperty("accountNo")
	private String accountNo;
	
	@JsonProperty("kycId")
	private String kycId;
    	
	@JsonProperty("authToken")
	private String authToken;
	
	@JsonProperty("id")
	private String id;
	
	@JsonProperty("dedupeId")
	private String dedupeId;
	
	@JsonProperty("gkv")
	private String gkv;
	
	@JsonProperty("method")
	private String method;
	
	@JsonProperty("typesrch")
	private String typesrch;
	
	@JsonProperty("customerId")
	private String customerId;

}
