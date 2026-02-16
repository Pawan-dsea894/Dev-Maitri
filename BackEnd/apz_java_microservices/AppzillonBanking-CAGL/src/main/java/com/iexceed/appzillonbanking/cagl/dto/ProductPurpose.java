package com.iexceed.appzillonbanking.cagl.dto;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProductPurpose {

	private String productId;

	private String purpose;

	private String purposeDesc;
	
	private List<String> productSubPurpose;

}
