package com.iexceed.appzillonbanking.cagl.loan.repository.ab;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import com.iexceed.appzillonbanking.cagl.loan.domain.ab.TbUaobCbResponse;
import com.iexceed.appzillonbanking.cagl.loan.domain.ab.TbUaobCbResponseId;

public interface TbUaobCbResponseRepository extends CrudRepository<TbUaobCbResponse, TbUaobCbResponseId> {

	Optional<TbUaobCbResponse> findByAppIdAndApplicationIdOrderByVersionNumDesc(String appId, String applicationId);

	@Query(value = "SELECT * FROM TB_UAOB_CB_RESPONSE WHERE APPLICATION_ID =:applicationId ORDER BY REQ_TS DESC LIMIT 1", nativeQuery = true)
	TbUaobCbResponse findByAppIdAndApplicationId(@Param("applicationId") String applicationId);

	Optional<TbUaobCbResponse> findByApplicationIdOrderByVersionNumDesc(String applicationId);

	Optional<TbUaobCbResponse> findTopByApplicationIdOrderByVersionNumDesc(String applicationId);

	Optional<TbUaobCbResponse> findTopByApplicationIdOrderByResTsDesc(String applicationId);

	@Query(value = "SELECT am.application_id " + "FROM public.tb_uaco_application_master am "
			+ "WHERE am.customer_id = :customerId and am.application_type is null " + "AND NOT EXISTS ( " + "SELECT 1 "
			+ "FROM public.tb_uaob_cb_response cr "
			+ "WHERE cr.application_id = am.application_id );", nativeQuery = true)
	List<String> getApplicationListAfterOTPDrop(String customerId);
}
