package com.iexceed.appzillonbanking.core.repository.ab;

import org.springframework.data.repository.CrudRepository;
import com.iexceed.appzillonbanking.core.domain.ab.TbAbmiUdeMaster;
import com.iexceed.appzillonbanking.core.domain.ab.TbAbmiUdeMasterId;
import jakarta.transaction.Transactional;

public interface TbAbmiUdeMasterRepository extends CrudRepository<TbAbmiUdeMaster, TbAbmiUdeMasterId> {

	@Transactional
	void deleteByAppIdAndModule(String appId, String module);	
}