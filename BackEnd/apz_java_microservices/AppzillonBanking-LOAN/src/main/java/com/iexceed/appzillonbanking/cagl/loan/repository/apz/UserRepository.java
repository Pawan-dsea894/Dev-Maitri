package com.iexceed.appzillonbanking.cagl.loan.repository.apz;

import java.util.List;
import java.util.Optional;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.iexceed.appzillonbanking.cagl.loan.domain.apz.User;
import com.iexceed.appzillonbanking.cagl.loan.domain.apz.UserId;

@Repository
public interface UserRepository extends CrudRepository<User, UserId> {

	List<User> findByAddInfo1AndAddInfo2(String addInfo1, String addInfo2);

	 Optional<User> findByUserId(String string);
}
