package com.iexceed.appzillonbanking.cagl.dto;

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
	
	@JsonProperty("mobileNumber")
	private String mobileNumber;
	
	@JsonProperty("primaryId")
	private String primaryId;
	
	@JsonProperty("totalDue")
	private double totalDue;
	
	@JsonProperty("totalAdv")
	private double totalAdv;
	
	@JsonProperty("netDue")
	private double netDue;
	
	@JsonProperty("collAmount")
	private double collAmount;
	
	@JsonProperty("attend")
	private String attend;
	
	@JsonProperty("parFlg")
	private String parFlg;
	
	@JsonProperty("parAmt")
	private double parAmt;
	
	@JsonProperty("prevParAmt")
	private double prevParAmt;
	
	@JsonProperty("advAmt")
	private double advAmt;
	
	@JsonProperty("loans")
	List<CollectionLoanDtls> collectionLoanDtls;
}
