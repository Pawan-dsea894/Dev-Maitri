package com.iexceed.appzillonbanking.cagl.collection.repository.ab;

import com.iexceed.appzillonbanking.cagl.collection.domain.ab.TbUalnLoanDtls;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface TbUalnLoanDtlsRepository extends JpaRepository<TbUalnLoanDtls, String> {

    @Query("SELECT t.payload FROM TbUalnLoanDtls t WHERE t.applicationId = :applicationId")
    String findPayloadByApplicationId(@Param("applicationId") String applicationId);
}
