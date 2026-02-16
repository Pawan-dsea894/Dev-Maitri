package com.iexceed.appzillonbanking.cagl.dto;

import com.iexceed.appzillonbanking.cagl.domain.cus.GkEarningMember;
import com.iexceed.appzillonbanking.cagl.domain.cus.GkEligibleLoans;
import com.iexceed.appzillonbanking.cagl.domain.cus.GkIncomeAssesment;
import com.iexceed.appzillonbanking.cagl.domain.cus.GkLoanData;
import com.iexceed.appzillonbanking.cagl.domain.cus.GkMLoanProduct;
import com.iexceed.appzillonbanking.cagl.entity.GkCustomerData;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class CustomerLoanDtlsDto {

	private GkCustomerData customerData;
	
	private GkLoanData loanData;
	
	private GkEligibleLoans eligibleLoans;
	
	private GkEarningMember earningMember;
	
	private GkMLoanProduct loanProduct;
	
	private GkIncomeAssesment incomeAssesment;
}
