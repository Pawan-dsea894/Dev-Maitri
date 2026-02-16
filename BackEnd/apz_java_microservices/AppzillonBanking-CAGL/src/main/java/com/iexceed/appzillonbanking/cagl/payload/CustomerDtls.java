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
public class CustomerDtls {

	@JsonProperty("customerId")
	private String customerId;

	@JsonProperty("customerName")
	private String customerName;

	@JsonProperty("kendraId")
	private int kendraId;

	@JsonProperty("kendraName")
	private String kendraName;

	@JsonProperty("groupId")
	private int groupId;

	@JsonProperty("kycDetails")
	private KYCDetails kycDetails;

	@JsonProperty("incomeDtls")
	private IncomeDetails incomeDtls;
}
