package com.iexceed.appzillonbanking.cagl.loan.payload;

import com.fasterxml.jackson.annotation.JsonProperty;

public class CommonParamRequestFields {
	
	@JsonProperty("accessType")
	public String accessType;
	
	@JsonProperty("code")
	public String code;

	public String getAccessType() {
		return accessType;
	}

	public void setAccessType(String accessType) {
		this.accessType = accessType;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	@Override
	public String toString() {
		return "CommonParamRequestFields [accessType=" + accessType + ", code=" + code + "]";
	}
}
