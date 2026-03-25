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
public class OCRData {

	@JsonProperty("name")
	private String name;
	
	@JsonProperty("dob")
	private String dob; 
	
	@JsonProperty("memRelation")
	private String memRelation;
	
	@JsonProperty("legaldocName")
	private String legaldocName;
	
	@JsonProperty("legaldocId")
	private String legaldocId;
	
	@JsonProperty("mobileNum")
	private String mobileNum;
	
	@JsonProperty("gender")
	private String gender;
	
} 
