package com.iexceed.appzillonbanking.cagl.repository.ab;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.iexceed.appzillonbanking.cagl.domain.ab.ApplicationMaster;
import com.iexceed.appzillonbanking.cagl.domain.ab.ApplicationMasterId;

@Repository
public interface ApplicationMasterRepository
		extends CrudRepository<ApplicationMaster, ApplicationMasterId> {
}