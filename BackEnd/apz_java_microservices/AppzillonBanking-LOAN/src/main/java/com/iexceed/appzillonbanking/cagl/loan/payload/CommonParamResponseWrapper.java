package com.iexceed.appzillonbanking.cagl.loan.payload;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.iexceed.appzillonbanking.core.payload.ResponseHeader;

import java.util.List;

public class CommonParamResponseWrapper {
	
	@JsonProperty("ResponseHeader")
	private ResponseHeader responseHeader;

	@JsonProperty("commonParamRes")
	private List<CommonParamResponse> commonParamResponse;

	public ResponseHeader getResponseHeader() {
		return responseHeader;
	}

	public void setResponseHeader(ResponseHeader responseHeader) {
		this.responseHeader = responseHeader;
	}

	public List<CommonParamResponse> getCommonParamResponse() {
		return commonParamResponse;
	}

	public void setCommonParamResponse(List<CommonParamResponse> commonParamResponse) {
		this.commonParamResponse = commonParamResponse;
	}

}
