package com.iexceed.appzillonbanking.kendra.repository.cus;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.iexceed.appzillonbanking.kendra.domain.cus.TbAsmiUser;
import com.iexceed.appzillonbanking.kendra.domain.cus.TbAsmiUserId;
import com.iexceed.appzillonbanking.kendra.domain.cus.TbAsmiUserRole;
import com.iexceed.appzillonbanking.kendra.payload.AsmiUserDetailsResponse;
import com.iexceed.appzillonbanking.kendra.payload.ResponseUserDetails;

public interface TbAsmiUserRepo extends JpaRepository<TbAsmiUser, TbAsmiUserId> {

	public Optional<TbAsmiUser> findByUserId(String userId);
	
	@Query(value = "SELECT a.ADD_INFO2 FROM TB_ASMI_USER a WHERE a.USER_ID = :USER_ID", nativeQuery = true)
	String fetchBranchId(@Param("USER_ID") String userId);
	
	@Query(value = "SELECT * FROM public.tb_asmi_user where user_id =:userId",nativeQuery = true)
	public Optional<TbAsmiUser> findByAmId(@Param("userId") String userId);
	 
	  @Query("SELECT new com.iexceed.appzillonbanking.kendra.payload.ResponseUserDetails(a.userId, a.userName, " +
		       "'{' || STRING_AGG(r.roleId, ', ') || '}') " +
		       "FROM TbAsmiUser a " +
		       "LEFT JOIN TbAsmiUserRole r ON r.userId = a.userId " +
		       "WHERE a.userActive = 'Y' AND a.addInfo2 = :addInfo2 " +
		       "GROUP BY a.userId, a.userName")
		List<ResponseUserDetails> findUsersDetailsByAddInfo2(@Param("addInfo2") String addInfo2);
	  
		/*
		 * @Query(value =
		 * "SELECT a.ADD_INFO2 and a.ADD_INFO1 FROM TB_ASMI_USER a WHERE a.USER_ID =:USER_ID"
		 * , nativeQuery = true) public Optional<TbAsmiUser>
		 * fetchUserDetails(@Param("USER_ID") String userId);
		 */
	    
	  @Query("SELECT new com.iexceed.appzillonbanking.kendra.payload.AsmiUserDetailsResponse(a.addInfo1, a.addInfo2) FROM TbAsmiUser a WHERE a.userId = :userId")
	  Optional<AsmiUserDetailsResponse> fetchUserDetails(@Param("userId") String userId);

}
