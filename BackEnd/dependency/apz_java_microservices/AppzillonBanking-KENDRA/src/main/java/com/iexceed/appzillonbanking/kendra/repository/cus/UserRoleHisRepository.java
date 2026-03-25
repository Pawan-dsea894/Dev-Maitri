package com.iexceed.appzillonbanking.kendra.repository.cus;

import com.iexceed.appzillonbanking.kendra.domain.cus.UserRoleHis;
import com.iexceed.appzillonbanking.kendra.domain.cus.UserRoleHisId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRoleHisRepository extends JpaRepository<UserRoleHis, UserRoleHisId> {
   
	
}
