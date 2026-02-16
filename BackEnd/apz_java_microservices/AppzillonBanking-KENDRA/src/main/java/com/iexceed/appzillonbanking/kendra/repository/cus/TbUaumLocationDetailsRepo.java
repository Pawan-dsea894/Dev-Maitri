package com.iexceed.appzillonbanking.kendra.repository.cus;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.iexceed.appzillonbanking.kendra.domain.cus.LocationMappingDetails;

public interface TbUaumLocationDetailsRepo extends JpaRepository<LocationMappingDetails, String> {

	public Optional<LocationMappingDetails> findByUserId(String userId);
	
	@Query(value = "SELECT (CAST(location_details AS jsonb))->0->>'locID' AS locID " + "FROM tb_uaum_location_details "
			+ "WHERE user_id =:userId", nativeQuery = true)
	String fetchBranchID(@Param("userId") String userId);
  

}
