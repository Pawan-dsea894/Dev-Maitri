package com.iexceed.appzillonbanking.cagl.repository.cus;

import org.springframework.data.jpa.repository.JpaRepository;

import com.iexceed.appzillonbanking.cagl.entity.BranchLatlong;

public interface LatLongRepo extends JpaRepository<BranchLatlong, String> {

}
