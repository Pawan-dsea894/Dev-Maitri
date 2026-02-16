package com.iexceed.appzillonbanking.cagl.incomeassesment.payload;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EarningMemberDedupeUpdateRequestFields {

	@JsonProperty("id")
	private String id;
	
	@JsonProperty("gkv")
	private String gkv;
	
	@JsonProperty("method")
	private String method;
	
	@JsonProperty("customerId")
	private String customerId;
	
	@JsonProperty("name")
	private String name;
	
	@JsonProperty("dob")
	private String dob;
	
	@JsonProperty("memrelation")
	private String memrelation;
	
	@JsonProperty("legaldocname")
	private String legaldocname;
	
	@JsonProperty("legalid")
	private String legalid;
	
	@JsonProperty("incomeflag")
	private String incomeflag;
	
	@JsonProperty("recordstatus")
	private String recordstatus;
	
	@JsonProperty("branchId")
	private String branchId;
	
}