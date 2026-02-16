package com.iexceed.appzillonbanking.cagl.collection.repository.ab;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.iexceed.appzillonbanking.cagl.collection.domain.ab.TbUacoKendraDtls;
import com.iexceed.appzillonbanking.cagl.collection.domain.ab.TbUacoKendraDtlsId;

@Repository
public interface TbUacoKendraDtlsRepository extends CrudRepository<TbUacoKendraDtls, TbUacoKendraDtlsId> {

	Optional<TbUacoKendraDtls> findTopByAppIdAndApplicationIdAndKendraIdOrderByVersionNumDesc(String appId,
			String applicationId, String kendraId);

	List<TbUacoKendraDtls> findByAppIdAndApplicationIdAndVersionNum(String appId, String applicationId,
			String versionNum);

	@Query("SELECT k FROM TbUacoKendraDtls k WHERE k.appId IN :appIds AND k.applicationId IN :applicationIds")
	List<TbUacoKendraDtls> findAllByAppIdsAndApplicationIds(@Param("appIds")List<String> appIds, @Param("applicationIds")List<String> applicationIds);
}
