package com.iexceed.appzillonbanking.cagl.domain.cus;

import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "gk_m_loan_product")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class GkMLoanProduct {

	@Id
	@Column(name = "PRODUCT_ID")
	private String productId;
	
	@Column(name = "product_type")
	private String productType;

	@JsonProperty("description")
	@Column(name = "DESCRIPTION")
	private String description;

	@JsonProperty("shortDesc")
	@Column(name = "SHORT_DESCRIPTION")
	private String shortDesc;

	@JsonProperty("amountLimit")
	@Column(name = "AMOUNT_LIMIT")
	private String amountLimit;

	@JsonProperty("disbursementType")
	@Column(name = "DISBURSEMENT_TYPE")
	private String disbursementType;

	@JsonProperty("amountMin")
	@Column(name = "AMOUNT_MIN")
	private String amountMin;

	@JsonProperty("amountMax")
	@Column(name = "AMOUNT_MAX")
	private String amountMax;

	@JsonProperty("amountDefault")
	@Column(name = "AMOUNT_DEFAULT")
	private String amountDefault;

	@JsonProperty("amountMultiple")
	@Column(name = "AMOUNT_MULTIPLE")
	private String amountMultiple;

	@JsonProperty("spouseInsurance")
	@Column(name = "SPOUSE_INSURANCE")
	private String spouseInsurance;
	
	@JsonProperty("InsLnamount")
	@Column(name = "InsLnamount")
	private String insLnamount;

	@JsonProperty("DisbOTP")
	@Column(name = "disbOTP")
	private String disbOTP; 

	@JsonProperty("freq")
	@Column(name = "FREQ")
	private String freq;

	@JsonProperty("term")
	@Column(name = "TERM")
	private String term;

	@JsonProperty("minMemAge")
	@Column(name = "MIN_MEM_AGE")
	private String minMemAge;

	@JsonProperty("maxMemAge")
	@Column(name = "MAX_MEM_AGE")
	private String maxMemAge;

	@JsonProperty("osVal")
	@Column(name = "OS_VALIDATION")
	private String osVal;

	@JsonProperty("osValAmt")
	@Column(name = "OS_VAL_AMOUNT")
	private String osValAmt;

	@JsonProperty("year")
	@Column(name = "YEAR")
	private String year;

	@JsonProperty("insuProvider")
	@Column(name = "INSURANCE_PROVIDER")
	private String insuProvider;

	@JsonProperty("minSpAge")
	@Column(name = "MIN_SP_AGE")
	private String minSpAge;

	@JsonProperty("maxSpAge")
	@Column(name = "MAX_SP_AGE")
	private String maxSpAge;
	
	private String product_status;
	
	private String loan_prod_type;
	
	@JsonProperty("MEM_INSURANCE")
	@Column(name = "MEM_INSURANCE")
	private String memInsurance;
		
	@JsonProperty("feeCharge")
	@Column(name = "fee_charge")
	private String feeCharge;
		
	@JsonProperty("gst")
	@Column(name = "GST")
	private String gst;

	@JsonProperty("payload")
	@Column(name = "payload")
	private String payload;
	 
	@JsonProperty("consentType")
	@Column(name ="consent_type")
	private String consentType;
		
}
