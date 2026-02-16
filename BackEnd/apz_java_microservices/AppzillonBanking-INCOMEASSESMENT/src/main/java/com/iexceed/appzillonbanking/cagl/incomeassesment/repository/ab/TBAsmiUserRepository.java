package com.iexceed.appzillonbanking.cagl.incomeassesment.repository.ab;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.iexceed.appzillonbanking.cagl.incomeassesment.domain.ab.TbAsmiUser;
import com.iexceed.appzillonbanking.cagl.incomeassesment.domain.ab.TbAsmiUserId;

public interface TBAsmiUserRepository extends JpaRepository<TbAsmiUser,TbAsmiUserId> {
	
	 @Query(value = "SELECT t.ADD_INFO2 FROM public.tb_asmi_user t WHERE t.ADD_INFO1 = 'RPC' AND t.USER_ID = :userId", nativeQuery = true)
	 String findAddInfo2ByAddInfo1AndUserId(@Param("userId") String userId);
}
