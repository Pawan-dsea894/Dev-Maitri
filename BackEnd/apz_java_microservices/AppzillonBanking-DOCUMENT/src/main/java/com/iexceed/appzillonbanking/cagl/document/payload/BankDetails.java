package com.iexceed.appzillonbanking.cagl.document.payload;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class BankDetails {
    	
	@JsonProperty("bankAccNo")
	private String bankAccNo;

	@JsonProperty("bankAccName")
	private String bankAccName;
	
	@JsonProperty("bankBranchName")
	private String bankBranchName;
	
	@JsonProperty("bankName")
	private String bankName;

	@JsonProperty("bankIfscCode")
	private String bankIfscCode;
}
