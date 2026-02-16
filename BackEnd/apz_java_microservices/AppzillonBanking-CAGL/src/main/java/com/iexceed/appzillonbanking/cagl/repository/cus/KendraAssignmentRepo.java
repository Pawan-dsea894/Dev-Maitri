package com.iexceed.appzillonbanking.cagl.repository.cus;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.iexceed.appzillonbanking.cagl.entity.GkKendraAssignmentEntity;

public interface KendraAssignmentRepo extends JpaRepository<GkKendraAssignmentEntity, String> {
	
	@Query(value = "select kmid,kendra_id from tb_kendra_assignment where end_date between current_date and end_date and kmid = :kmid",nativeQuery = true)
	public List<GkKendraAssignmentEntity> fetchKendraMangerAsignList(String kmid);

}
