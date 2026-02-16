package com.iexceed.appzillonbanking.cagl.loan.bulkupload.repository.ab;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.iexceed.appzillonbanking.cagl.loan.bulkupload.domain.ab.BulkUploadEntity;

@Repository
public interface BulkUploadRepo extends CrudRepository<BulkUploadEntity, String>{

	Optional<List<BulkUploadEntity>> findByUserIdAndTypeOfUpload(String userId, String typeOfUpload);

	@Query(value = "SELECT * FROM public.tb_bulk_upload WHERE DATE(created_at) = CURRENT_DATE", nativeQuery = true)
	List<BulkUploadEntity> findAllTodaysRecords();

										

}
