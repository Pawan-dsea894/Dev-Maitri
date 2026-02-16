package com.iexceed.appzillonbanking.cagl.incomeassesment.repository.ab;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import com.iexceed.appzillonbanking.cagl.incomeassesment.domain.ab.TbUaobIncomeAssessment;
import com.iexceed.appzillonbanking.cagl.incomeassesment.domain.ab.TbUaobIncomeAssessmentId;

public interface TbUaobIncomeAssessmentRepository
		extends CrudRepository<TbUaobIncomeAssessment, TbUaobIncomeAssessmentId> {
	
	Optional<TbUaobIncomeAssessment> findByApplicationId(String applicationId);

	@Query(value = "select * from public.tb_uaob_income_assessment where application_id=:applicationId",nativeQuery = true)
	Optional<List<TbUaobIncomeAssessment>> findByApplicationIdList(String applicationId);

	@Query(value = "select * from public.tb_uaob_income_assessment where updatedby=:userId",nativeQuery = true)
	Optional<List<TbUaobIncomeAssessment>> findByUpdatedBy(@Param("userId") String userId);
	
	

}
