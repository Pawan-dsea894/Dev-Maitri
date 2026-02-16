package com.iexceed.appzillonbanking.cagl.loan.repository.ab;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.iexceed.appzillonbanking.cagl.loan.domain.ab.EmiProductList;

@Repository
public interface EmiProductListRepository extends JpaRepository<EmiProductList, String> {

}
