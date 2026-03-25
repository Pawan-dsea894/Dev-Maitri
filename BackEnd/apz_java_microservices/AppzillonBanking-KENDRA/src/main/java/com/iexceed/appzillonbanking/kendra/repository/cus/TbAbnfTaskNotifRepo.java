package com.iexceed.appzillonbanking.kendra.repository.cus;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.iexceed.appzillonbanking.kendra.domain.cus.TbAbnfTaskNotif;

@Repository
public interface TbAbnfTaskNotifRepo extends JpaRepository<TbAbnfTaskNotif, Long> {

	List<TbAbnfTaskNotif> findByToUserIdAndToUserRoleAndBranchIdAndApplicationDate(String toUserId, String toUserRole,
			String branchId, LocalDate applicationDate);

	Optional<TbAbnfTaskNotif> findTopByToUserRoleAndBranchIdAndApplicationDateOrderByUpdatedTsDesc(String toUserRole,
			String branchId, LocalDate applicationDate);

}