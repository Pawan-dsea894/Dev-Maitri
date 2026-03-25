package com.iexceed.appzillonbanking.cagl.loan.repository.ab;

import java.util.List;
import java.util.Map;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.iexceed.appzillonbanking.cagl.loan.domain.ab.GkCustInfo;

public interface GkCustInfoRepository extends JpaRepository<GkCustInfo, String> {

	@Query(value = """
			SELECT *
			FROM gk_cust_info
			WHERE
			custid IN (:custids)
			OR kendraid IN (:kendrids)
			OR branchid IN (:branchids)
			""", nativeQuery = true)
	List<Map<String, Object>> fetchCustInfo(@Param("custids") List<String> custids,
			@Param("kendrids") List<String> kendrids, @Param("branchids") List<String> branchids);
}
