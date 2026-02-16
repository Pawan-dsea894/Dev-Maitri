package com.iexceed.appzillonbanking.cagl.document.repository.ab;

import com.iexceed.appzillonbanking.cagl.document.domain.ab.TbUacoInsuranceDtlsHis;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TbUacoInsuranceDtlsHisRepo extends CrudRepository<TbUacoInsuranceDtlsHis, String> {

    Optional<TbUacoInsuranceDtlsHis> findByApplicationId(String applicationId);}
