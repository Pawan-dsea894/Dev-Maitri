package com.iexceed.appzillonbanking.kendra.payload;

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
public class KendraDetailsUpdateRequestFields {
	
	@JsonProperty("fromBranch")
	private String fromBranch;
	
	@JsonProperty("kendraIndicator")
	private String kendraIndicator;
	
	@JsonProperty("toKM")
	private String toKM;
	
	@JsonProperty("Reason")
	private String Reason;
	
	@JsonProperty("branchId")
	private String branchId;

	@JsonProperty("KendraList")
	private List<KendraIds> KendraList;
	
	@JsonProperty("kendraIds")
	private List<KendraId> kendraIds;

	@JsonProperty("fromKM")
	private String fromKM;
	
}
