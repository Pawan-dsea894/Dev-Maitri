package com.iexceed.appzillonbanking.kendra.repository.cus;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.iexceed.appzillonbanking.kendra.domain.cus.ApplicationMaster;
import com.iexceed.appzillonbanking.kendra.domain.cus.ApplicationMasterId;

@Repository
public interface ApplicationMasterRepository extends CrudRepository<ApplicationMaster, ApplicationMasterId> {

	// Fetch distinct KMIDs where ADD_INFO contains DEO
	@Query(value = "SELECT DISTINCT KMID " + "FROM TB_UACO_APPLICATION_MASTER " + "WHERE BRANCH_CODE = :branchCode "
			+ "AND APPLICATION_DATE = :applicationDate " + "AND ADD_INFO LIKE '%DEO%'", nativeQuery = true)
	List<String> findDistinctKmIdsWithDeo(@Param("branchCode") String branchCode,
			@Param("applicationDate") LocalDate applicationDate);

	// Update REMARKS = ADD_INFO for given KMIDs
	@Modifying
	@Transactional
	@Query(value = "UPDATE TB_UACO_APPLICATION_MASTER " + "SET REMARKS = ADD_INFO " + "WHERE BRANCH_CODE = :branchCode "
			+ "AND APPLICATION_DATE = :applicationDate " + "AND KMID IN (:kmIds) "
			+ "AND ADD_INFO LIKE '%DEO%'", nativeQuery = true)
	int updateRemarksFromAddInfo(@Param("branchCode") String branchCode,
			@Param("applicationDate") LocalDate applicationDate, @Param("kmIds") List<String> kmIds);

	// Update CREATED_BY, KMID, and ADD_INFO = DEO~curDeoUserId
	@Modifying
	@Transactional
	@Query(value = "UPDATE TB_UACO_APPLICATION_MASTER " + "SET CREATED_BY = :curDeoUserId, "
			+ "    KMID = :curDeoUserId, " + "    ADD_INFO = CONCAT('DEO~', :curDeoUserId) "
			+ "WHERE BRANCH_CODE = :branchCode " + "AND APPLICATION_DATE = :applicationDate " + "AND KMID IN (:kmIds) "
			+ "AND ADD_INFO LIKE '%DEO%'", nativeQuery = true)
	int updateDeoRecords(@Param("curDeoUserId") String curDeoUserId, @Param("branchCode") String branchCode,
			@Param("applicationDate") LocalDate applicationDate, @Param("kmIds") List<String> kmIds);

	// Fetch Application IDs where ADD_INFO contains DEO
	@Query(value = "SELECT APPLICATION_ID " + "FROM TB_UACO_APPLICATION_MASTER " + "WHERE BRANCH_CODE = :branchCode "
			+ "AND APPLICATION_DATE = :applicationDate " + "AND ADD_INFO LIKE '%DEO%'", nativeQuery = true)
	List<String> findApplicationIdsWithDeo(@Param("branchCode") String branchCode,
			@Param("applicationDate") LocalDate applicationDate);
}
