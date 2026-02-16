package com.iexceed.appzillonbanking.logs.repository.apz;

import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.iexceed.appzillonbanking.logs.domain.apz.TbAsmiSecurityParams;

@Repository
public interface TbAsmiSecurityParamsRepository extends JpaRepository<TbAsmiSecurityParams, String>, JpaSpecificationExecutor<TbAsmiSecurityParams>{
	
	@Cacheable(value = "securityParams", key = "#appId")
	@Query("SELECT c FROM TbAsmiSecurityParams as c WHERE c.appId =:appId")
	TbAsmiSecurityParams findSecurityParamsbyAppId(@Param("appId") String appId);
}
