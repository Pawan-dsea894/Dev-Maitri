package com.iexceed.appzillonbanking.cagl.loan.repository.ab;

import java.util.List;

import org.springframework.data.repository.CrudRepository;

import com.iexceed.appzillonbanking.cagl.loan.domain.ab.RoleAccessMap;
import com.iexceed.appzillonbanking.cagl.loan.domain.ab.RoleAccessMapId;

public interface RoleAccessMapRepository extends CrudRepository<RoleAccessMap, RoleAccessMapId> {
	List<RoleAccessMap> findByAppId(String appId);
}