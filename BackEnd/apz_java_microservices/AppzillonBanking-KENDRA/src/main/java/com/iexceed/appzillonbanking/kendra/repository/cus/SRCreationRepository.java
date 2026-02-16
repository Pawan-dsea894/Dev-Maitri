package com.iexceed.appzillonbanking.kendra.repository.cus;

import java.util.Optional;
import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.iexceed.appzillonbanking.kendra.domain.cus.SRCreation;
import com.iexceed.appzillonbanking.kendra.domain.cus.SRCreationId;


@Repository
public interface SRCreationRepository extends CrudRepository<SRCreation, SRCreationId> {

	Optional<SRCreation> findByCurrRoleAndCreateBy(String currRole, String createBy);
	
	Optional<SRCreation> findByCurrRoleAndCreateByAndApplicationIdAndSrType(String currRole, String createBy, String applicationId, String srType);
	
	Optional<SRCreation> findByCreateByAndApplicationIdAndSrType(String createBy, String applicationId, String srType);
	
	Optional<SRCreation> findByCurrRoleAndApplicationIdAndSrType(String currRole, String applicationId, String srType);
	
	Optional<SRCreation> findByApplicationIdAndSrType(String applicationId, String srType);
	
	Optional<List<SRCreation>> findByBranchId(String branchId);

	Optional<List<SRCreation>> findByCreateBy(String createBy);

	@Query(value = "SELECT * FROM tb_uasr_application WHERE next_role = 'AM' and branch_id IN (:branchDataOfAm); ", nativeQuery = true)
	List<SRCreation> findAllAMRecords(List<String> branchDataOfAm);

}
