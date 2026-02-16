package com.iexceed.appzillonbanking.kendra.repository.cus;

import java.sql.Date;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.iexceed.appzillonbanking.kendra.domain.cus.KendraAssignment;

@Repository
public interface KendraAssignmentRepository extends CrudRepository<KendraAssignment, String> {

	Optional<List<KendraAssignment>> findByKmId(String kmId);

	@Query(value = "SELECT * FROM public.tb_kendra_assignment WHERE branch_id =:branchId AND(remarks != 'DELETED' OR remarks IS NULL);", nativeQuery = true)
	Optional<List<KendraAssignment>> findByBranchId(String branchId);

	Optional<List<KendraAssignment>> findByBatchNo(String batchNo);

	Optional<List<KendraAssignment>> findByKmIdAndBatchNo(String kmId, String batchNo);

	@Query(value = "SELECT * FROM public.tb_kendra_assignment WHERE kmId = :kmId AND assignment_type = :assignmentType AND kendra_id = :kendraId AND ((start_date >= :startDate AND end_date >= :endDate) OR (start_date = :startDate AND end_date <= :endDate) OR (start_date >= :startDate AND end_date = :endDate) OR (start_date = :startDate AND end_date = :endDate) OR (start_date <= :startDate AND end_date >= :endDate) OR (start_date <= :startDate AND end_date <= :endDate))", nativeQuery = true)
	Optional<KendraAssignment> findByKmIdAndKendraIdAndStartDateAndEndDateBetween(String kmId, String assignmentType, String kendraId, Date startDate, Date endDate);


    @Modifying
    @Transactional
    @Query(value = "DELETE FROM public.tb_kendra_assignment WHERE kendra_id =:kendraId and kmid =:oldKmid", nativeQuery = true)
	void deleteByKendraIdAndkmId(String kendraId, String oldKmid);



}
