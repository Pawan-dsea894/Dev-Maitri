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
public class KendraAssignmentFetchRequestWrapper {

	@JsonProperty("apiRequest")
	private KendraAssignmentFetchRequest apiRequest;
}
