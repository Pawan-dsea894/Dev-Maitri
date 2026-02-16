package com.iexceed.appzillonbanking.cagl.loan.payload;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AddressPayload {

	@JsonProperty("addrType")
	private String addrType;

	@JsonProperty("addrLine1")
	private String addrLine1;
	
	@JsonProperty("district")
	private String district;
	
	@JsonProperty("city")
	private String city;
	
	@JsonProperty("state")
	private String state;
	
	@JsonProperty("pinCode")
	private String pinCode;

}
