package com.iexceed.appzillonbanking.cagl.repository.cus;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import com.iexceed.appzillonbanking.cagl.entity.KendraLatLongEntity;
import com.iexceed.appzillonbanking.cagl.payload.KendraIdsLatLongProjection;

public interface KendrLatLongRepo extends JpaRepository<KendraLatLongEntity, Integer> {

	@Query(value = "SELECT k.KendraID AS kendraId, k.Lat AS lat, k.longit AS longit "
			+ "FROM kendra_latlong k WHERE k.KendraID IN (:kendraIds)", nativeQuery = true)
	List<KendraIdsLatLongProjection> findAllLatLongBasedOnKendraIds(@Param("kendraIds") List<Integer> kendraIds);

}
