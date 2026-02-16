package com.iexceed.appzillonbanking.cagl.loan.repository.ab;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import com.iexceed.appzillonbanking.cagl.loan.domain.ab.BusinessTarget;
import com.iexceed.appzillonbanking.cagl.loan.domain.ab.BusinessTargetId;

@Repository
public interface BusinessTargetRepository extends JpaRepository<BusinessTarget, BusinessTargetId> {

	@Query(value = "SELECT * FROM tb_uabt_business_target WHERE branch_id = :branch_id OR userid = :userid", nativeQuery = true)
	List<BusinessTarget> fetchTargetDetails(@Param("branch_id") String branchId, @Param("userid") String userId);

}
