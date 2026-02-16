package com.iexceed.appzillonbanking.cagl.repository.cus;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.iexceed.appzillonbanking.cagl.domain.cus.GkLoanData;

public interface LoanDataRepository extends JpaRepository<GkLoanData, String> {
	
	public List<GkLoanData> findByCustomerId(String custId);

}
