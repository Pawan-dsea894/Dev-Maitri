package com.iexceed.appzillonbanking.cagl.repository.cus;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.iexceed.appzillonbanking.cagl.domain.cus.GkIncomeAssesment;

public interface CustomerIncomeRepository extends JpaRepository<GkIncomeAssesment, String> {
	
	public List<GkIncomeAssesment> findByCustomerId(String custId);

}
