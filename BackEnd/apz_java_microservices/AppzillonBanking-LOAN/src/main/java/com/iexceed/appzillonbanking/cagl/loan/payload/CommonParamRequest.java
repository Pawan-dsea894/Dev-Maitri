package com.iexceed.appzillonbanking.cagl.loan.payload;

import com.fasterxml.jackson.annotation.JsonProperty;

public class CommonParamRequest {
	
	@JsonProperty("interfaceName")
	private String interfaceName;
	
	@JsonProperty("appId")
	private String appId;
	
	@JsonProperty("requestObj")
	private CommonParamRequestFields requestObj;

	public String getInterfaceName() {
		return interfaceName;
	}

	public void setInterfaceName(String interfaceName) {
		this.interfaceName = interfaceName;
	}

	public String getAppId() {
		return appId;
	}

	public void setAppId(String appId) {
		this.appId = appId;
	}

	public CommonParamRequestFields getRequestObj() {
		return requestObj;
	}

	public void setRequestObj(CommonParamRequestFields requestObj) {
		this.requestObj = requestObj;
	}

	@Override
	public String toString() {
		return "CommonParamRequest [interfaceName=" + interfaceName + ", appId=" + appId + ", requestObj=" + requestObj + "]";
	}
}
