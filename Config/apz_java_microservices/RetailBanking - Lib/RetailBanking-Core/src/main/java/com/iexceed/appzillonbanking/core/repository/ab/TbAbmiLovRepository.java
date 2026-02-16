package com.iexceed.appzillonbanking.core.repository.ab;

import java.util.List;
import java.util.Optional;

import org.springframework.data.repository.CrudRepository;

import com.iexceed.appzillonbanking.core.domain.ab.TbAbmiLovMaster;
import com.iexceed.appzillonbanking.core.domain.ab.TbAbmiLovMasterId;

public interface TbAbmiLovRepository extends CrudRepository<TbAbmiLovMaster, TbAbmiLovMasterId> {

	Optional<TbAbmiLovMaster> findByLovNameAndLanguage(String lovName, String language);

	List<TbAbmiLovMaster> findAllByAppId(String appId);
		
	TbAbmiLovMaster findTopByOrderByLovIdDesc();

    void deleteByLovNameIn(List<String> lovNames);

}
