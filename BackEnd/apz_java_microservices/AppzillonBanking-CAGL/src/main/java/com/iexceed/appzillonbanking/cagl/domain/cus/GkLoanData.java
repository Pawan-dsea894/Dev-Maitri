package com.iexceed.appzillonbanking.cagl.domain.cus;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "gk_loan_data")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class GkLoanData {
	
	@Id
	@Column(name = "loan_id")
	private String loanId; 
	
	@Column(name = "customerId")
	private String customerId;
	
	private String amount;  
	
	@Column(name = "approved_amt")
	private String approvedAmt; 
	
	private String status; 
	
	private String freq; 
	
	private String term; 

	private String product;
	
	@Column(name = "ln_Value_Date")
	private String lnValueDate; 
	
	@Column(name = "ln_mat_date")
	private String lnMatDate; 
	
	@Column(name = "interest_rate")
	private String interestRate; 
	
	@Column(name = "overdue_principal")
	private String overduePrincipal; 
	
	@Column(name = "overdue_interest")
	private String overdueInterest; 
	
	@Column(name = "overdue_status")
	private String overDueStatus;
	
	@Column(name = "outstanding_principal")
	private String outstandingPrincipal;
	
	@Column(name = "doc_ref_num")
	private String docRefNum;

}