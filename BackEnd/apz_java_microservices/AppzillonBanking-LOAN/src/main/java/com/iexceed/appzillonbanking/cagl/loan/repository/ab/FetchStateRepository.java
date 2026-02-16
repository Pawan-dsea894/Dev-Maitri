package com.iexceed.appzillonbanking.cagl.loan.repository.ab;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.iexceed.appzillonbanking.cagl.loan.domain.ab.FetchStateEntity;

@Repository
public interface FetchStateRepository extends JpaRepository<FetchStateEntity, String> {
	
	FetchStateEntity findByCode(String code);
	@Query("SELECT c.description,code FROM FetchStateEntity c WHERE c.keyvalue = 'STATE'")
	List<String> findDistinctKeyvalues();

	@Query(value = "SELECT DISTINCT description FROM tb_ascd_common_codes WHERE keyvalue = :code", nativeQuery = true)
	List<String> findDescriptionsByKeyvalue(@Param("code") String code);

}
