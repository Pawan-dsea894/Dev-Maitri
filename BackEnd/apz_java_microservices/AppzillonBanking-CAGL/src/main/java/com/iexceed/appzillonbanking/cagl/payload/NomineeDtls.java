package com.iexceed.appzillonbanking.cagl.payload;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class NomineeDtls {

	@JsonProperty("nomineeName")
	private String nomineeName;

	@JsonProperty("nomineeRelationship")
	private String nomineeRelationship;

	@JsonProperty("nomineeDob")
	private String nomineeDob;

	@JsonProperty("guardianName")
	private String guardianName;

	@JsonProperty("guardianRelationship")
	private String guardianRelationship;

	@JsonProperty("guardianDob")
	private String guardianDob;

	@JsonProperty("nomineeMobile")
	private String nomineeMobile;

	@JsonProperty("nomineeEmail")
	private String nomineeEmail;

	@JsonProperty("guardianLandMark")
	private String guardianLandMark;

	@JsonProperty("guardianMobile")
	private String guardianMobile;

	@JsonProperty("guardianEmail")
	private String guardianEmail;
}
