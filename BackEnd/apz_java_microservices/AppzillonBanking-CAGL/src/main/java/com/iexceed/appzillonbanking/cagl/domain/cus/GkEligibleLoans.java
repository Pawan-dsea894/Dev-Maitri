package com.iexceed.appzillonbanking.cagl.domain.cus;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "cust_loan_eligible")
public class GkEligibleLoans {

	@Id
	@Column(name = "CUSTOMERID")
	private String customerId;

	@Column(name = "customername")
	private String customerName;

	@Column(name = "kendraid")
	private int kendraid;

	@Column(name = "kendraname")
	private String kendraname;

	@Column(name = "Overall_CB_Eligible_amount")
	private Double cbAmt;

	@Column(name = "Eligible_CAGL_AMT")
	private Double caglAmt;

	@Column(name = "Eligible_CAGL_Product")
	private String productType;

}
