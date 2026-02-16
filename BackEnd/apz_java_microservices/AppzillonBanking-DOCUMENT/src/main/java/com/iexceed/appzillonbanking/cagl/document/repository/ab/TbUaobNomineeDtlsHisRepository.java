package com.iexceed.appzillonbanking.cagl.document.repository.ab;

import com.iexceed.appzillonbanking.cagl.document.domain.ab.TbUaobNomineeDetailsHis;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface TbUaobNomineeDtlsHisRepository extends CrudRepository<TbUaobNomineeDetailsHis, String> {

    Optional<TbUaobNomineeDetailsHis> findByApplicationId(String applicationIdRef);
}
