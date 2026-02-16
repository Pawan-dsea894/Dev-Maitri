package com.iexceed.appzillonbanking.cagl.repository.cus;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import com.iexceed.appzillonbanking.cagl.domain.cus.GkMLoanProduct;

public interface GkMLoanRepository extends CrudRepository<GkMLoanProduct, String> {

	@Query("SELECT k FROM GkMLoanProduct k JOIN GkLoanData c ON k.productId = c.product WHERE c.customerId = :customerId")
	List<GkMLoanProduct> findByCustomerId(String customerId);
	
	@Query("SELECT k FROM GkMLoanProduct k")
	List<GkMLoanProduct> findAllLoanData();

}
