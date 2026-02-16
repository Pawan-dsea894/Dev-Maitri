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
public class FetchRptNonMtngColKendraDtls {
	
	@JsonProperty("id")
	private String id;
	
	@JsonProperty("name")
	private String name;
	
	@JsonProperty("startTime")
	private String startTime;
	
	@JsonProperty("meetingDate")
	private String meetingDate;
	
	@JsonProperty("meetingDay")
	private String meetingDay;
	
	@JsonProperty("applnDtls")
	private List<ApplicationDetails> applnDtls;
}
