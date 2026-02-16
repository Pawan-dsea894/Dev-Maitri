package com.iexceed.appzillonbanking.cagl.loan.bulkupload.repository.ab;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.iexceed.appzillonbanking.cagl.loan.bulkupload.domain.ab.BulkRecordsEntity;

@Repository
public interface BulkRecordsRepo extends CrudRepository<BulkRecordsEntity, Long>{

//	@Query(value = "SELECT loan_id, status from public.tb_bulk_records where doc_id=:docId", nativeQuery = true)
	Optional<List<BulkRecordsEntity>> findByDocId(@Param("docId") String docId);

}
