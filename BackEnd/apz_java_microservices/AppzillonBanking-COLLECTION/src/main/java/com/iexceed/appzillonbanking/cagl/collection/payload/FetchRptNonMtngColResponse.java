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
public class FetchRptNonMtngColResponse {

	@JsonProperty("kmId")
	private String kmId;

	@JsonProperty("kmName")
	private String kmName;

	@JsonProperty("kendraCount")
	private int kendraCount;

	@JsonProperty("totalColAmt")
	private int totalColAmt;

	@JsonProperty("collStatus")
	private String collStatus;

	@JsonProperty("collInfo")
	private FetchCollectionInfo collInfo;

	@JsonProperty("kendraDtls")
	private List<FetchRptNonMtngColKendraDtls> kendraDtls;
	
	@JsonProperty("cashDepositPoints")
	private List<CashDepositPoints> cashDepositPoints;
}
