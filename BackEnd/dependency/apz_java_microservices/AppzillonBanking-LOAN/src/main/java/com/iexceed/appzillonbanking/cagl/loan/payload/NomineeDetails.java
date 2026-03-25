package com.iexceed.appzillonbanking.cagl.loan.payload;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class NomineeDetails {
	
	@JsonProperty("memRelation")
	private String memRelation;
	
	@JsonProperty("legaldocName")
	private String legaldocName;

	@JsonProperty("legaldocId")
	private String legaldocId;
	
	@JsonProperty("oCRResponselog")
	private OCRResponselog oCRresponselog;
	
	@JsonProperty("oCRData")
	private OCRData oCRData;
	
	@JsonProperty("inputData")
	private InputData inputData;
	
	@JsonProperty("docuNoF")
	private String docuNoF;
	
	@JsonProperty("docuNoB")
	private String docuNoB;
	
	  //RPC FLOW  
	@JsonProperty("isEdited")
	private String isEdited;

	@JsonProperty("isKycEdited")
	private String isKycEdited;

	@JsonProperty("isReUploadKM")
	private String isReUploadKM;
	
    @JsonProperty("reason")
	private List<Object> reason;
			
}
