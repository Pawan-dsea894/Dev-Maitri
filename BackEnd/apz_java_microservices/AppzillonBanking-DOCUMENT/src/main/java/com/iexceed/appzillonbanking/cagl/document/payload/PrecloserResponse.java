package com.iexceed.appzillonbanking.cagl.document.payload;

import java.sql.Timestamp;

public interface PrecloserResponse {

	String getRequestPayload();

	String getResponsePayload();

	Timestamp getApiReqTs();

	Timestamp getApiResTs();

}
