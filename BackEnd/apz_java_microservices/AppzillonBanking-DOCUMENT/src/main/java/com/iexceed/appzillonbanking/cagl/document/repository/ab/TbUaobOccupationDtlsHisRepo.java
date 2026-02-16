package com.iexceed.appzillonbanking.cagl.document.repository.ab;


import com.iexceed.appzillonbanking.cagl.document.domain.ab.TbUaobOccupationDtlsHis;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TbUaobOccupationDtlsHisRepo extends CrudRepository<TbUaobOccupationDtlsHis, String> {
    Optional<TbUaobOccupationDtlsHis> findByApplicationId(String applicationId);

}
