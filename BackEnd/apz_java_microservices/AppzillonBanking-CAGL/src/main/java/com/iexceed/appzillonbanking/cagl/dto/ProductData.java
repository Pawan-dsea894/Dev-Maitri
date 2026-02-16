package com.iexceed.appzillonbanking.cagl.dto;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProductData {
	
	private String productId;
	private String description;
	private String productType;
	private String shortDesc;
	private String amountLimit;
	private String amountMin;
	private String amountMax;
	private String amountDefault;
	private String spouseInsurance;
	private String insLnamount;
	private String disbOTP; 
	private String freq;
	private String insuranceProvider;
	private int insurancePercentage;
	private String term;
	private String productStatus;
	private String loanProdType;
	private String disbursementType;
	private String memInsurance;
	private String feeCharge;
	private String gst;
	private String consentType;
	private String payload;
	private List<ProductPurpose> prodPurpose;
	
}
