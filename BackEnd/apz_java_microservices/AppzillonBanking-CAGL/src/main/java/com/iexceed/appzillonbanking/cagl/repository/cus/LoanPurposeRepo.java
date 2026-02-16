package com.iexceed.appzillonbanking.cagl.repository.cus;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.iexceed.appzillonbanking.cagl.domain.cus.GkLoanPurpose;

public interface LoanPurposeRepo extends JpaRepository<GkLoanPurpose, String> {
	
	public List<GkLoanPurpose> findByProductId(String prodId);

}
