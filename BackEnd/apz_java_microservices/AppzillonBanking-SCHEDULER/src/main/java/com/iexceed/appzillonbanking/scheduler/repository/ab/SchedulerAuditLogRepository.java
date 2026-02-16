package com.iexceed.appzillonbanking.scheduler.repository.ab;

import com.iexceed.appzillonbanking.scheduler.domain.ab.SchedulerAuditLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SchedulerAuditLogRepository extends JpaRepository<SchedulerAuditLog, Long> {

}
