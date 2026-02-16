package com.iexceed.appzillonbanking.cagl.loan.domain.ab;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.persistence.Column;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TbUaobAddressDetailsId implements Serializable {

	private static final long serialVersionUID = 1L;

	@JsonProperty("appId")
	@Column(name = "ADDRESS_DTLS_ID")
	private String addressDtlId;
}
