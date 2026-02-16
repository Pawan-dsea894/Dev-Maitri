package com.iexceed.appzillonbanking.cagl.repository.cus;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.iexceed.appzillonbanking.cagl.domain.cus.GkEarningMember;

public interface EarningMemberRepository extends JpaRepository<GkEarningMember, String> {
	
	public List<GkEarningMember> findByCustomerId(String custId);

}
