package com.iexceed.appzillonbanking.cbs.repository.ab;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import com.iexceed.appzillonbanking.cbs.domain.ab.ApplicationMaster;
import com.iexceed.appzillonbanking.cbs.domain.ab.ApplicationMasterId;

import jakarta.transaction.Transactional;

@Repository
public interface ApplicationMasterRepository extends CrudRepository<ApplicationMaster, ApplicationMasterId> {
	
	List<ApplicationMaster> findAllByApplicationId(String applicationId);
      
         // this is for DisbursementStatus API check
	List<ApplicationMaster> findByApplicationId(String applicationId);
	
        // for charge&BreakUpDetails needed to update Approved_Loan_Amount in master table
	@Modifying
	@Transactional
	@Query(value = "UPDATE TB_UACO_APPLICATION_MASTER  SET AMOUNT = :amount WHERE APPLICATION_ID = :applicationId ", nativeQuery = true)
	void updateApplicationAmount(@Param("amount") BigDecimal amount, @Param("applicationId") String applicationId);
	
	
}
