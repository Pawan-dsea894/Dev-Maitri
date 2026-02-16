package com.iexceed.appzillonbanking.cagl.loan.repository.ab;


import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import com.iexceed.appzillonbanking.cagl.loan.domain.ab.ApplicationMaster;
import com.iexceed.appzillonbanking.cagl.loan.domain.ab.TbUalnLoanDtls;
import com.iexceed.appzillonbanking.cagl.loan.domain.ab.TbUalnLoanDtlsId;

public interface TbUalLoanDtlsRepository extends CrudRepository<TbUalnLoanDtls, TbUalnLoanDtlsId> {

	Optional<TbUalnLoanDtls> findByApplicationId(String applicationId);

	List<TbUalnLoanDtls> findByApplicationIdIn(List<String> applicationIds);
	
	// update installmentDetails based on applicationId 

	@Modifying
	@Transactional
	@Query(value = "UPDATE TB_UALN_LOAN_DTLS SET INSTALLMENTDETAILS =:installmentDetails WHERE APPLICATION_ID =:applicationId", nativeQuery = true)
	int updateInstallmentDetails(@Param("installmentDetails") String installmentDetails, 
	                             @Param("applicationId") String applicationId);

	
	
	@Modifying
	@Transactional
	@Query(value = "UPDATE TB_UALN_LOAN_DTLS SET loan_amount =:loan_amount , payload =:payload WHERE APPLICATION_ID =:applicationId", nativeQuery = true)
	int updateValuesPostRetrigger(@Param("loan_amount") String loan_amount,@Param("payload") String payload, 
	                             @Param("applicationId") String applicationId);
	
	
	@Query(value = "SELECT * FROM TB_UALN_LOAN_DTLS d WHERE d.APPLICATION_ID =:applicationId", nativeQuery = true)
	Optional<TbUalnLoanDtls> fetchActiveLoanDetails(@Param("applicationId") String applicationId);

}
