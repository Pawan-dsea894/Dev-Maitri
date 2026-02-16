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
public class StdMeetingDayCustomerDtls {

	@JsonProperty("header")
	private Object header;
	
	@JsonProperty("body")
	private StdMeetingDayBody body;
	
	@JsonProperty("id")
	private String id;
	
	@JsonProperty("branchId")
	private String branchId;
}
