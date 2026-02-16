package com.iexceed.appzillonbanking.cagl.document.repository.ab;

import com.iexceed.appzillonbanking.cagl.document.domain.ab.TbUaobCustDtlsHis;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TbUaobCustomerDtlsHisRepo extends CrudRepository<TbUaobCustDtlsHis, String> {

    Optional<TbUaobCustDtlsHis> findByApplicationId(String applicationId);

}

