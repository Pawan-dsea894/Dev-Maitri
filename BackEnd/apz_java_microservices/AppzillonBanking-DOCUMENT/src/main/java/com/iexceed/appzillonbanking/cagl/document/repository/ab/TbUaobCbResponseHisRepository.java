package com.iexceed.appzillonbanking.cagl.document.repository.ab;

import com.iexceed.appzillonbanking.cagl.document.domain.ab.TbUaobCbResponseHis;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface TbUaobCbResponseHisRepository extends CrudRepository<TbUaobCbResponseHis, String> {

    Optional<TbUaobCbResponseHis> findTopByApplicationIdOrderByResTsDesc(String applicationId);

    @Query(value = "SELECT * FROM TB_UAOB_CB_RESPONSE_HISTORY WHERE APPLICATION_ID =:applicationId ORDER BY REQ_TS DESC LIMIT 1", nativeQuery = true)
    TbUaobCbResponseHis findByAppIdAndApplicationId(@Param("applicationId") String applicationId);

}
