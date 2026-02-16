package com.iexceed.appzillonbanking.cagl.incomeassesment.repository.ab;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.iexceed.appzillonbanking.cagl.incomeassesment.domain.ab.ApplicationMaster;
import com.iexceed.appzillonbanking.cagl.incomeassesment.domain.ab.ApplicationMasterId;

import jakarta.transaction.Transactional;

@Repository
public interface ApplicationMasterRepository extends CrudRepository<ApplicationMaster, ApplicationMasterId> {

	Optional<ApplicationMaster> findByAppIdAndApplicationIdAndVersionNumAndApplicationStatus(String appId,
			String applicationId, String versionNum, String status);

	Optional<ApplicationMaster> findByAppIdAndApplicationIdAndVersionNum(String appId, String applicationId,
			String versionNum);

	Optional<ApplicationMaster> findByApplicationId(String applicationId);
	
	@Transactional
	void deleteByApplicationId(String applicationId);

	@Transactional
	void deleteByApplicationIdAndVersionNum(String applicationId,String versionNum);

	
	@Query(value = "SELECT * FROM public.tb_uaco_application_master where customer_id=:customerId", nativeQuery = true)
	List<ApplicationMaster> findAllRecods(@Param("customerId") String customerId);

}