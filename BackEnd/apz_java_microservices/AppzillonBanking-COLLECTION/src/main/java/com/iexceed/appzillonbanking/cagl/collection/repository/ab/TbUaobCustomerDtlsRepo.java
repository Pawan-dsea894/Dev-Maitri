package com.iexceed.appzillonbanking.cagl.collection.repository.ab;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import com.iexceed.appzillonbanking.cagl.collection.domain.ab.TbUaobCustDtls;
import com.iexceed.appzillonbanking.cagl.collection.domain.ab.TbUaobCustDtlsId;

public interface TbUaobCustomerDtlsRepo extends CrudRepository<TbUaobCustDtls, TbUaobCustDtlsId> {

	Optional<TbUaobCustDtls> findByApplicationId(String applicationId);

	Optional<TbUaobCustDtls> findByApplicationIdOrderByVersionNoDesc(String applicationId);

	List<TbUaobCustDtls> findByAppIdAndApplicationIdOrderByVersionNoDesc(String appId, String applnId);
	
	List<TbUaobCustDtls> findByAppIdAndApplicationIdAndKendraIdAndVersionNo(String appId, String applnId,
			String kendraId, String versionNo);
	
	List<TbUaobCustDtls> findByAppIdAndApplicationIdAndVersionNo(String appId, String applnId,
			String versionNo);
	
	Optional<TbUaobCustDtls> findByCustomerId(String memberId);

	@Query("SELECT t FROM TbUaobCustDtls t WHERE t.appId IN :appIds AND t.applicationId IN :applicationIds")
	List<TbUaobCustDtls> findAllByAppIdsAndApplicationIds(@Param("appIds")List<String> appIds, @Param("applicationIds")List<String> applicationIds);
}
