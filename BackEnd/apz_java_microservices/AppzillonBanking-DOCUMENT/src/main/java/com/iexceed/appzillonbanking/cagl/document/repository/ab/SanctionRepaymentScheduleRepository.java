package com.iexceed.appzillonbanking.cagl.document.repository.ab;

import com.iexceed.appzillonbanking.cagl.document.domain.ab.SanctionRepaymentSchedule;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SanctionRepaymentScheduleRepository extends CrudRepository<SanctionRepaymentSchedule, Long>{

	Optional<SanctionRepaymentSchedule> findTopByApplicationIdOrderByCreateTsDesc(String applicationId);

	@Transactional
	void deleteByApplicationId(String applicaitonID);
	
	@Query("SELECT s FROM SanctionRepaymentSchedule s WHERE s.applicationId = :applicationId ORDER BY s.createTs")
    List<SanctionRepaymentSchedule> findByApplicationId(@Param("applicationId") String applicationId);
	
}

