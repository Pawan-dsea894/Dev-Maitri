package com.iexceed.appzillonbanking.cagl.repository.cus;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.iexceed.appzillonbanking.cagl.entity.MahiLead;

import java.util.List;

@Repository
public interface MahiLeadRepository extends JpaRepository<MahiLead, String> {
	
	@Query("SELECT lead FROM MahiLead lead where lead.kendra IN (:kendraId)")
	List<MahiLead> fetchMahiLeadByKendra(@Param("kendraId") List<String> kendraId);
	
	@Query(value = "SELECT count(*) FROM mahi_lead  where kendra=:kendraId",nativeQuery = true)
	int fetchMahiLeadCount(@Param("kendraId") String kendraId);

	@Query(value = "SELECT count(*) FROM mahi_lead  where lead_status='Pending' and kendra=:kendraId",nativeQuery = true)
	int fetchPendingLeadCount(@Param("kendraId") String kendraId);
		
}
