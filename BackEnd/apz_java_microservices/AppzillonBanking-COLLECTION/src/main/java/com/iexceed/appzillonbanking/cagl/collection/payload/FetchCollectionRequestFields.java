package com.iexceed.appzillonbanking.cagl.collection.payload;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class FetchCollectionRequestFields {

	@JsonProperty("kendraId")
	private String kendraId;

	@JsonProperty("applicationId")
	private String applnId;
	
	@JsonProperty("refNo")
	private String refNo;

	@JsonProperty("branchId")
	private String branchId;

	@JsonProperty("versionNo")
	private String versionNo;
	
	@JsonProperty("meetingDate")
	private String meetingDate;
	
	@JsonProperty("applicationType")
	private String applicationType;
	
	@JsonProperty("kmUserRole")
	private String kmUserRole;
	
	@JsonProperty("repType")
	private String repType;
}
