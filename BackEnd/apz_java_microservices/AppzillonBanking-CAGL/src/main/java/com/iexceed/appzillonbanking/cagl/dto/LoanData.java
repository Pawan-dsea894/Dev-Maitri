package com.iexceed.appzillonbanking.cagl.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class LoanData {
	

	private String loanId; 
	private String customerId;
	private String amount;  
	private String approvedAmt; 
	private String status; 
	private String freq; 
	private String term; 
	private String product;  
	private String lnValueDate; 
	private String lnMatDate; 
	private String interestRate; 
	private String overduePrincipal; 
	private String overdueInterest; 
	private String overDueStatus;
	private String outstandingPrincipal;
	
	// New colum has been added for passbook 
    private String loanPurpose;
    private String pf;
    private String GST;
    private String mem_insu;
    private String sp_insu;
    private String APR;	
    


}
