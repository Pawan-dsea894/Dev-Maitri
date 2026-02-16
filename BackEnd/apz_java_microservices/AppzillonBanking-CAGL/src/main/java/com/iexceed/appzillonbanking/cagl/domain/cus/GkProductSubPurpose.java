package com.iexceed.appzillonbanking.cagl.domain.cus;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "gk_m_purpose_subpurpose_mapping")
@IdClass(ProductSubPurposePK.class)
public class GkProductSubPurpose {
	
	@Column(name = "PRODUCT_ID")
	@Id
	private String productId;
	
	@Column(name = "PURPOSE")
	@Id
	private String purposeId;
	
	@Column(name = "SUB_PURPOSE")
	@Id
	private String subPurpose;

}
