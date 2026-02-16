package com.iexceed.appzillonbanking.cagl.domain.cus;

import java.io.Serializable;

import jakarta.persistence.Column;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProductSubPurposePK implements Serializable {

	private static final long serialVersionUID = 1L;

	@Column(name = "PRODUCT_ID")
	private String productId;
	
	@Column(name = "PURPOSE")
	private String purposeId;
	
	@Column(name = "SUB_PURPOSE")
	private String subPurpose;
}
