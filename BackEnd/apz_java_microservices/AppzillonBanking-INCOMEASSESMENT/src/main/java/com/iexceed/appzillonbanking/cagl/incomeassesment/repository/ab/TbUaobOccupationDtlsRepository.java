package com.iexceed.appzillonbanking.cagl.incomeassesment.repository.ab;

import java.util.Optional;

import org.springframework.data.repository.CrudRepository;

import com.iexceed.appzillonbanking.cagl.incomeassesment.domain.ab.TbUaobOccptDtlsId;
import com.iexceed.appzillonbanking.cagl.incomeassesment.domain.ab.TbUaobOccupationDtls;


public interface TbUaobOccupationDtlsRepository extends CrudRepository<TbUaobOccupationDtls, TbUaobOccptDtlsId>{

	Optional<TbUaobOccupationDtls> findByApplicationId(String applicationId);
}
