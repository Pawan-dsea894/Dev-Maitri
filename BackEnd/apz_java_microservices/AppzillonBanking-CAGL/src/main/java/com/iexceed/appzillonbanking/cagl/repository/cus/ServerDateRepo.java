package com.iexceed.appzillonbanking.cagl.repository.cus;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.iexceed.appzillonbanking.cagl.entity.ServerDate;



@Repository
public interface ServerDateRepo extends JpaRepository<ServerDate,Integer>{

}
