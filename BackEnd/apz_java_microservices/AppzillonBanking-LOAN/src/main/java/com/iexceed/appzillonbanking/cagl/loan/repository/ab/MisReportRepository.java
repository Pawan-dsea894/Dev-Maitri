package com.iexceed.appzillonbanking.cagl.loan.repository.ab;


import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.iexceed.appzillonbanking.cagl.loan.domain.ab.MisReport;

@Repository
public interface MisReportRepository extends JpaRepository<MisReport, String>{

	Optional<MisReport> findByApplicationId(String applicationId);

}
