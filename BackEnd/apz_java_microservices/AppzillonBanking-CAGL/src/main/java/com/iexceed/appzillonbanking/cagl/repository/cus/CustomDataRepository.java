package com.iexceed.appzillonbanking.cagl.repository.cus;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.iexceed.appzillonbanking.cagl.entity.GkKendraData;

@Repository
public interface CustomDataRepository extends CrudRepository<GkKendraData, Integer> {

	
	/*
	 * @Query("SELECT new com.iexceed.appzillonbanking.cagl.payload.CustomResponseDTO("
	 * + "new com.iexceed.appzillonbanking.cagl.payload.ApiResponse(" +
	 * "new com.iexceed.appzillonbanking.cagl.payload.ResponseHeader('0', ''), " +
	 * "new com.iexceed.appzillonbanking.cagl.payload.ResponseBody(k, cList))) " +
	 * "FROM GkKendraData k " +
	 * "LEFT JOIN GkCustDemoData cList ON k.kendraId = cList.kendraId " +
	 * "WHERE k.kendraId = :kendraId") CustomResponseDTO
	 * fetchCustomData(@Param("kendraId") int kendraId);
	 */
	 
		/*
		 * @Query(value = "SELECT k.*, c.* " + "FROM gk_kendra_data k " +
		 * "JOIN gk_cust_demo_data c ON k.kendraId = c.kendraId " +
		 * "WHERE k.kendraId = :kendraId", nativeQuery = true) List<Object[]>
		 * fetchCustomData(int kendraId);
		 */

	  @Query("SELECT k, c FROM GkKendraData k LEFT JOIN GkCustDemoData c ON k.kendraId = c.kendraId WHERE k.kendraId = :kendraId")
	    List<Object[]> fetchCustomData(@Param("kendraId") int kendraId);

}
