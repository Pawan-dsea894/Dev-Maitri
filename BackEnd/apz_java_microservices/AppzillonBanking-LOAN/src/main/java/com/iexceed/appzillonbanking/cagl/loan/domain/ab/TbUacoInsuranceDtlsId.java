package com.iexceed.appzillonbanking.cagl.loan.domain.ab;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.persistence.Column;

public class TbUacoInsuranceDtlsId  implements Serializable {

	private static final long serialVersionUID = 1L;

	@JsonProperty("insuranceDtlId")
	@Column(name = "INSURANCE_DTL_ID")
	private String insuranceDtlId;

}
