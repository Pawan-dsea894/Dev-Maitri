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
public class DrivingLicenseVerifyRequestFields {

	@JsonProperty("client_ref_num")
	private String clientrefnum;
	
	@JsonProperty("dl_number")
	private String dlnumber;
	
	@JsonProperty("dob")
	private String dob;
	
}

