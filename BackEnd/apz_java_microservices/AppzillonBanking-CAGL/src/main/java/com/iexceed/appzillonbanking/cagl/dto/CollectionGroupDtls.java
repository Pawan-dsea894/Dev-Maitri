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
public class CollectionGroupDtls {

	@JsonProperty("id")
	private String id;
	
	@JsonProperty("totalDue")
	private double totalDue;
	
	@JsonProperty("totalAdv")
	private double totalAdv;
	
	@JsonProperty("netDue")
	private double netDue;
	
	@JsonProperty("totCollAmt")
	private double totCollAmt;
	
	@JsonProperty("members")
	List<CollectionMemberDtls> collectionMemberDtls;
}
