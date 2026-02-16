package com.iexceed.appzillonbanking.cagl.collection.domain.ab;

import java.io.Serializable;

import jakarta.persistence.Column;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TbUacoQRDtlsId implements Serializable {

	private static final long serialVersionUID = 1L;

	@Column(name = "app_id", nullable = false)
	private String appId;

	@Column(name = "customer_id", nullable = false)
	private String customerId;

	@Column(name = "bill_number", nullable = false)
	private String billNumber;

}