package com.iexceed.appzillonbanking.cagl.document.repository.ab;

import com.iexceed.appzillonbanking.cagl.document.domain.ab.TbOfficeData;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;


@Repository
public interface TbOfficeDataRepository extends CrudRepository<TbOfficeData, String> {
	//Optional<TbOfficeData> findByBranchid( String branchid);
	
	@Query(value = "select t.branch_lan from T24_OFFICE t where t.branchid =:branchid ", nativeQuery = true)
	String findBranchLan(@Param("branchid") String branchid);
	
}
