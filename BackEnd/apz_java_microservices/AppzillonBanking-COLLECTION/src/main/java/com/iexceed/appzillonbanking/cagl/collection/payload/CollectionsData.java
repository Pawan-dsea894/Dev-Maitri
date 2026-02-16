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
public class CollectionsData {

	@JsonProperty("kendraId")
	private String kendraId;
	
	@JsonProperty("branchId")
	private String branchId;
	
	@JsonProperty("kendraName")
	private String kendraName;
	
	@JsonProperty("meetingDate")
	private String meetingDate;
	
	@JsonProperty("meetingDay")
	private String meetingDay;
	
	@JsonProperty("startTime")
	private String startTime;
	
	@JsonProperty("kmId")
	private String kmId;
	
	@JsonProperty("lat")
	private String lat;
	
	@JsonProperty("long")
	private String longitude;
	
	@JsonProperty("updLoc")
	private String updLoc;
	
	@JsonProperty("colldtls")
	private CollectionDtls colldtls;
	
	@JsonProperty("applnDtls")
	private ApplicationDetails applnDtls;
	
	@JsonProperty("applnWFDtls")
	private List<ApplicationWFDetails> applnWFDtls;
	
	@JsonProperty("type")
	private String type;
}
