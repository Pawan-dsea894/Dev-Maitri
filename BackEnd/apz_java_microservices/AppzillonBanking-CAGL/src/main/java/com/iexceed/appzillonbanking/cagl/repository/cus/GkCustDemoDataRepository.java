package com.iexceed.appzillonbanking.cagl.repository.cus;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.iexceed.appzillonbanking.cagl.entity.GkCustDemoData;
import com.iexceed.appzillonbanking.cagl.entity.GkCustDemoDataPK;



@Repository
public interface GkCustDemoDataRepository extends CrudRepository<GkCustDemoData, GkCustDemoDataPK> {
	
	@Query(value = "SELECT c FROM GkCustDemoData c" + " WHERE (:kendraId is null or c.kendraId=:kendraId) AND "
				+ "(:branchId is null or c.branchId=:branchId) AND (:groupId is null or c.groupId=:groupId)", nativeQuery = false)
	List<GkCustDemoData> findByKendraIdAndBranchIdAndGroupId(String kendraId, String branchId, String groupId);
	
}