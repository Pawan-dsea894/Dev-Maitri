package com.iexceed.appzillonbanking.kyc.repository.cus;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.iexceed.appzillonbanking.kyc.entity.ExternalSystemAuditLogs;

@Repository
public interface ExternalSystemAuditLogsRepository extends JpaRepository<ExternalSystemAuditLogs, Integer> {

}
