package com.iexceed.appzillonbanking.cagl.collection.payload;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CollectionMemberDtls {
	
	
	@JsonProperty("id")
	private String id;
	
	@JsonProperty("name")
	private String name;
	
	@JsonProperty("depname")
	private String depname;
	
	@JsonProperty("totalDue")
	private int totalDue;
	
	@JsonProperty("totalAdv")
	private int totalAdv;
	
	@JsonProperty("netDue")
	private int netDue;
	
	@JsonProperty("collAmount")
	private int collAmount;
	
	@JsonProperty("attend")
	private String attend;
	
	@JsonProperty("parFlg")
	private String parFlg;
	
	@JsonProperty("parAmt")
	private int parAmt;
	
	@JsonProperty("advAmt")
	private int advAmt;
	
	@JsonProperty("totalCash")
	private int totalCash;
	
	@JsonProperty("totalUPI")
	private int totalUPI;
	
	@JsonProperty("outstandingAmt")
	private int outstandingAmt;
	
	@JsonProperty("advAdj")
	private int advAdj;
	
	@JsonProperty("paymentFlg")
	private String paymentFlg;
	
	@JsonProperty("loans")
	private List<CollectionLoanDtls> loans;
	
	@JsonProperty("pos")
	private int pos;
}
