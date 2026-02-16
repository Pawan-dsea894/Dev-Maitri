package com.iexceed.appzillonbanking.kendra.repository.cus;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.iexceed.appzillonbanking.kendra.domain.cus.KendraManagementEntity;

public interface KendraRepo extends JpaRepository<KendraManagementEntity, String> {

//	@Query(value = "select kmid,kendra_id from tb_kendra_assignment where end_date between current_date and end_date and kmid in (:kList) ",nativeQuery = true)
	@Query(value = "SELECT kmid, old_kmid, kendra_id FROM tb_kendra_assignment WHERE start_date  <= current_date and end_date >=current_date AND kmid IN (:kList) AND (remarks NOT IN ('DELETED') OR remarks IS NULL)", nativeQuery = true)
	public List<KendraManagementEntity> fetchKendraMangerAsignList(List<String> kList);

	
//	@Query(value = "select kmid,kendra_id from tb_kendra_assignment where end_date between current_date and end_date and kmid in (:kList) ",nativeQuery = true)
	@Query(value = "SELECT * FROM tb_kendra_assignment WHERE start_date  <= current_date and end_date >=current_date AND old_kmid IN (:kList) AND (remarks NOT IN ('DELETED') OR remarks IS NULL)", nativeQuery = true)
	public List<KendraManagementEntity> fetchKendraMangerAsignListExclude(List<String> kList);
	
}
