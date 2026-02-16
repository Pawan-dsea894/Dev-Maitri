package com.iexceed.appzillonbanking.cagl.loan.payload;

import com.fasterxml.jackson.annotation.JsonProperty;

public class CommonParamRequestWrapper {
	
	@JsonProperty("apiRequest")
	private CommonParamRequest apiRequest;
	
	public CommonParamRequestWrapper() {
	}

	public CommonParamRequestWrapper(CommonParamRequest apiRequest) {
		super();
		this.apiRequest = apiRequest;
	}

	public CommonParamRequest getApiRequest() {
		return apiRequest;
	}

	public void setApiRequest(CommonParamRequest apiRequest) {
		this.apiRequest = apiRequest;
	}

	@Override
	public String toString() {
		return "CommonParamRequestWrapper [apiRequest=" + apiRequest + "]";
	}
}
