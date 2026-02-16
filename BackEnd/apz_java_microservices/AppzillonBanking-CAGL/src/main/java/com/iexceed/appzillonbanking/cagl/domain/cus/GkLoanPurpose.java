package com.iexceed.appzillonbanking.cagl.domain.cus;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "gk_m_product_purpose_mapping")
public class GkLoanPurpose {
	
	@Column(name = "PRODUCT_ID")
	private String productId;
	
	@Column(name = "PURPOSE")
	@Id
	private String purpose;
	
	@Column(name = "PURPOSE_DESCRIPTION")
	private String purposeDesc;
	
	

}
