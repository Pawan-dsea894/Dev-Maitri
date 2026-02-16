package com.iexceed.appzillonbanking.cbs.repository.ab;

import java.util.Optional;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import com.iexceed.appzillonbanking.cbs.domain.ab.TbUaobCbResponse;
import com.iexceed.appzillonbanking.cbs.domain.ab.TbUaobCbResponseId;


public interface TbUaobCbResponseRepository extends CrudRepository<TbUaobCbResponse, TbUaobCbResponseId> {

	Optional<TbUaobCbResponse> findByAppIdAndApplicationIdOrderByVersionNumDesc(String appId, String applicationId);

	@Query(value = "SELECT * FROM TB_UAOB_CB_RESPONSE WHERE APPLICATION_ID =:applicationId ORDER BY REQ_TS DESC LIMIT 1", nativeQuery = true)
	TbUaobCbResponse findByAppIdAndApplicationId(@Param("applicationId") String applicationId);

	
}
