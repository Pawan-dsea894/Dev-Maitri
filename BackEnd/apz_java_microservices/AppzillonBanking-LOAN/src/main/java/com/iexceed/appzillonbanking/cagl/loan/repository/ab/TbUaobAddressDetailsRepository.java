package com.iexceed.appzillonbanking.cagl.loan.repository.ab;

import java.util.List;

import org.springframework.data.repository.CrudRepository;

import com.iexceed.appzillonbanking.cagl.loan.domain.ab.TbUaobAddressDetails;
import com.iexceed.appzillonbanking.cagl.loan.domain.ab.TbUaobAddressDetailsId;

import jakarta.transaction.Transactional;

public interface TbUaobAddressDetailsRepository extends CrudRepository<TbUaobAddressDetails, TbUaobAddressDetailsId> {

	List<TbUaobAddressDetails> findByApplicationId(String applicationId);

	@Transactional
	void deleteByApplicationIdIn(List<String> applicationId);

}
