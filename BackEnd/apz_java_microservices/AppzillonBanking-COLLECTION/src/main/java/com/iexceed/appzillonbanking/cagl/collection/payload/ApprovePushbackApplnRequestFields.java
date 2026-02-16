package com.iexceed.appzillonbanking.cagl.collection.payload;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
//@AllArgsConstructor
//@NoArgsConstructor
public class ApprovePushbackApplnRequestFields {

	@JsonProperty("applicationId")
	private String applicationId;

	@JsonProperty("kmId")
	private String kmId;
	
	@JsonProperty("kmUserRole")
	private String kmUserRole;

	@JsonProperty("kendraIds")
	private String kendraIds;
	
	@JsonProperty("kmUserName")
	private String kmUserName;
	
	@JsonProperty("versionNo")
	private String versionNo;
	
	@JsonProperty("remarks")
	private String remarks;
	
	@JsonProperty("createTs")
	private String createTs;
}
