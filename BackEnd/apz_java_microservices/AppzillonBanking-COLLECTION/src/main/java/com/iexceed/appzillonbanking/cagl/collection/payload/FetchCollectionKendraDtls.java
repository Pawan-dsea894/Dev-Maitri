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
public class FetchCollectionKendraDtls {
	
	@JsonProperty("id")
	private String id;
	
	@JsonProperty("name")
	private String name;
	
	@JsonProperty("startTime")
	private String startTime;
	
	@JsonProperty("meetingDate")
	private String meetingDate;
	
	@JsonProperty("meetingDay")
	private String meetingDay;
	
	@JsonProperty("lat")
	private String lat;
	
	@JsonProperty("long")
	private String longitude;
	
	@JsonProperty("branchId")
	private String branchId;
	
	@JsonProperty("applnDtls")
	private ApplicationDetails applnDtls;
	
	@JsonProperty("applnWFDtls")
	private List<ApplicationWFDetails> applnWFDtls;
	
	@JsonProperty("colldtls")
	private CollectionDtls colldtls;
	
	@JsonProperty("cashDepositPoints")
	private List<CashDepositPoints> cashDepositPoints;
	
	@JsonProperty("type")
	private String type;
}
