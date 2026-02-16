package com.iexceed.appzillonbanking.cagl.incomeassesment.payload;

import java.time.LocalDateTime;

import org.hibernate.annotations.CreationTimestamp;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UpdateEarningRequestFields {

	@JsonProperty("id")
	private String id; 
	
	@JsonProperty("gkv")
	private Object gkv;
    
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
