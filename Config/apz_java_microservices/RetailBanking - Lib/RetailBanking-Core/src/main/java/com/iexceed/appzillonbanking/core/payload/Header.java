package com.iexceed.appzillonbanking.core.payload;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Header {
	
	private String userId;
	
	private String appId;
	
	private String interfaceId;
	
	private String deviceId;
	
	private String masterTxnRefNo;
	
	private String deviceType;

}
