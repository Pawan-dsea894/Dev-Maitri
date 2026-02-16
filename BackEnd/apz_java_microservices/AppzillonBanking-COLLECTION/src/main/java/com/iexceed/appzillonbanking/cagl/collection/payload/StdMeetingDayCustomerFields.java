package com.iexceed.appzillonbanking.cagl.collection.payload;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class StdMeetingDayCustomerFields {
	
	@JsonProperty("customerId")
	private String customerId;
	
	@JsonProperty("cusCollectionAmt")
	private String cusCollectionAmt;
	
	@JsonProperty("cusFlag")
	private String cusFlag;
	
	@JsonProperty("cusAttendance")
	private String cusAttendance;
	
	@JsonProperty("cusFine")
	private String cusFine;
	
	@JsonProperty("cusUpiFlag")
	private String cusUpiFlag;
	
	@JsonInclude(Include.NON_EMPTY)
	@JsonProperty("loanDetails")
	private List<StdMeetingLoanDtls> loanDetails;
}
