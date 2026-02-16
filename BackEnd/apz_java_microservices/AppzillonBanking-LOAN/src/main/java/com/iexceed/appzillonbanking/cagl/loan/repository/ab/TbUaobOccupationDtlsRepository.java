package com.iexceed.appzillonbanking.cagl.loan.repository.ab;

import java.util.Optional;

import org.springframework.data.repository.CrudRepository;

import com.iexceed.appzillonbanking.cagl.loan.domain.ab.TbUaobOccptDtlsId;
import com.iexceed.appzillonbanking.cagl.loan.domain.ab.TbUaobOccupationDtls;

public interface TbUaobOccupationDtlsRepository extends CrudRepository<TbUaobOccupationDtls, TbUaobOccptDtlsId>{

	Optional<TbUaobOccupationDtls> findByApplicationId(String applicationId);
}
