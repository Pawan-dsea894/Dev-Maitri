package com.iexceed.appzillonbanking.cagl.repository.cus;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import com.iexceed.appzillonbanking.cagl.domain.cus.GkGroupData;
import com.iexceed.appzillonbanking.cagl.dto.GroupNameDto;


public interface GkGroupDataRepository extends CrudRepository<GkGroupData, Integer>{
	
	@Query(value = "SELECT groupId as groupId, name as name,KENDRAID as kendraId FROM gk_group_data WHERE groupId IN (:groupIds) AND name LIKE '%unnati%'", nativeQuery = true)
	List<GroupNameDto> getGroupNames(@Param("groupIds") List<Integer> groupIds);
	
}
