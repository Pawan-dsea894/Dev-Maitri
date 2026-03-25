package com.iexceed.appzillonbanking.kendra.repository.cus;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.iexceed.appzillonbanking.kendra.domain.cus.OfficeData;

public interface TOfficeDetailsRepo extends JpaRepository<OfficeData, String> {

	// @Query(value ="select distinct new LovMaster(lovMaster.lovName) FROM
	// LovMaster lovMaster where lovMaster.appId=:appId", nativeQuery = false)

	@Query(value = "SELECT DISTINCT zone, zone_id FROM t24_office", nativeQuery = true)
	List<String> findZoneNames();

	@Query(value = "select distinct AreaName, area_id  from t24_office;", nativeQuery = true)
	List<String> findAreaNames();

	@Query(value = "SELECT DISTINCT Branchname, BRANCHID FROM t24_office;", nativeQuery = true)
	List<String> findBranchNames();

	@Query(value = "select distinct region_name , region_id from t24_office;", nativeQuery = true)
	List<String> findRegionNames();

	@Query(value = "select distinct branchid from public.t24_office where area_id =:areaId", nativeQuery = true)
	List<String> findByAreaId(@Param("areaId") String areaId);
        // Approval Matrix Changes    
	@Query(value = "SELECT DISTINCT state, state_id FROM t24_office;", nativeQuery = true)
	List<String> findStateNames();

	/*
	@Query(value = "SELECT * FROM t24_office WHERE " +
			"state_id = :locationId OR " +
			"region_id = :locationId OR " +
			"zone_id = :locationId OR " +
			"area_id = :locationId OR " +
			"branchid = :locationId",
			nativeQuery = true)
	List<OfficeData> findByLocation(@Param("locationId") String locationId);
	*/
	
	@Query(value = "SELECT * FROM t24_office WHERE " +
	        "state_id IN (:locationId) OR " +
	        "region_id IN (:locationId) OR " +
	        "zone_id IN (:locationId) OR " +
	        "area_id IN (:locationId) OR " +
	        "branchid IN (:locationId)",
	        nativeQuery = true)
	List<OfficeData> findByLocation(@Param("locationId") List<String> locationId);


}
