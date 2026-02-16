package com.iexceed.appzillonbanking.kendra.repository.cus;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.iexceed.appzillonbanking.kendra.domain.cus.TbAshsUser;
import com.iexceed.appzillonbanking.kendra.domain.cus.TbAshsUserId;

public interface TbAshsUserRepo extends JpaRepository<TbAshsUser, TbAshsUserId> {

	public Optional<TbAshsUser> findByUserId(String userId);

}
