package com.iexceed.appzillonbanking.cagl.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "mahi_lead")
@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
@Data
@ToString
@Builder
public class MahiLead {

	@Id
	@Column(name = "transaction_id", length = 100, nullable = false)
	private String transactionId;

	@Column(name = "branch_id", length = 100)
	private String branchId;

	@Column(name = "kendra", length = 100)
	private String kendra;

	@Column(name = "customer_id", length = 100)
	private String customerId;
	
	@Column(name = "customer_name" , length = 200)
	private String customerName;

	@Column(name = "product_name", length = 200)
	private String productName;

	@Column(name = "loan_purpose", length = 255)
	private String loanPurpose;

	@Column(name = "eligible_loan_amount", precision = 15, scale = 2)
	private BigDecimal eligibleLoanAmount;

	@Column(name = "lead_status", length = 100)
	private String leadStatus;

	@Column(name = "date_submitted")
	private LocalDateTime dateSubmitted;

	@Column(name = "lead_update_date")
	private LocalDateTime leadUpdateDate;

}
