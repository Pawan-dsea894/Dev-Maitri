package com.iexceed.appzillonbanking.cagl.payload;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SMSRequestFields {

	@JsonProperty("msg")
	private String msg; 
	
	@JsonProperty("mobileNo")
	private String mobileNo; 
	
	@JsonProperty("transId")
	private String transId; 
    
	@JsonProperty("customerId")
	private String customerId;
    	
	@JsonProperty("authToken")
	private String authToken;
	
	@JsonProperty("customerName")
	private String customerName;
	
	@JsonProperty("senderId")
	private String senderId;

}
