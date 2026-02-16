package com.iexceed.appzillonbanking.cagl.incomeassesment.domain.ab;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.persistence.Column;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TbUalnLoanDtlsId implements Serializable {

	private static final long serialVersionUID = 1L;

	@JsonProperty("loanDtlId")
	@Column(name = "LOAN_DTL_ID")
	private String loanDtlId;
}
