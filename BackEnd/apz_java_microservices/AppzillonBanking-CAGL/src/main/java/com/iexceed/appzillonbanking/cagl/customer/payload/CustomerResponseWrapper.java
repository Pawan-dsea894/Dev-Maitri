package com.iexceed.appzillonbanking.cagl.customer.payload;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CustomerResponseWrapper {
	
	@JsonProperty("apiResponse")
	private CustomerResponse apiResponse;

}
