package com.iexceed.appzillonbanking.core.repository.apz;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.iexceed.appzillonbanking.core.domain.apz.UserProfileDomainId;
import com.iexceed.appzillonbanking.core.domain.apz.UserProfileDomain;


@Repository
public interface UserProfileDomainRepository extends CrudRepository<UserProfileDomain, UserProfileDomainId>{

	Optional<UserProfileDomain> findByUserIdAndAppId(String userId, String appId);
	
	@Transactional
	@Modifying
	@Query("DELETE FROM UserProfileDomain u WHERE u.userId IN (:ids)")
	public void deleteUserByUserId(@Param("ids") List<String> ids);

	public List<UserProfileDomain> findByCustomerSegment(String customerSegment);
}
