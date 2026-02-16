package com.iexceed.appzillonbanking.cagl.repository.cus;

import org.springframework.data.jpa.repository.JpaRepository;

import com.iexceed.appzillonbanking.cagl.domain.cus.GkMLoanProduct;

public interface LoanProductRepo extends JpaRepository<GkMLoanProduct, String> {

}
