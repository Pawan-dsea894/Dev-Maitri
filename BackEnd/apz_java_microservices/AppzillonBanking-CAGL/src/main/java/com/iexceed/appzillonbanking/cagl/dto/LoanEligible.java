package com.iexceed.appzillonbanking.cagl.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class LoanEligible {
	
	private double cbAmt;
	private double caglAmt;
	private String productType;
	private String product;
	private int intRate;
	private String customerId;

}
