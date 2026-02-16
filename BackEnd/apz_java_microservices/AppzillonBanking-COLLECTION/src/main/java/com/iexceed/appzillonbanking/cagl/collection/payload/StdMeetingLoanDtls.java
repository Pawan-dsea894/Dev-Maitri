package com.iexceed.appzillonbanking.cagl.collection.payload;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class StdMeetingLoanDtls {

	@JsonInclude(Include.NON_NULL)
	@JsonProperty("loanId")
	private String loanId;
	
	@JsonInclude(Include.NON_NULL)
	@JsonProperty("loanDue")
	private String loanDue;
	
	@JsonInclude(Include.NON_NULL)
	@JsonProperty("loanCollectionAmt")
	private String loanCollectionAmt;
}
