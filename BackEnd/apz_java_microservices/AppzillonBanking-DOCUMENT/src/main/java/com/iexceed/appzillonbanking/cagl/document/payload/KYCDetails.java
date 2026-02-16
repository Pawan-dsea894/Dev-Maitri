package com.iexceed.appzillonbanking.cagl.document.payload;

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
	

}
