package com.iexceed.appzillonbanking.cagl.loan.payload;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CustomerNomineeExternalRequest {
	
	@JsonProperty("memRelation")
	private String memRelation;
	
	@JsonProperty("mobileNum")
	private String mobileNum;
	
	@JsonProperty("customer_id")
	private String customer_id;
	
	@JsonProperty("applicationId")
	private String applicationId;
	
	@JsonProperty("legaldocId")
	private String legaldocId;
	
	@JsonProperty("dob")
	private String dob;
	
	@JsonProperty("legaldocName")
	private String legaldocName;
	
	@JsonProperty("gender")
	private String gender;
	
	@JsonProperty("docuNoF")
	private String docuNoF;
	
	@JsonProperty("docuNoB")
	private String docuNoB;
	
	@JsonProperty("create_ts")
	private String create_ts;
	
	@JsonProperty("application_status")
	private String application_status;

}
