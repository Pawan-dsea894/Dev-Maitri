package com.iexceed.appzillonbanking.cbs.repository.ab;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.iexceed.appzillonbanking.cbs.domain.ab.MisReport;

@Repository
public interface MisReportRepository extends JpaRepository<MisReport, String> {

	Optional<MisReport> findByApplicationId(String applicationId);

}
