package com.iexceed.appzillonbanking.cagl.collection.repository.ab;

import java.util.List;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.iexceed.appzillonbanking.cagl.collection.domain.ab.TbUacoDeathIntimationDtls;
import com.iexceed.appzillonbanking.cagl.collection.domain.ab.TbUacoDeathIntimationDtlsId;

@Repository
public interface TbUacoDeathIntimationRepository
		extends CrudRepository<TbUacoDeathIntimationDtls, TbUacoDeathIntimationDtlsId> {
	
	List<TbUacoDeathIntimationDtls> findByAppIdAndApplicationIdIn(String appId, List<String> applicationId);
}
