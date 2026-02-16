package com.iexceed.appzillonbanking.cagl.loan.repository.ab;

import java.util.Optional;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import com.iexceed.appzillonbanking.cagl.loan.domain.ab.TbUaobIncomeAssessment;
import com.iexceed.appzillonbanking.cagl.loan.domain.ab.TbUaobIncomeAssessmentId;

public interface TbUaobIncomeAssessmentRepository extends CrudRepository<TbUaobIncomeAssessment, TbUaobIncomeAssessmentId> {
	
	@Query(value = "SELECT * FROM public.tb_uaob_income_assessment WHERE application_id = :applicationId AND version_no = (SELECT MAX(version_no) FROM public.tb_uaob_income_assessment WHERE application_id = :applicationId)",nativeQuery = true)
	Optional<TbUaobIncomeAssessment> findByApplicationId(String applicationId);

}
