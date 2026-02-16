package com.iexceed.appzillonbanking.cagl.loan.repository.ab;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import com.iexceed.appzillonbanking.cagl.loan.domain.ab.LovMaster;


public interface LovMasterRepository extends CrudRepository<LovMaster, Integer> {

	Optional<LovMaster> findByAppIdAndLanguageAndLovName(String appId, String language, String lovName);
	
	@Query(value ="select distinct new LovMaster(lovMaster.lovName) FROM LovMaster lovMaster where lovMaster.appId=:appId", nativeQuery = false)
	List<LovMaster> findDistinctLovs(String appId);

	Optional<LovMaster> findByLovNameAndLanguage(String lovName, String language);

	Optional<LovMaster> findTopByOrderByLovIdDesc();

	Optional<LovMaster> findByLovNameAndLanguageAndLovId(String lovName, String language, int lovId);

}
