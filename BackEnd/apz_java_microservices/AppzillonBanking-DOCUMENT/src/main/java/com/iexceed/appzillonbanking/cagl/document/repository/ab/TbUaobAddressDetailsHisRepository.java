package com.iexceed.appzillonbanking.cagl.document.repository.ab;

import com.iexceed.appzillonbanking.cagl.document.domain.ab.TbUaobAddressDetailsHis;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TbUaobAddressDetailsHisRepository extends CrudRepository<TbUaobAddressDetailsHis, String > {

    List<TbUaobAddressDetailsHis> findByApplicationId(String applicationId);

}
