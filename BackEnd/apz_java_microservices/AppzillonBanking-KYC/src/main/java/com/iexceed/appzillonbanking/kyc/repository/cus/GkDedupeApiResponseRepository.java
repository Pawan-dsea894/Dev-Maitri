package com.iexceed.appzillonbanking.kyc.repository.cus;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.iexceed.appzillonbanking.kyc.entity.GkDedupeApiResponseData;



@Repository
public interface GkDedupeApiResponseRepository extends CrudRepository<GkDedupeApiResponseData, String> {
	
}