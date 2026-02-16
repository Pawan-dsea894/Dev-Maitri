package com.iexceed.appzillonbanking.cagl.loan.domain.ab;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.persistence.Column;

public class TbUaobCustDtlsId implements Serializable {

	private static final long serialVersionUID = 1L;

	@JsonProperty("custDtlId")
	@Column(name = "CUST_DTL_ID")
	private String custDtlId;
}
