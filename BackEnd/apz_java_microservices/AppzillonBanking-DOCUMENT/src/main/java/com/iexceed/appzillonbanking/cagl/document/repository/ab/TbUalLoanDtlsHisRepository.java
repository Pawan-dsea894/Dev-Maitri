package com.iexceed.appzillonbanking.cagl.document.repository.ab;

import com.iexceed.appzillonbanking.cagl.document.domain.ab.TbUalnLoanDtlsHis;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Repository
public interface TbUalLoanDtlsHisRepository extends CrudRepository<TbUalnLoanDtlsHis, String> {
    Optional<TbUalnLoanDtlsHis> findByApplicationId(String applicationId);

    @Modifying
    @Transactional
    @Query(value = "UPDATE TB_UALN_LOAN_DTLS_HISTORY SET INSTALLMENTDETAILS =:installmentDetails WHERE APPLICATION_ID =:applicationId", nativeQuery = true)
    int updateInstallmentDetails(@Param("installmentDetails") String installmentDetails,
                                 @Param("applicationId") String applicationId);

}
