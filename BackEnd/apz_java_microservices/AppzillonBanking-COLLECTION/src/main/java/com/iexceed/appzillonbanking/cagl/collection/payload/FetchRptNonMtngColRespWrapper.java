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
public class FetchRptNonMtngColRespWrapper {

	@JsonProperty("collectionData")
	private List<FetchRptNonMtngColResponse> collectionData;
}
