package com.iexceed.appzillonbanking.cagl.repository.cus;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.iexceed.appzillonbanking.cagl.domain.cus.GkProductSubPurpose;
import com.iexceed.appzillonbanking.cagl.domain.cus.ProductSubPurposePK;

public interface ProductSubPurposeRepo extends JpaRepository<GkProductSubPurpose, ProductSubPurposePK> {
	
	public List<GkProductSubPurpose> findByProductIdAndPurposeId(String prodId,String purpId);

}
