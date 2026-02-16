package com.iexceed.appzillonbanking.cagl.repository.cus;

import org.springframework.data.jpa.repository.JpaRepository;

import com.iexceed.appzillonbanking.cagl.entity.GkUserData;

public interface UserDetailsRepo extends JpaRepository<GkUserData, String> {

}
