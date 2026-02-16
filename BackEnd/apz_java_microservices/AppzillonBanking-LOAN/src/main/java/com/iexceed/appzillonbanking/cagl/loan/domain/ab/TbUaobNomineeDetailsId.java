package com.iexceed.appzillonbanking.cagl.loan.domain.ab;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.persistence.Column;

public class TbUaobNomineeDetailsId implements Serializable {

	private static final long serialVersionUID = 1L;

	@JsonProperty("nomineeDtlsId")
	@Column(name = "NOMINEE_DTLS_ID")
	private String nomineeDtlsId;

}
