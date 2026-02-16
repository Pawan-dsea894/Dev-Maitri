package com.iexceed.appzillonbanking.cagl.collection.payload;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
public class StdMeetingDayBody {
	
	@JsonProperty("customerDetails")
	private List<StdMeetingDayCustomerFields> customerDetails;
	
	@JsonProperty("kendraUpiTotal")
	private String kendraUpiTotal;
	
	@JsonProperty("kendraCashTotal")
	private String kendraCashTotal;
	
	@JsonProperty("feeCollAmt")
	private String feeCollAmt;
	
	@JsonProperty("totalDisbAmt")
	private String totalDisbAmt;
	
	@JsonProperty("totalFineColl")
	private String totalFineColl;
	
	@JsonProperty("netCollection")
	private String netCollection;
	
	@JsonProperty("netCollectionUPI")
	private String netCollectionUPI;
	
	@JsonProperty("netCollectionCash")
	private String netCollectionCash;
	
	@JsonProperty("initiatedBy")
	private String initiatedBy;
	
	@JsonProperty("verifiedBy")
	private String verifiedBy;
	
	@JsonProperty("authorizedBy")
	private String authorizedBy;
	
	@JsonProperty("referenceId")
	private String referenceId;
}
