package com.iexceed.appzillonbanking.cagl.collection.payload;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CollectionAmountDtls {

	@JsonProperty("total")
	private int total;

	@JsonProperty("cashCol")
	private int cashCol;

	@JsonProperty("upiCol")
	private int upiCol;

	@JsonProperty("advAdj")
	@JsonInclude(Include.NON_NULL)
	private int advAdj;
}
