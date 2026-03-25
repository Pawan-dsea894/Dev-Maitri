package com.iexceed.appzillonbanking.cagl.loan.payload;

import java.sql.Timestamp;

public interface PrecloserResponse {

	String getRequestPayload();

	String getResponsePayload();

	Timestamp getApiReqTs();

	Timestamp getApiResTs();
	// need to return for preclose Response
	String getStatus();
	
	String getApiStatus();

}
