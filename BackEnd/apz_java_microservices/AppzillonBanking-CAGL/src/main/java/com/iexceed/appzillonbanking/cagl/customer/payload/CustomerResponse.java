package com.iexceed.appzillonbanking.cagl.customer.payload;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CustomerResponse {
	
	@JsonProperty("responseHeader")
	private CustomerResponseHeader responseHeader;
	
	@JsonProperty("responseBody")
	private CustomerResponseBody responseBody;

}
