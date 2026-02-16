package com.iexceed.appzillonbanking.cagl.repository.cus;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.iexceed.appzillonbanking.cagl.entity.GkUnifiedData;

public interface GkUnifiedDataRepository extends JpaRepository<GkUnifiedData, String> {

	
	//@Query("SELECT cus FROM GkUnifiedData cus WHERE cus.kendraId IN (:kendraId)")
//	List<GkUnifiedData> fetchCustomerDataNew(@Param("kendraId") List<Integer> kendraId);

	
	@Query("SELECT cus FROM GkUnifiedData cus WHERE cus.kendraId IN (:kendraId)")
	List<GkUnifiedData> fetchCustomerDataNew1(@Param("kendraId") List<Integer> kendraId);
	
	@Query("SELECT cus FROM GkUnifiedData cus WHERE cus.customerId =:customerId ")
	List<GkUnifiedData> fetchCustomerDataNewByCustomerId(@Param("customerId") String customerId);
	
}
