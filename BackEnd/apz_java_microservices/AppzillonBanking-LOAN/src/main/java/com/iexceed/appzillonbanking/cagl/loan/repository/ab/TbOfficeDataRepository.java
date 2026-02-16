package com.iexceed.appzillonbanking.cagl.loan.repository.ab;

import java.util.Optional;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;


import com.iexceed.appzillonbanking.cagl.loan.domain.ab.TbOfficeData;
import com.iexceed.appzillonbanking.cagl.loan.domain.ab.TbOfficeDataId;


@Repository
public interface TbOfficeDataRepository extends CrudRepository<TbOfficeData, TbOfficeDataId> {
	//Optional<TbOfficeData> findByBranchid( String branchid);
	
	@Query(value = "select t.branch_lan from T24_OFFICE t where t.branchid =:branchid ", nativeQuery = true)
	String findBranchLan(@Param("branchid") String branchid);
	
}
