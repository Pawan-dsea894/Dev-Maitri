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
public class FetchCustomerPayOffDetailRequestFields {

	@JsonProperty("branchId")
	private String branchId;
	
	@JsonProperty("branchId")
	private String customerId;

}
