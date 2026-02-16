package com.iexceed.appzillonbanking.logs.payload;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LogData {

	private String txnRefNo;

	private String masterTxnRefNo;

	private String interfaceId;

	private String endpointType;

	private LocalDateTime stTm;

	private LocalDateTime endTm;

	private String status;

	private String request;

	private String response;

	private String deviceId;

	private String userId;

	private String appId;

	private String requestRefNo;

	private String responseRefNo;

	private int requestSize;

	private int responseSize;

}
