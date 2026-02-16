package com.iexceed.appzillonbanking.cagl.loan.repository.ab;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.iexceed.appzillonbanking.cagl.loan.domain.ab.TbAsmiUser;
import com.iexceed.appzillonbanking.cagl.loan.domain.ab.TbAsmiUserId;

public interface TbAsmiUserRepo extends JpaRepository<TbAsmiUser, TbAsmiUserId> {

	public Optional<TbAsmiUser> findByUserId(String userId);

	@Query(value = "select * from tb_asmi_user where user_id =:createdBy", nativeQuery = true)
	public Optional<TbAsmiUser> findBmNameAndGkId(String createdBy);
	
	@Query(value = "select a.ADD_INFO1 from TB_ASMI_USER a WHERE a.USER_ID =:userId",nativeQuery = true)
	String findbyUserRole(@Param("userId") String userId);

}
