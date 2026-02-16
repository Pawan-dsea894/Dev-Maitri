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
public class KYCDetails {

	@JsonProperty("familyMemberName")
	private String familyMemberName;
	
	@JsonProperty("relationship")
	private String relationship;
	
	@JsonProperty("dob")
	private String dob;
	
	@JsonProperty("address")
	private String address;
	
	@JsonProperty("kycType")
	private String kycType;
	
	@JsonProperty("kycId")
	private String kycId;
}
