package com.iexceed.appzillonbanking.logs.payload;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LargeData {

	private String refNo;
	private String data1;
	private String data2;
	private String data3;
	private String data4;
	private String data5;
	private int seqNo;
	
}

