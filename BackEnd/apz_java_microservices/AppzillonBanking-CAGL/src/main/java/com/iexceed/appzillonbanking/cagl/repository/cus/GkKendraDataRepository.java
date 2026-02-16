package com.iexceed.appzillonbanking.cagl.repository.cus;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.iexceed.appzillonbanking.cagl.entity.GkKendraData;



@Repository
public interface GkKendraDataRepository extends CrudRepository<GkKendraData, Integer> {

	/*
	 * @Query(value = "SELECT k FROM DemoTbKendra k" +
	 * " WHERE (:kendraId is null or k.kendraId=:kendraId)", nativeQuery = false)
	 * List<DemoTbKendra> findKendraDetails(int kendraId);
	 */
}
