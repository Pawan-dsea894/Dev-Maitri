package com.iexceed.appzillonbanking.cagl.loan.repository.ab;

import java.util.Optional;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import com.iexceed.appzillonbanking.cagl.loan.domain.ab.TbUacoInsuranceDtls;
import com.iexceed.appzillonbanking.cagl.loan.domain.ab.TbUacoInsuranceDtlsId;

public interface TbUacoInsuranceDtlsRepository extends CrudRepository<TbUacoInsuranceDtls, TbUacoInsuranceDtlsId> {

	Optional<TbUacoInsuranceDtls> findByApplicationId(String applicationId);

	
	@Modifying
	@Transactional
	@Query(value = "UPDATE TB_UACO_INSURANCE_DETAILS SET  payload =:payload WHERE APPLICATION_ID =:applicationId", nativeQuery = true)
	int updateInsuranceValuesPostRetrigger(@Param("payload") String payload, 
	                             @Param("applicationId") String applicationId);
	
}
