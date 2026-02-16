package com.iexceed.appzillonbanking.cagl.repository.cus;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.iexceed.appzillonbanking.cagl.domain.cus.GkLoanData;

@Repository
public interface GkLoanDataRepo extends CrudRepository<GkLoanData, String> {

	List<GkLoanData> findByCustomerIdIn(List<String> customerId);

	@Query(value = "SELECT k FROM GkLoanData k JOIN GkCustDemoData c ON k.customerId = c.customerId WHERE c.groupId IN (:groupId)", nativeQuery = true)
	List<GkLoanData> findByGroupId(@Param("groupId") List<String> groupId);
	
	List<GkLoanData> findByCustomerId(String customerId);
}