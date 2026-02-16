package com.iexceed.appzillonbanking.cagl.loan.repository.ab;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.iexceed.appzillonbanking.cagl.loan.domain.ab.OfficeData;

public interface TOfficeDetailsRepo extends JpaRepository<OfficeData, String> {

	// @Query(value ="select distinct new LovMaster(lovMaster.lovName) FROM
	// LovMaster lovMaster where lovMaster.appId=:appId", nativeQuery = false)

	@Query(value = "SELECT DISTINCT zone FROM t24_office", nativeQuery = true)
	List<String> findZoneNames();

	@Query(value = "select distinct AreaName, area_id  from t24_office;", nativeQuery = true)
	List<String> findAreaNames();

	@Query(value = "SELECT DISTINCT Branchname, BRANCHID FROM t24_office;", nativeQuery = true)
	List<String> findBranchNames();

	@Query(value = "select distinct region_name , region_id from t24_office;", nativeQuery = true)
	List<String> findRegionNames();

	@Query(value = "select distinct branchid from public.t24_office where area_id =:areaId", nativeQuery = true)
	List<String> findByAreaId(@Param("areaId") String areaId);
	
	// new query
	@Query(value = "select distinct branchid from public.t24_office where region_id =:regionId", nativeQuery = true)
	List<String> findByRegionId(@Param("regionId") String regionId);
	
	@Query(value = "select distinct branchid from public.t24_office where zone_id =:zoneId", nativeQuery = true)
	List<String> findByZoneId(@Param("zoneId") String zoneId);
	
	@Query(value = "select distinct branchid from public.t24_office where state_id =:stateId", nativeQuery = true)
	List<String> findByStateId(@Param("stateId") String stateId);
	


}
