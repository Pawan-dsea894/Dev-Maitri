package com.iexceed.appzillonbanking.cagl.payload;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class CustomerDetailsPayload {

	@JsonProperty("title")
	private String title;
	
	@JsonProperty("dob")
	private String dob;

	@JsonProperty("gender")
	private String gender;
	
	@JsonProperty("maritalStatus")
	private String maritalStatus;
	
	@JsonProperty("altMobileNumber")
	private String altMobileNumber;
	
	@JsonProperty("emailId")
	private String emailId;

	@JsonProperty("pan")
	private String pan;
	
	@JsonProperty("spouseName")
	private String spouseName;
	
}