package com.iexceed.appzillonbanking.cagl.repository.cus;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.iexceed.appzillonbanking.cagl.dto.CustomerLoanDtlsDto;
import com.iexceed.appzillonbanking.cagl.entity.GkCustomerData;

public interface CustomerDataRepository extends JpaRepository<GkCustomerData, String> {

	public List<GkCustomerData> findByKendraId(int kendraId);

	public List<GkCustomerData> findByCustomerId(String custId);
	
	@Query("SELECT new com.iexceed.appzillonbanking.cagl.dto.CustomerLoanDtlsDto(cus, lns, el, em, pdt, ia) " 
			+ "FROM GkCustomerData cus "
			+ "LEFT JOIN GkLoanData lns ON cus.customerId = lns.customerId "
			+ "LEFT JOIN GkEligibleLoans el ON cus.customerId = el.customerId "
			+ "LEFT JOIN GkMLoanProduct pdt ON pdt.shortDesc = el.productType "
			+ "LEFT JOIN GkIncomeAssesment ia ON cus.customerId = ia.customerId "
			+ "LEFT JOIN GkEarningMember em ON cus.customerId = em.customerId "
			+ "WHERE cus.kendraId IN (:kendraId)")
	List<CustomerLoanDtlsDto> fetchCustomerData(@Param("kendraId") List<Integer> kendraId);
	
	List<GkCustomerData> findByKendraIdIn(List<Integer> kendraId);

}
