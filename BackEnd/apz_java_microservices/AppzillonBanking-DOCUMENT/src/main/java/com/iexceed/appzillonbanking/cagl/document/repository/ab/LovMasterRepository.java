package com.iexceed.appzillonbanking.cagl.document.repository.ab;

import com.iexceed.appzillonbanking.cagl.document.domain.ab.LovMaster;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.List;
import java.util.Optional;


public interface LovMasterRepository extends CrudRepository<LovMaster, Integer> {

	Optional<LovMaster> findByAppIdAndLanguageAndLovName(String appId, String language, String lovName);
	
	@Query(value ="select distinct new LovMaster(lovMaster.lovName) FROM LovMaster lovMaster where lovMaster.appId=:appId", nativeQuery = false)
	List<LovMaster> findDistinctLovs(String appId);

	Optional<LovMaster> findByLovNameAndLanguage(String lovName, String language);

	Optional<LovMaster> findTopByOrderByLovIdDesc();

	Optional<LovMaster> findByLovNameAndLanguageAndLovId(String lovName, String language, int lovId);

}
