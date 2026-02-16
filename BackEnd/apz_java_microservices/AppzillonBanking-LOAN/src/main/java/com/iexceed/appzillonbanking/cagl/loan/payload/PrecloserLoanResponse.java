package com.iexceed.appzillonbanking.cagl.loan.payload;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.sql.Timestamp;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class PrecloserLoanResponse {
	private String requestPayload;
	private String responsePayload;
	private Timestamp apiReqTs;
	private Timestamp apiResTs;

}
