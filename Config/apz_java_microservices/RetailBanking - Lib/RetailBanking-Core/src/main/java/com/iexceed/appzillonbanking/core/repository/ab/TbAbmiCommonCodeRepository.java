package com.iexceed.appzillonbanking.core.repository.ab;

import java.util.List;
import java.util.Optional;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.iexceed.appzillonbanking.core.domain.ab.TbAbmiCommonCodeDomain;
import com.iexceed.appzillonbanking.core.domain.ab.TbAbmiCommonCodeId;

@Repository
public interface TbAbmiCommonCodeRepository extends CrudRepository<TbAbmiCommonCodeDomain, TbAbmiCommonCodeId> {

	//@Cacheable(value = "accessType", key = "#accessType")
	List<TbAbmiCommonCodeDomain> findAllByAccessType(String accessType);

	//@Cacheable(value = "findAllByCode", key = "#code")
	List<TbAbmiCommonCodeDomain> findAllByCode(String code);

	//@Cacheable(value = "codeAndAccessType")
	List<TbAbmiCommonCodeDomain> findAllByCodeAndAccessType(String code, String accessType);

	//@Cacheable(value = "findAllByCodeType", key = "#codeType")
	List<TbAbmiCommonCodeDomain> findAllByCodeType(String codeType);
	
	//@Cacheable(value = "cmCodeCache", key = "#cmCode")
	Optional<TbAbmiCommonCodeDomain> findByCode(String cmCode);
	
	Optional<TbAbmiCommonCodeDomain> findByCodeAndCodeType(String cmCode, String codeType);
}
