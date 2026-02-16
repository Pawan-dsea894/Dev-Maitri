package com.iexceed.appzillonbanking.cagl.payload;

import java.util.List;

import com.iexceed.appzillonbanking.cagl.dto.CustData;
import com.iexceed.appzillonbanking.cagl.dto.CustEarnings;
import com.iexceed.appzillonbanking.cagl.dto.IncomeAssesment;
import com.iexceed.appzillonbanking.cagl.dto.KendraData;
import com.iexceed.appzillonbanking.cagl.dto.LoanData;
import com.iexceed.appzillonbanking.cagl.dto.LoanEligible;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CdhDataResponse {
	
	List<KendraData> kendraList;
	List<CustData> custList;
	List<LoanData> loanList;
	List<LoanEligible> loanEligList;
	List<IncomeAssesment> incomeAssesList;
	List<CustEarnings> earningList;

}
