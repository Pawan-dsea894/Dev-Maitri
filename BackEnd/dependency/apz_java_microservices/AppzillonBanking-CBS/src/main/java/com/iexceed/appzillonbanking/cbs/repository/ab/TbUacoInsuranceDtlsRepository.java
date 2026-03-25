package com.iexceed.appzillonbanking.cbs.repository.ab;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import com.iexceed.appzillonbanking.cbs.domain.ab.TbUacoInsuranceDtls;
import com.iexceed.appzillonbanking.cbs.domain.ab.TbUacoInsuranceDtlsId;
import jakarta.transaction.Transactional;

public interface TbUacoInsuranceDtlsRepository extends JpaRepository<TbUacoInsuranceDtls, TbUacoInsuranceDtlsId> {
	
        // this is for charge&BreakUpDetails need to update insurDtls in Insurance table 
	@Modifying
	@Transactional
	@Query(value = "UPDATE TB_UACO_INSURANCE_DETAILS SET  payload =:payload WHERE APPLICATION_ID =:applicationId", nativeQuery = true)
	int updateInsuranceValuesPostRetrigger(@Param("payload") String payload, 
	                             @Param("applicationId") String applicationId);

}
