package com.iexceed.appzillonbanking.cagl.incomeassesment.repository.ab;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import com.iexceed.appzillonbanking.cagl.incomeassesment.domain.ab.OfficeData;

public interface OfficeDataRepository extends CrudRepository<OfficeData, String>{

	   @Query(value = "SELECT o.BRANCHID FROM t24_office o WHERE o.region_id = :regionId", nativeQuery = true)
	   List<String> findBranchIdsByRegionId(@Param("regionId") String regionId);

}
