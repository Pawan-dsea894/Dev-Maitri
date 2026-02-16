package com.iexceed.appzillonbanking.kendra.payload;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UpdateKendraAddressDetailsRequestFields {
	
	@JsonProperty("kendraName")
	private String kendraName;
	
	@JsonProperty("kendraStatus")
	private String kendraStatus;
	
	@JsonProperty("kendraAddress")
	private List<KendhraAddress> kendraAddress;
	
	@JsonProperty("stateId")
	private String stateId;

	@JsonProperty("districtId")
	private String districtId;
	
	@JsonProperty("village")
	private String village;

	@JsonProperty("pinCode")
	private String pinCode;

	@JsonProperty("areaType")
	private String areaType;

	@JsonProperty("taluk")
	private String taluk;

	@JsonProperty("distance")
	private String distance;

	@JsonProperty("branch")
	private String branch;
	
	@JsonProperty("kendraId")
	private String kendraId;

	@JsonProperty("branchId")
	private String branchId;

}
