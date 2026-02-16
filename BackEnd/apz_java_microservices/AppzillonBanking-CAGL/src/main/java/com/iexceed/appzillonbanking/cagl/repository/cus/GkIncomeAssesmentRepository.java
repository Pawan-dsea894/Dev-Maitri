package com.iexceed.appzillonbanking.cagl.repository.cus;

import java.util.List;

import org.springframework.data.repository.CrudRepository;

import com.iexceed.appzillonbanking.cagl.domain.cus.GkIncomeAssesment;

public interface GkIncomeAssesmentRepository extends CrudRepository<GkIncomeAssesment, String>{

	List<GkIncomeAssesment> findByCustomerId(String customerId);

}
