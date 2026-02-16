package com.iexceed.appzillonbanking.cagl.document.repository.ab;

import com.iexceed.appzillonbanking.cagl.document.domain.ab.ApplicationWorkflowHis;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ApplicationWorkflowHisRepository {
    @Query(value = "SELECT * FROM public.tb_uawf_appln_workflow a where a.application_id =:applicationId ORDER BY a.created_ts DESC LIMIT 1 ", nativeQuery = true)
    Optional<ApplicationWorkflowHis> findlatestWorkflowDetails(@Param("applicationId") String applicationId);

}
