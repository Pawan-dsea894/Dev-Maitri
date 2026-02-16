package com.iexceed.appzillonbanking.scheduler.repository.ab;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.iexceed.appzillonbanking.scheduler.domain.ab.MisReport;

public interface MisReportRepository extends JpaRepository<MisReport, String> {
	
	Optional<MisReport> findByApplicationId(String applicationId);

}
