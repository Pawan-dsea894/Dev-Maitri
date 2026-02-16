package com.iexceed.appzillonbanking.cagl.loan.repository.ab;

import java.util.Optional;

import org.springframework.data.repository.CrudRepository;

import com.iexceed.appzillonbanking.cagl.loan.domain.ab.TbUaobNomineeDetails;
import com.iexceed.appzillonbanking.cagl.loan.domain.ab.TbUaobNomineeDetailsId;

public interface TbUaobNomineeDtlsRepository extends CrudRepository<TbUaobNomineeDetails, TbUaobNomineeDetailsId>{

	Optional<TbUaobNomineeDetails> findByApplicationId(String applicationIdRef);

}
