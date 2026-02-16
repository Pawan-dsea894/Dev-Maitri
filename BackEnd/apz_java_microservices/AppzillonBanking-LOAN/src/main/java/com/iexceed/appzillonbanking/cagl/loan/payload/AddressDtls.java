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
public class AddressDtls {

	@JsonProperty("addressType")
	private String addressType;

	@JsonProperty("addLine1")
	private String addLine1;

	@JsonProperty("addLine2")
	private String addLine2;

	@JsonProperty("state")
	private String state;

	@JsonProperty("district")
	private String district;

	@JsonProperty("villageLocality")
	private String villageLocality;

	@JsonProperty("pincode")
	private String pincode;

	@JsonProperty("taluk")
	private String taluk;

}
