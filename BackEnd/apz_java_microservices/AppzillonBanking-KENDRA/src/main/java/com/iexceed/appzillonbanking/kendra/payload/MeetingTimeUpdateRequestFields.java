package com.iexceed.appzillonbanking.kendra.payload;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MeetingTimeUpdateRequestFields {

	@JsonProperty("kendraId")
	private String kendraId;
	
	@JsonProperty("meetingTimeFrom")
	private String meetingTimeFrom;
	
	@JsonProperty("meetingTimeTo")
	private String meetingTimeTo;
	
	@JsonProperty("branchId")
	private String branchId;

}
