package com.iexceed.appzillonbanking.core.payload;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ConnectionPooler {

	private int maxTotalConnections;
	private int maxRouteConnections;
	private int maxLocalHostConnections;
	private int defaultKeepAliveTime;
	private int connectionTimeout;
	private int requestTimeout;
	private int socketTimeout;
	private int idleConnectionWaitTime;
	private int taskSchedulerPoolSize;
	private String hostIp;
	private int hostPortNumber;
	
}
