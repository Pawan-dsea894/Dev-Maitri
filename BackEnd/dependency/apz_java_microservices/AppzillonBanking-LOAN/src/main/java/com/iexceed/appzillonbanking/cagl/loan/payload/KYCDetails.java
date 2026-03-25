package com.iexceed.appzillonbanking.cagl.loan.payload;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class KYCDetails {

	@JsonProperty("dob")
	private String dob;

	@JsonProperty("maritalStatus")
	private String maritalStatus;

	@JsonProperty("memRelation")
	private String memRelation;

	@JsonProperty("mobileNum")
	private String mobileNum;

	@JsonProperty("primaryId")
	private String primaryId;

	@JsonProperty("primaryType")
	private String primaryType;

	@JsonProperty("vintage")
	private String vintage;
	
	@JsonProperty("stateBranch")
	private String stateBranch;
	
	@JsonProperty("activationDate")
	private String activationDate;
	
	// KYC Details changes
	@JsonProperty("depDob")
	private String depDob;
	
	@JsonProperty("depDocId")
	private String depDocId;
	
	@JsonProperty("depDocType")
	private String depDocType;
	
	@JsonProperty("depname")
	private String depname;
	
	@JsonProperty("custFlag")
	private String custFlag;
	
	// MobileNo update cases
	@JsonProperty("ismobUpdated")
	private String ismobUpdated;
	
	@JsonProperty("UpdatedOn")
	private String UpdatedOn;
		
}
