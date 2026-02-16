package com.iexceed.appzillonbanking.cagl.loan.domain.ab;


import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "tb_EmiProductList")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class EmiProductList {

	@Id
	@JsonProperty("NewProductCode")
	@Column(name = "NewProductCode")
	private String newProductCode;

	@JsonProperty("Product_Name")
	@Column(name = "Product_Name")
	private String product_Name;

	@JsonProperty("Loan_Amount")
	@Column(name = "Loan_Amount")
	private String loan_Amount;

	@JsonProperty("Tenure_W")
	@Column(name = "Tenure_W")
	private String tenure_W;

	@JsonProperty("Vintage")
	@Column(name = "Vintage")
	private String vintage;

	@JsonProperty("ROI")
	@Column(name = "ROI")
	private String rOI;

	@JsonProperty("Frequency")
	@Column(name = "Frequency")
	private String frequency;

	@JsonProperty("Tenure_M")
	@Column(name = "Tenure_M")
	private String tenure_M;

	@JsonProperty("Instalments")
	@Column(name = "Instalments")
	private String instalments;

	@JsonProperty("emi")
	@Column(name = "emi")
	private String emi;

	@JsonProperty("EMI_Amount")
	@Column(name = "EMI_Amount")
	private String eMI_Amount;

	@JsonProperty("New_Product_Code")
	@Column(name = "New_Product_Code")
	private String new_Product_Code;

	@JsonProperty("VintageNew")
	@Column(name = "VintageNew")
	private String vintageNew;

	@JsonProperty("Min")
	@Column(name = "Min")
	private String min;

	@JsonProperty("Max")
	@Column(name = "Max")
	private String max;

}
