package com.iexceed.appzillonbanking.cagl.repository.cus;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.iexceed.appzillonbanking.cagl.domain.cus.GkEligibleLoans;

public interface EligibleLoanRepository extends JpaRepository<GkEligibleLoans, String> {
	
	public List<GkEligibleLoans> findByCustomerId(String custId);
	
	@Query(value = "SELECT elig.Overall_CB_Eligible_amount AS cbAmt, elig.Eligible_CAGL_AMT AS caglAmt,"
			+ " (SELECT pm.product_type FROM gk_m_loan_product pm WHERE pm.SHORT_DESCRIPTION = elig.Eligible_CAGL_Product) AS productType,"
			+ " (SELECT pm.product_id FROM gk_m_loan_product pm WHERE pm.SHORT_DESCRIPTION = elig.Eligible_CAGL_Product) AS product"
			+ " FROM cust_loan_eligible elig WHERE elig.customerid = :custId", nativeQuery = true)
	List<Object[]> findLoanEligibleDetailsByCustomerId(@Param("custId") String custId);
}
