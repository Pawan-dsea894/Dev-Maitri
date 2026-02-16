package com.iexceed.appzillonbanking.cagl.incomeassesment.domain.ab;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.persistence.Column;

public class TbUaobOccptDtlsId implements Serializable {

	private static final long serialVersionUID = 1L;

	@JsonProperty("occuPtDtlsId")
	@Column(name = "OCCPT_DTLS_ID")
	private String occuPtDtlsId;
}
