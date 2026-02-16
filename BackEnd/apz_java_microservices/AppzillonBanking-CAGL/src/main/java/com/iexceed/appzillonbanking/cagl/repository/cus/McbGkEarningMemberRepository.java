package com.iexceed.appzillonbanking.cagl.repository.cus;

import java.util.List;

import org.springframework.data.repository.CrudRepository;

import com.iexceed.appzillonbanking.cagl.domain.cus.McbGkEarningMember;
import com.iexceed.appzillonbanking.cagl.domain.cus.McbGkEarningMemberPK;

public interface McbGkEarningMemberRepository extends CrudRepository<McbGkEarningMember, McbGkEarningMemberPK>{
	
	List<McbGkEarningMember> findByCustomerId(String customerId);

}
