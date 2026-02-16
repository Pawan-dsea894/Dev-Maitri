package com.iexceed.appzillonbanking.cagl.collection.repository.ab;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.iexceed.appzillonbanking.cagl.collection.domain.ab.CollectionReport;
import com.iexceed.appzillonbanking.cagl.collection.domain.ab.CollectionReportId;
import java.util.List;
import java.util.Optional;


@Repository
public interface CollectionReportRepository extends JpaRepository<CollectionReport, CollectionReportId> {

	Optional<CollectionReport> findByApplicationIdAndKendraIdAndBranchIdAndVersionNum(String applicationId, String KendraId, String branchId, int versionNum);
}