package com.iexceed.appzillonbanking.cagl.document.repository.ab;

import com.iexceed.appzillonbanking.cagl.document.domain.ab.TbAbobCommonCodeDomain;
import com.iexceed.appzillonbanking.cagl.document.domain.ab.TbAbobCommonCodeId;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TbAbobCommonCodeRepository extends CrudRepository<TbAbobCommonCodeDomain, TbAbobCommonCodeId> {
	
	List<TbAbobCommonCodeDomain> findAllByCodeAndAccessType(String code, String accessType);
	
	List<TbAbobCommonCodeDomain> findAllByAccessType(String accessType);
	
	List<TbAbobCommonCodeDomain> findAllByCode(String code);
	
	Optional<TbAbobCommonCodeDomain> findByCode(String cmCode);
	
	@Query(value = "SELECT c.CODE_DESC FROM TB_ABOB_COMMON_CODES c WHERE c.CM_CODE ='APPROVALMATRIX'",nativeQuery = true)
	String fetchCodeDesc();
}