package com.iexceed.appzillonbanking.cagl.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CollectionLoanDtls {

	@JsonProperty("id")
	private String id;
	
	@JsonProperty("prodCode")
	private String prodCode;
	
	@JsonProperty("dueAmt")
	private double dueAmt;
	
	@JsonProperty("osAmt")
	private double osAmt;
	
	@JsonProperty("cashAmt")
	private double cashAmt;
	
	@JsonProperty("upiAmt")
	private double upiAmt;
	
	@JsonProperty("parAmt")
	private double parAmt;
	
	@JsonProperty("prevParAmt")
	private double prevParAmt;
}
