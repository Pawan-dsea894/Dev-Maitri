package com.iexceed.appzillonbanking.kendra.assignment.repo;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.iexceed.appzillonbanking.kendra.assignment.entty.KendraManagementEntity;

public interface KendraRepo extends JpaRepository<KendraManagementEntity, String> {
	
	@Query(value = "select kmid,kendra_id from tb_kendra_assignment where end_date between current_date and end_date and kmid in (:kList) ",nativeQuery = true)
	public List<KendraManagementEntity> fetchKendraMangerAsignList(List<String> kList);

}
