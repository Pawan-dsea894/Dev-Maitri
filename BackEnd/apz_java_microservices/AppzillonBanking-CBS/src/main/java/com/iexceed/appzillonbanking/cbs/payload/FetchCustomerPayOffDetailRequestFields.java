package com.iexceed.appzillonbanking.cbs.payload;

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

	@JsonProperty("companyId")
	private String companyId;
	
	@JsonProperty("customerId")
	private String customerId;

}
