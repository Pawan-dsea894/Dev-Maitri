package com.iexceed.appzillonbanking.cagl.loan.repository.ab;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.iexceed.appzillonbanking.cagl.loan.domain.ab.SanctionRepaymentSchedule;

import jakarta.transaction.Transactional;

@Repository
public interface SanctionRepaymentScheduleRepository extends CrudRepository<SanctionRepaymentSchedule, Long>{

	Optional<SanctionRepaymentSchedule> findTopByApplicationIdOrderByCreateTsDesc(String applicationId);

	@Transactional
	void deleteByApplicationId(String applicaitonID);
	
	@Query("SELECT s FROM SanctionRepaymentSchedule s WHERE s.applicationId = :applicationId ORDER BY s.createTs")
    List<SanctionRepaymentSchedule> findByApplicationId(@Param("applicationId") String applicationId);
	
}

