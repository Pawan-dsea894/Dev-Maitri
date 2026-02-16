package com.iexceed.appzillonbanking.cagl.collection.payload;

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
	private int dueAmt;
	
	@JsonProperty("osAmt")
	private int osAmt;
	
	@JsonProperty("cashAmt")
	private int cashAmt;
	
	@JsonProperty("upiAmt")
	private int upiAmt;
	
	@JsonProperty("parAmt")
	private int parAmt;
}
