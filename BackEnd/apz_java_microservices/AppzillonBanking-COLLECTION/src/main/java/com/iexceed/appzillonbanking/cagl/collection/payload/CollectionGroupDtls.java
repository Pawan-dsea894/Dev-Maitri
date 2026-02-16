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
public class CollectionGroupDtls {

	@JsonProperty("id")
	private String id;
	
	@JsonProperty("totalDue")
	private int totalDue;
	
	@JsonProperty("totalAdv")
	private int totalAdv;
	
	@JsonProperty("netDue")
	private int netDue;
	
	@JsonProperty("totCollAmt")
	private int totCollAmt;
	
	@JsonProperty("parAmt")
	private int parAmt;
	
	@JsonProperty("members")
	private List<CollectionMemberDtls> members;
}
