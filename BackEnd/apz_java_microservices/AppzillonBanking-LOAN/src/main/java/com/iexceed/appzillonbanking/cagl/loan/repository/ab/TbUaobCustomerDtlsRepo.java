package com.iexceed.appzillonbanking.cagl.loan.repository.ab;

import java.util.Optional;

import org.springframework.data.repository.CrudRepository;

import com.iexceed.appzillonbanking.cagl.loan.domain.ab.TbUaobCustDtls;
import com.iexceed.appzillonbanking.cagl.loan.domain.ab.TbUaobCustDtlsId;

public interface TbUaobCustomerDtlsRepo extends CrudRepository<TbUaobCustDtls, TbUaobCustDtlsId>{

	
    Optional<TbUaobCustDtls> findByApplicationId(String applicationId);
    
    Optional<TbUaobCustDtls> findByApplicationIdOrderByVersionNoDesc(String applicationId);

}
