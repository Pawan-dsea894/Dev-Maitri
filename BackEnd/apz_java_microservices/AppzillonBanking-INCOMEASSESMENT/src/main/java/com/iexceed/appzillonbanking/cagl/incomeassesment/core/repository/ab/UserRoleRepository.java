package com.iexceed.appzillonbanking.cagl.incomeassesment.core.repository.ab;

import org.springframework.data.repository.CrudRepository;

import com.iexceed.appzillonbanking.cagl.incomeassesment.core.domain.ab.UserRole;
import com.iexceed.appzillonbanking.cagl.incomeassesment.core.domain.ab.UserRoleId;

import java.util.Optional;

public interface UserRoleRepository extends CrudRepository<UserRole, UserRoleId> {

	Optional<UserRole> findByAppIdAndUserId(String appId, String userId);

}
