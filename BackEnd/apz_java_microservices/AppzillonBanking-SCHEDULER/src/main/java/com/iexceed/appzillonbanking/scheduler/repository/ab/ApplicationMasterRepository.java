package com.iexceed.appzillonbanking.scheduler.repository.ab;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.iexceed.appzillonbanking.scheduler.domain.ab.ApplicationMaster;
import com.iexceed.appzillonbanking.scheduler.domain.ab.ApplicationMasterId;

@Repository
public interface ApplicationMasterRepository extends CrudRepository<ApplicationMaster, ApplicationMasterId> {

	@Query(value = "select am.* from tb_uaco_application_master am "
			+ "inner join tb_uaob_cb_response cb on am.application_id = cb.application_id "
			+ "WHERE cb.status = 'FAILURE' AND (cb.retry_count >=5 ) "
			+ "AND NOT EXISTS (SELECT 1 FROM tb_uaob_cb_response cb2 WHERE cb2.application_id = am.application_id AND cb2.status = 'SUCCESS') "
			+ "UNION "
			+ "SELECT * FROM public.tb_uaco_application_master "
			+ "WHERE application_date < CURRENT_DATE - INTERVAL '30 days' AND application_type IS null AND application_status NOT IN ('DISBURSED','CANCELLED','REJECTED') "
			+ "", nativeQuery = true)
	Optional<List<ApplicationMaster>> findRecords();
	

	@Query(value = "SELECT   c.* FROM public.tb_uawf_appln_workflow a , public.tb_uaco_application_master c WHERE NOT EXISTS (     SELECT 1     FROM tb_uaob_cb_response b     WHERE b.application_id = a.application_id ) AND a.application_id = c.application_id AND c.application_type is null AND c.application_status NOT IN ('CANCELLED','REJECTED') AND c.application_date < CURRENT_DATE - INTERVAL '8 days' UNION SELECT a.* FROM public.tb_uaco_application_master a JOIN tb_uaob_cb_response c ON a.application_Id = c.application_Id WHERE a.application_date < CURRENT_DATE - INTERVAL '8 days' AND (a.application_Type IS NULL OR a.application_Type = 'LOAN') AND ((c.status = 'FAILURE' OR c.cb_check_status = 'FAILURE') AND a.application_Status NOT IN ('DISBURSED', 'REJECTED')) AND NOT (c.status = 'SUCCESS' AND c.cb_check_status = 'SUCCESS' AND a.application_Status = 'INITIATE') ", nativeQuery = true)
	Optional<List<ApplicationMaster>> findRejectRecords();

}