package com.iexceed.appzillonbanking.cagl.payload;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class FetchLoanRequestFields {

	@JsonProperty("customerId")
	private List<String> customerId;
	
	@JsonProperty("groupId")
	private List<String> groupId;
	
	@JsonProperty("fetchBy")
	private String fetchBy;
}
