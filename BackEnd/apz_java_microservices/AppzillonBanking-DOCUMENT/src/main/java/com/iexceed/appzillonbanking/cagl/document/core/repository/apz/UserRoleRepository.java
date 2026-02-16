package com.iexceed.appzillonbanking.cagl.document.core.repository.apz;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.iexceed.appzillonbanking.cagl.document.core.domain.apz.UserRole;
import com.iexceed.appzillonbanking.cagl.document.core.domain.apz.UserRoleId;

import java.util.Optional;

@Repository
public interface UserRoleRepository extends CrudRepository<UserRole, UserRoleId> {

	Optional<UserRole> findByAppIdAndUserId(String appId, String userId);

}
