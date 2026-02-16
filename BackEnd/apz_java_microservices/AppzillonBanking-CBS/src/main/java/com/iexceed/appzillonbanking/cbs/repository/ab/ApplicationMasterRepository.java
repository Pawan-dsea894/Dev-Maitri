package com.iexceed.appzillonbanking.cbs.repository.ab;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import com.iexceed.appzillonbanking.cbs.domain.ab.ApplicationMaster;
import com.iexceed.appzillonbanking.cbs.domain.ab.ApplicationMasterId;

@Repository
public interface ApplicationMasterRepository extends CrudRepository<ApplicationMaster, ApplicationMasterId> {
	
	List<ApplicationMaster> findAllByApplicationId(String applicationId);
	
}