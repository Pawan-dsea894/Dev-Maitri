package com.iexceed.appzillonbanking.cagl.collection.payload;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class QRPaymentCBRequest {

	@JsonProperty("billNumber")
	private String billNumber;

	@JsonProperty("amount")
	private String amount;

	@JsonProperty("mobileNumber")
	private String mobileNumber;

	@JsonProperty("customerTransactionId")
	private String customerTransactionId;

	@JsonProperty("transactionstatus")
	private String transactionstatus;

	@JsonProperty("statuscode")
	private String statuscode;

	@JsonProperty("remarks")
	private String remarks;

}
