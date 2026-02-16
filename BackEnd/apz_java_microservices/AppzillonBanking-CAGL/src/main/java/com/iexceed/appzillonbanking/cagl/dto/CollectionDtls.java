package com.iexceed.appzillonbanking.cagl.dto;

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
public class CollectionDtls {
	
	@JsonProperty("collId")
	private String collId;
	
	@JsonProperty("collDate")
	private String collDate;
	
	@JsonProperty("cdhKmId")
	private String cdhKmId;
	
	@JsonProperty("groups")
	List<CollectionGroupDtls> collectionGroupDtls;
}
