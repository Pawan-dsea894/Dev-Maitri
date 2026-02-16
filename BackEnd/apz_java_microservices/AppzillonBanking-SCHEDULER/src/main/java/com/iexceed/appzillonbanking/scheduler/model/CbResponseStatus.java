package com.iexceed.appzillonbanking.scheduler.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CbResponseStatus {
	
	private String applicationId;
	private String versionNo;
	private String userId;
	private String memberId ; 

}
