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
public class CollectionDtls {
	
	@JsonProperty("collId")
	private String collId;
	
	@JsonProperty("collDate")
	private String collDate;
	
	@JsonProperty("groups")
	List<CollectionGroupDtls> collectionGroupDtls;
	
	@JsonProperty("totCollAmt")
	private int totCollAmt;
	
	@JsonProperty("totalAdv")
	private int totalAdv;
	
	@JsonProperty("totalDue")
	private int totalDue;
	
	@JsonProperty("netDue")
	private int netDue;
}
