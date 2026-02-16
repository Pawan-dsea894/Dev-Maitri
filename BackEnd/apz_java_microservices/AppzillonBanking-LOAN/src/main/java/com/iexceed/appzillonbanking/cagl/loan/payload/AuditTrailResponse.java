package com.iexceed.appzillonbanking.cagl.loan.payload;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AuditTrailResponse {

    private String appId;
	
	private String applicationId;
	
	private String userId;
	
	private String userRole;
	
	private String stageId;
	
	private String createTs;
	
	private String createDate;
	
	private String addInfo1;
	
	private String remarks;
	
	private String addInfo3;
	
	private String addInfo4;

	private String loanAmt;
	
	private String mobileNumber;
	
	private Object purpose;
	
	private Object repaymentFrequecy;
	
	private String branchId;
	
	private String payload;
	
	private String productId;
	
	private String customerId;
	
	private String kendreName;
	
	private String customerName;
	
	private String spouse;
	
	private String userName;
	
	private String kendraId;
	
	private String appVersion;
		
}
