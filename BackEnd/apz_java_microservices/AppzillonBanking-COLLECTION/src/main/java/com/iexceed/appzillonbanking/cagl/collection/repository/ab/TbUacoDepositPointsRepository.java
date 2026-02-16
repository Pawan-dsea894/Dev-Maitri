package com.iexceed.appzillonbanking.cagl.collection.repository.ab;

import java.util.List;
import java.util.Optional;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.iexceed.appzillonbanking.cagl.collection.domain.ab.TbUacoDepositPointsDtls;
import com.iexceed.appzillonbanking.cagl.collection.domain.ab.TbUacoDepositPointsDtlsId;

@Repository
public interface TbUacoDepositPointsRepository extends CrudRepository<TbUacoDepositPointsDtls, TbUacoDepositPointsDtlsId> {
	
	List<TbUacoDepositPointsDtls> findByAppIdAndApplicationIdAndVersionNoOrderByVersionNoDesc(String appId,
			String applicationId, String versionNo);
	
	Optional<TbUacoDepositPointsDtls> findTopByAppIdAndRefNoAndVersionNoOrderByVersionNoDesc(String appId,
			String refNo, String versionNo);
}
