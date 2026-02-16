package com.iexceed.appzillonbanking.cagl.loan.repository.ab;



import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.iexceed.appzillonbanking.cagl.loan.domain.ab.LockCustomerDtls;
import com.iexceed.appzillonbanking.cagl.loan.domain.ab.LockCustomerDtlsPK;

import jakarta.transaction.Transactional;


@Repository
public interface TbLockCustomerRepository extends CrudRepository<LockCustomerDtls, LockCustomerDtlsPK> {
	
	@Query(value = "SELECT * FROM tb_lock_customer WHERE created_ts >= NOW() - INTERVAL '3 minutes' and application_id=:applicationId", nativeQuery = true)
	public LockCustomerDtls fetchApplications(String applicationId);

	@Transactional
	void deleteByApplicationId(String applicationId);


}
