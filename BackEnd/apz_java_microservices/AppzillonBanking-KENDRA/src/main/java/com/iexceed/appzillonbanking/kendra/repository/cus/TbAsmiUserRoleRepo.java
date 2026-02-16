package com.iexceed.appzillonbanking.kendra.repository.cus;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.iexceed.appzillonbanking.kendra.domain.cus.TbAsmiUserRole;
import com.iexceed.appzillonbanking.kendra.domain.cus.TbAsmiUserRoleId;

public interface TbAsmiUserRoleRepo extends JpaRepository<TbAsmiUserRole, TbAsmiUserRoleId> {

	public Optional<List<TbAsmiUserRole>> findByUserId(String userId);

}
